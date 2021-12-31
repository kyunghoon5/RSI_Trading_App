package com_group_cs370_fall21_tradingapp.github.rsitradingapp.models;

import java.util.ArrayList;

public class Simulator {

    private ArrayList<String> transactions;
    private int savedCapital;
    private int simulatedCapital;
    private double simulatedWorth;
    private User user;
    private Stock stock;
    private int startingAmount;
    private double buyThreshold;
    private double sellThreshold;
    private int buyAmount;
    private int sellAmount;

    public Simulator(User user, Stock stock, double buyThreshold, double sellThreshold, int buyAmount, int sellAmount){
        this.transactions = new ArrayList<String>();
        this.user = user;
        this.stock = stock;
        this.startingAmount = stock.getNumOwned();
        this.savedCapital = user.getCapital();
        this.buyThreshold = buyThreshold;
        this.sellThreshold = sellThreshold;
        this.buyAmount = buyAmount;
        this.sellAmount = sellAmount;

    }
    public void runSimulation(){

        for (int i = stock.getNumPrices() - 14; i >= 0; i--){
            double rsi = stock.calculateRSI1(i, i + 14);
            if (rsi <= buyThreshold){
                int transaction = 0;
                for (int j = 0; j < buyAmount; j++){
                    int result = user.buyStock(stock, stock.getHistoricPrices()[i] * 100.0);
                    if (result > 0){
                        transaction += result;
                    }
                }
                if (transaction > 0){
                    String output = stock.getHistoricTimestamps()[i] + ": bought " + transaction + " shares at " + stock.getHistoricPrices()[i];
                    this.transactions.add(output);
                }
            }
            if (rsi >= sellThreshold){
                int transaction = 0;
                for (int j = 0; j < sellAmount; j++){
                    int result = user.sellStock(stock, stock.getHistoricPrices()[i] * 100.0);
                    if (result > 0){
                        transaction += result;
                    }
                }
                if (transaction > 0){
                    String output = stock.getHistoricTimestamps()[i] + ": sold " + transaction + " shares at " + stock.getHistoricPrices()[i];
                    this.transactions.add(output);
                }
            }
        }
        user.calculateCurrentWorth();
        this.simulatedWorth = user.getCurrentWorth();
        this.simulatedCapital = user.getCapital();

        user.setCapital(this.savedCapital);
        stock.setNumOwned(this.startingAmount);
        user.calculateCurrentWorth();;
    }

    public ArrayList<String> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<String> transactions) {
        this.transactions = transactions;
    }

    public int getSavedCapital() {
        return savedCapital;
    }

    public void setSavedCapital(int savedCapital) {
        this.savedCapital = savedCapital;
    }

    public int getSimulatedCapital() {
        return simulatedCapital;
    }

    public void setSimulatedCapital(int simulatedCapital) {
        this.simulatedCapital = simulatedCapital;
    }

    public double getSimulatedWorth() {
        return simulatedWorth;
    }

    public void setSimulatedWorth(double simulatedWorth) {
        this.simulatedWorth = simulatedWorth;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(int startingAmount) {
        this.startingAmount = startingAmount;
    }

    public double getBuyThreshold() {
        return buyThreshold;
    }

    public void setBuyThreshold(double buyThreshold) {
        this.buyThreshold = buyThreshold;
    }

    public double getSellThreshold() {
        return sellThreshold;
    }

    public void setSellThreshold(double sellThreshold) {
        this.sellThreshold = sellThreshold;
    }

    public int getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(int buyAmount) {
        this.buyAmount = buyAmount;
    }

    public int getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(int sellAmount) {
        this.sellAmount = sellAmount;
    }
}
