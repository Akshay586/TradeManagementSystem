package com.trademanagement.test;

import com.trademanagement.DatabaseHandler;
import com.trademanagement.Trade;
import com.trademanagement.TradeException;
import org.hibernate.Session;
import org.hibernate.boot.model.relational.Database;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDatabaseHandler {

    Trade trade = null;
    DatabaseHandler databaseHandler=null;
    Date maturityDate = null;
    Date today = null;
    Session session = null;
    @Before
    public void setup(){

        try{
            databaseHandler = new DatabaseHandler();
            session = databaseHandler.generateDbSession();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            maturityDate = dateFormatter.parse("20/05/2021");
            today = dateFormatter.parse("18/03/2021");

            //T1,1,CP-1,B1,20/05/2020,18/03/2021,N
            trade = new Trade.Builder().tradeId("T1")
                    .version(2)
                    .counterPartyId("CP-1")
                    .bookId("B1")
                    .maturityDate(maturityDate)
                    .createdDate(today)
                    .expired('N')
                    .build();
        }
        catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testSaveOneTradeToDatabase(){
        try{


            databaseHandler.saveOneTradeToDatabase(session,trade);

            assert session.get(Trade.class,trade.getTradeId())!=null;
        }
        catch (TradeException e){
            e.printStackTrace();
        }

    }

    @Test
    public void testValidateTradeVersion(){
        try{
            Trade existingTrade = new Trade.Builder().tradeId("T1")
                    .version(1)
                    .counterPartyId("CP-1")
                    .bookId("B1")
                    .maturityDate(maturityDate)
                    .createdDate(today)
                    .expired('N')
                    .build();
            databaseHandler.saveOneTradeToDatabase(session, existingTrade);
            DatabaseHandler.TradeStatus status =databaseHandler.validateTradeVersion(existingTrade,trade);
            assert status == DatabaseHandler.TradeStatus.OVERRIDE_TRADE;
        } catch (TradeException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testValidateTradeVersionLowerVersion(){
        try{
            Trade existingTrade = new Trade.Builder().tradeId("T1")
                    .version(3)
                    .counterPartyId("CP-1")
                    .bookId("B1")
                    .maturityDate(maturityDate)
                    .createdDate(today)
                    .expired('N')
                    .build();
            databaseHandler.saveOneTradeToDatabase(session, existingTrade);
            DatabaseHandler.TradeStatus status =databaseHandler.validateTradeVersion(existingTrade,trade);
            assert status == DatabaseHandler.TradeStatus.LOWER_VERSION_TRADE;
        }
        catch (TradeException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testValidateMaturityDateOfTradeForDateLessThanPresent(){
        try{
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            Date passedMaturityDate = dateFormatter.parse("17/03/2021");
            trade.setMaturityDate(passedMaturityDate);
            DatabaseHandler.TradeStatus status =databaseHandler.validateMaturityDateOfTrade(trade);
            assert status == DatabaseHandler.TradeStatus.MATURITY_DATE_LESS_THAN_PRESENT;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
