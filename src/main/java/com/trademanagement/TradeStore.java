package com.trademanagement;

import org.hibernate.Session;

import java.util.ArrayList;

public class TradeStore {

    //providing a default runner for the necessary functionality
    //as a generic test
    //this will create a list of trades from a text file
    //and then save each of those to hsql database
    public static void main(String[] args) {
        try{
            TradeReader reader = new TradeReader();
            ArrayList<Trade> tradeList = reader.readTradesFromFile("trades.txt");

            DatabaseHandler databaseHandler = new DatabaseHandler();
            Session session = databaseHandler.generateDbSession();

            tradeList.stream().forEach(trade -> {
                try {
                    databaseHandler.saveOneTradeToDatabase(session,trade);
                } catch (TradeException e) {
                    System.out.println(e.getMessage());
                }
            });

            databaseHandler.updateExpiryForTrades();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            DatabaseHandler.shutdown();
        }

    }



}
