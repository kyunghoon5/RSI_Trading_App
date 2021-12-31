package com_group_cs370_fall21_tradingapp.github.rsitradingapp.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
public class User {
    private String username;
    private String password;
    private String email;
    private int capital;
    private ArrayList<Stock> stocks;
    private int numStocks;
    private int prevNumStocks;
    private double currentWorth;

    public User(){
    }
    //default User used for testing purposes
    public User(int num){
        this.username = "Test1";
        this.password = "$Password1";
        this.email = "email@email.com";
        this.numStocks = 0;
        this.prevNumStocks = this.numStocks;
        this.capital = 500;
        this.stocks = new ArrayList<Stock>();
        this.addStock(new Stock(1));
        this.addStock(new Stock(2));
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.capital = 0;
        this.numStocks = 0;
        this.prevNumStocks = 0;
        this.stocks = new ArrayList<Stock>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumStocks() {
        return numStocks;
    }

    public void setNumStocks(int numStocks) {
        this.numStocks = numStocks;
    }

    public int getCapital() {
        return capital;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }

    public ArrayList<Stock> getStocks() {
        return stocks;
    }

    public double getCurrentWorth() {
        return currentWorth;
    }

    public void setCurrentWorth(double currentWorth) {
        this.currentWorth = currentWorth;
    }

    public int getPrevNumStocks() {
        return prevNumStocks;
    }

    public void setPrevNumStocks(int prevNumStocks) {
        this.prevNumStocks = prevNumStocks;
    }

    public void setStocks(ArrayList<Stock> stocks) {
        this.stocks = stocks;
    }
    public void addStock(Stock stock){
        for (int i = 0; i < numStocks; i++){
            if (stock.getTicker().contentEquals(this.stocks.get(i).getTicker())){
                this.stocks.get(i).setNumOwned(this.stocks.get(i).getNumOwned() + stock.getNumOwned());
                return;
            }
        }
        this.stocks.add(stock);
        this.numStocks++;
        this.prevNumStocks++;
    }
    public void loadStock(Stock stock){
        this.stocks.add(stock);
    }
    public void removeStock(Stock stock){
        this.stocks.remove(stock);
        this.numStocks--;
    }
    public void calculateCurrentWorth(){
        currentWorth = ((double) capital) / 100.0;
        for (int i = 0; i < numStocks; i++){
            currentWorth += stocks.get(i).getCurrentPrice() * stocks.get(i).getNumOwned();
            currentWorth = Math.round(currentWorth * 100.0)/100.0;
        }
    }
    public int buyStock(Stock stock){
        int price = (int)(stock.getCurrentPrice() * 100.0);
        if (price > this.capital){
            return -1;
        }
        else {
            this.capital -= price;
            stock.setNumOwned(stock.getNumOwned() + 1);
        }
        return 1;
    }
    public int sellStock(Stock stock){
        int price = (int)(stock.getCurrentPrice() * 100.0);
        if (stock.getNumOwned() <= 0){
            return -1;
        }
        else {
            this.capital += price;
            stock.setNumOwned(stock.getNumOwned() - 1);
        }
        return 1;
    }
    public int buyStock(Stock stock, double price){
        if (price > this.capital){
            return -1;
        }
        else {
            this.capital -= price;
            stock.setNumOwned(stock.getNumOwned() + 1);
        }
        return 1;
    }
    public int sellStock(Stock stock, double price){
        if (stock.getNumOwned() <= 0){
            return -1;
        }
        else {
            this.capital += price;
            stock.setNumOwned(stock.getNumOwned() - 1);
        }
        return 1;
    }
}
