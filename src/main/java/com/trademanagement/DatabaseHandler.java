package com.trademanagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DatabaseHandler {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public Session generateDbSession(){
        Session session = DatabaseHandler.getSessionFactory().openSession();
        return  session;
    }

    public int saveOneTradeToDatabase(Session currentSession, Trade trade) throws TradeException {
        Transaction transaction = null;
        try{
            transaction = currentSession.beginTransaction();
            Trade existingTrade = currentSession.get(Trade.class,trade.getTradeId());
            TradeStatus status = validateTradeVersion(existingTrade,trade);
            TradeStatus dateStatus = validateMaturityDateOfTrade(trade);


            if (dateStatus == TradeStatus.MATURITY_DATE_LESS_THAN_PRESENT){
                transaction.commit();
                throw new TradeException("Maturity date of the trade " +trade.getTradeId()+" "
                        +trade.getMaturityDate()+" is less than the current date");
            }

            if(dateStatus == TradeStatus.EXPIRED_TRADE){
                System.out.println("Trade "+trade.getTradeId()+" is expired");
                trade.setExpired('Y');
            }

            if (status==TradeStatus.LOWER_VERSION_TRADE){
                transaction.commit();
                throw new TradeException("Trade "+trade.getTradeId()+" rejected because a higher version of trade was found");
            }

            else if (status == TradeStatus.OVERRIDE_TRADE){
                System.out.println("Updating trade");
                currentSession.update(trade);
                transaction.commit();
                return 0;
            }
            else if (status == TradeStatus.VALID_TRADE){
                currentSession.save(trade);
                transaction.commit();
                return 0;
            }
            else{
                System.out.println("Trade status unrecognizable");
                transaction.commit();
                return 1;
            }
        }
        catch (Exception e){
            if (e instanceof TradeException){
                throw e;
            }
            if(transaction!=null){
                transaction.rollback();
            }
            e.printStackTrace();
            return 1;
        }

    }

    public TradeStatus validateMaturityDateOfTrade(Trade trade){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        try {
            Date date = new Date();
            String dateString = simpleDateFormat.format(date);
            Date today = simpleDateFormat.parse(dateString);
            if(trade.getMaturityDate().before(today)){
                return TradeStatus.MATURITY_DATE_LESS_THAN_PRESENT;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return TradeStatus.VALID_TRADE;
    }

    public TradeStatus validateTradeVersion(Trade existingTrade, Trade trade){
        if(existingTrade!=null){
            if(existingTrade.getVersion()>trade.getVersion()){
                return TradeStatus.LOWER_VERSION_TRADE;
            }
            else{
                return TradeStatus.OVERRIDE_TRADE;
                //currentSession.update(trade);
            }
        }
        else {
            return TradeStatus.VALID_TRADE;
        }
    }

    public void updateExpiryForTrades(){
        try (Session session = getSessionFactory().openSession()) {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            String dateString = simpleDateFormat.format(date);
            Date today = simpleDateFormat.parse(dateString);
            List<Trade> trades = session.createQuery("from Trade", Trade.class).list();
            trades.stream()
                    .filter(trade -> trade.getMaturityDate().before(today))
                    .forEach(trade -> {
                        trade.setExpired('Y');
                        Transaction transaction = session.beginTransaction();
                        try {
                            saveOneTradeToDatabase(session,trade);
                            transaction.commit();
                        } catch (TradeException e) {
                            if(transaction!=null){
                                transaction.rollback();
                            }
                            e.printStackTrace();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry
                registry = new StandardServiceRegistryBuilder().configure().build();

                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);

                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();

                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public enum TradeStatus{
        LOWER_VERSION_TRADE(1),
        OVERRIDE_TRADE(2),
        MATURITY_DATE_LESS_THAN_PRESENT(3),
        EXPIRED_TRADE(4),
        VALID_TRADE(0);

        private int tradeStatus;

        public int getTradeStatus(){
            return this.tradeStatus;
        }

        TradeStatus(int i) {
            this.tradeStatus = i;
        }
    }
}
