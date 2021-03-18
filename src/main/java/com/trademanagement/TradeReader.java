package com.trademanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

//utility class to read trades from a text file
public class TradeReader {

    public ArrayList<Trade> readTradesFromFile(String fileName){
        String line="";
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        ArrayList<Trade> trades = new ArrayList<>();
        //read trades from the file
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            while((line = reader.readLine())!=null){
                String[] tradeLine = line.split(",");
                Trade oneTrade = new Trade.Builder()
                        .tradeId(tradeLine[0])
                        .version(Integer.parseInt(tradeLine[1]))
                        .counterPartyId(tradeLine[2])
                        .bookId(tradeLine[3])
                        .maturityDate(dateFormatter.parse(tradeLine[4]))
                        .createdDate(dateFormatter.parse(tradeLine[5]))
                        .expired(tradeLine[6].charAt(0))
                        .build();

                trades.add(oneTrade);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return trades;
    }
}
