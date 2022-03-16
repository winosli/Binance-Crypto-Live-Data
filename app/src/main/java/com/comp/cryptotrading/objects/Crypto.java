package com.comp.cryptotrading.objects;

public class Crypto {
    /*
    API in the link: https://binance-docs.github.io/apidocs/spot/en/#all-market-mini-tickers-stream
    Stream name: <symbol@ticker>
    Request API for all coins: wss://stream.binance.com:9443/ws/!ticker@arr
     */
    private String symbol; // "s"
    private String stockExchange; // Unknown - need to find it
    private String lastPrice; // "c"
    private String priceChangePercent; // "P" - in the last 24 hours
    private int totalNumberOfTrades; // "n"
    private String ask; // "a" - Best ask price
    private String bid; // "b" - Best bid price
    private String eventTime; // "E"

    // HashMap - Key -> Object |
    //
    // LinkedHashmap ->
    // List[i] update -> adapter notify
    // Diss Util
    public Crypto(String symbol, String stockExchange, String lastPrice, String priceChangePercent, int totalNumberOfTrades, String ask, String bid, String eventTime) {
        this.symbol = symbol;
        this.stockExchange = stockExchange;
        this.lastPrice = lastPrice;
        this.priceChangePercent = priceChangePercent;
        this.totalNumberOfTrades = totalNumberOfTrades;
        this.ask = ask;
        this.bid = bid;
        this.eventTime = eventTime;
    }


    public String getSymbol() {
        return symbol;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public String getPriceChangePercent() {
        return priceChangePercent;
    }

    public int getTotalNumberOfTrades() {
        return totalNumberOfTrades;
    }

    public String getAsk() {
        return ask;
    }

    public String getBid() {
        return bid;
    }

    public String getEventTime() {
        return eventTime;
    }
}
