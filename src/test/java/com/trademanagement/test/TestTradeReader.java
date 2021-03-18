package com.trademanagement.test;

import com.trademanagement.Trade;
import com.trademanagement.TradeReader;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.util.ArrayList;

public class TestTradeReader {

    @Test
    public void testReadTradesFromFile(){
        ArrayList<Trade> testTradeList = new TradeReader().readTradesFromFile("trades.txt");
        assert CollectionUtils.isNotEmpty(testTradeList);
    }
}
