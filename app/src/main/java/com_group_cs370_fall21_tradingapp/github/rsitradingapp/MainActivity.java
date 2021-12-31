package com_group_cs370_fall21_tradingapp.github.rsitradingapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com_group_cs370_fall21_tradingapp.github.rsitradingapp.controllers.StockUtils;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.controllers.UserUtils;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.gui.AccountEditFragment;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.gui.AddStockFragment;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.gui.HomeFragment;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.gui.LoginFragment;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.gui.NewAccountActivity;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.models.Simulator;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.models.Stock;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.models.User;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static User USER;
    public ArrayList<Stock> deletedStocks = new ArrayList<Stock>();
    public static int displayedStock = 0;
    private int stockDownloadProgress = 0;
    public FirebaseFirestore database;
    private boolean changes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        USER = new User("", "", "");
        database = FirebaseFirestore.getInstance();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        loadLoginFragment();
    }

    private void loadNewAccountActivity(){
        Intent intent = new Intent(this, NewAccountActivity.class);
        startActivity(intent);
    }

    public void loadHomeFragment(){
        findViewById(R.id.loginLayout).setVisibility(View.GONE);
        findViewById(R.id.homeLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.accountEditFragment).setVisibility(View.GONE);
        findViewById(R.id.addStockFragment).setVisibility(View.GONE);
        findViewById(R.id.simFragment).setVisibility(View.GONE);

        USER.calculateCurrentWorth();
        if (displayedStock >= USER.getNumStocks()){
            displayedStock = 0;
        }
        updateStockDisplay();

        Button logoutButton = findViewById(R.id.logoutButton);
        Button nextButton = findViewById(R.id.nextStockButton);
        Button previousButton = findViewById(R.id.previousStockButton);
        Button editAccountButton = findViewById(R.id.editButton);
        Button trackNewStockButton = findViewById(R.id.addNewStockButton);
        Button buyStockButton = findViewById(R.id.buyButton);
        Button sellStockButton = findViewById(R.id.sellButton);
        Spinner simulationSpinner = findViewById(R.id.simulationSpinner);

        String[] SimArray = {"Simulation","RSI","Mean-Reversion", "Statistical Arbitrage", "Momentum", "Trend Following", "Market Making", "Sentiment"};
        ArrayAdapter<String> simAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,SimArray);
        simulationSpinner.setAdapter(simAdapter);
        simulationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (USER.getNumStocks() > 0) {
                    if (position == 1) {
                        loadSimulationFragment();
                    }else if (position == 2) {
                        loadSimulationFragment();
                    }else if (position == 3) {
                        loadSimulationFragment();
                    }else if (position == 4) {
                        loadSimulationFragment();
                    }else if (position == 5) {
                        loadSimulationFragment();
                    }else if (position == 6) {
                        loadSimulationFragment();
                    }else if (position == 7) {
                        loadSimulationFragment();
                    }
                }

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
                //TextView amount = findViewById(R.id.stockOwnedView);
               // amount.setText("Amount Owned:");
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadLoginFragment();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (USER.getNumStocks() > 0) {
                    displayedStock++;
                    if (displayedStock >= USER.getNumStocks()){
                        displayedStock = 0;
                    }
                    updateStockDisplay();
                }
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (USER.getNumStocks() > 0) {
                    displayedStock--;
                    if (displayedStock < 0) {
                        displayedStock = (int) USER.getNumStocks() - 1;
                    }
                    updateStockDisplay();
                }
            }
        });
        editAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAccountEditFragment();
            }
        });
        trackNewStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAddStockFragment();
            }
        });
        sellStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = USER.sellStock(USER.getStocks().get(displayedStock));
                if (result < 0){
                    makeToast("Not enough shares to sell stock.");
                }
                else {
                    updateHomeUserDisplay();
                    saveUserToDatabase();
                }
            }
        });
        buyStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = USER.buyStock(USER.getStocks().get(displayedStock));
                if (result < 0){
                    makeToast("Not enough money to buy stock.");
                }
                else {
                    updateHomeUserDisplay();
                    saveUserToDatabase();
                }
            }
        });




    }

    public void makeToast(String message){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }
    public void updateStockDisplay(){
        if (USER != null){
            final TextView ticker = findViewById(R.id.tickerText);
            final TextView company = findViewById(R.id.companyText);
            final TextView RSI = findViewById(R.id.rsiText);
            final TextView currentPrice = findViewById(R.id.priceText);
            final TextView suggestion = findViewById(R.id.buySellText);
            final TextView username = findViewById(R.id.usernameTextView);

            username.setText(USER.getUsername());
            updateHomeUserDisplay();

            if (USER.getNumStocks() == 0){
                ticker.setText("No Stocks");
                company.setText("Press ADD STOCKS to track new stocks.");
                RSI.setText("RSI: None");
                currentPrice.setText("Current Price: None");

                suggestion.setText("");
                updateGraph(null);
            }
            else if (displayedStock < USER.getNumStocks()){
                Stock stock = USER.getStocks().get(displayedStock);
                ticker.setText(stock.getTicker());
                company.setText(stock.getCompany());
                RSI.setText("RSI: " + String.valueOf(stock.getRsi()));
                currentPrice.setText("Current Price: " + String.valueOf(stock.getCurrentPrice()));
                String suggest = "HOLD";
                if (stock.getRsi() <= 40){
                    suggest = "BUY";
                }
                else if (stock.getRsi() >= 70){
                    suggest = "SELL";
                }
                suggestion.setText(suggest);
                updateGraph(stock);
            }
        }
    }
    public void updateGraph(Stock stock){
        GraphView graph = findViewById(R.id.graphView1);
        graph.removeAllSeries();

        if (stock!=null) {
            LineGraphSeries<DataPoint> dataSeries = new LineGraphSeries<>();
            for (int i = 0; i < stock.getNumPrices(); i++) {
                dataSeries.appendData(new DataPoint(i, stock.getHistoricPrices()[i]), true, stock.getNumPrices() + 1);
            }
            graph.addSeries(dataSeries);
        }
    }
    public void updateHomeUserDisplay(){
        final TextView capital = findViewById(R.id.capitalView);
        double money = ((double)USER.getCapital())/100.0;
        capital.setText("Capital: $" + money + "   Total Worth: $" + USER.getCurrentWorth());
    }

    public void loadLoginFragment(){
        findViewById(R.id.loginLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.homeLayout).setVisibility(View.GONE);
        findViewById(R.id.accountEditFragment).setVisibility(View.GONE);
        findViewById(R.id.addStockFragment).setVisibility(View.GONE);
        findViewById(R.id.loadingPage).setVisibility(View.GONE);
        findViewById(R.id.loadingView).setVisibility(View.GONE);
        findViewById(R.id.simFragment).setVisibility(View.GONE);

        final EditText usernameEntry = findViewById(R.id.usernameEntry);
        final EditText passwordEntry = findViewById(R.id.passwordEntry);
        final TextView errorTextView = findViewById(R.id.errorMessageTextView);
        Button loginButton = findViewById(R.id.loginButton);
        Button createNewButton = findViewById(R.id.newAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = usernameEntry.getText().toString();
                int result = UserUtils.validateUsername(username);
                if (result != 1){
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(UserUtils.getUsernameErrorMsg(result));
                    return;
                }
                String password = passwordEntry.getText().toString();
                result = UserUtils.validatePassword(password);
                if (result != 1){
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(UserUtils.getPasswordErrorMsg(result));
                    return;
                }
                attemptLogin();
            }
        });
        createNewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadNewAccountActivity();
            }
        });

    }

    public void loadAccountEditFragment(){
        findViewById(R.id.loginLayout).setVisibility(View.GONE);
        findViewById(R.id.homeLayout).setVisibility(View.GONE);
        findViewById(R.id.accountEditFragment).setVisibility(View.VISIBLE);
        findViewById(R.id.addStockFragment).setVisibility(View.GONE);
        findViewById(R.id.simFragment).setVisibility(View.GONE);

        TextView usernameView = findViewById(R.id.usernameView);
        usernameView.setText(USER.getUsername());

        EditText passwordEntry = findViewById(R.id.editTextPassword);

        EditText emailEntry = findViewById(R.id.emailEditText);
        emailEntry.setText(USER.getEmail());

        EditText capitalEntry = findViewById(R.id.capitalEditText);
        double capital = ((double)USER.getCapital())/100.0;
        capitalEntry.setText(capital + "");

        refreshStockSpinner();
        Spinner spinner = findViewById(R.id.removeStockSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Stock selectedItem = (Stock) parent.getItemAtPosition(position);
                TextView amount = findViewById(R.id.stockOwnedView);
                amount.setText("Amount Owned: " + selectedItem.getNumOwned());

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
                TextView amount = findViewById(R.id.stockOwnedView);
                amount.setText("Amount Owned:");
            }
        });

        Button removeStockButton = findViewById(R.id.removeStockButton);
        removeStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = findViewById(R.id.removeStockSpinner);
                TextView amount = findViewById(R.id.stockOwnedView);
                Stock stock = (Stock) spinner.getSelectedItem();
                if (stock!=null) {
                    deletedStocks.add(stock);
                    USER.removeStock(stock);
                    amount.setText("Amount Owned:");
                    refreshStockSpinner();
                }
            }
        });

        Button addStockButton = findViewById(R.id.addStockButton);
        addStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = findViewById(R.id.removeStockSpinner);
                TextView amount = findViewById(R.id.stockOwnedView);
                Stock stock = (Stock) spinner.getSelectedItem();
                if (stock!=null) {
                    stock.setNumOwned(stock.getNumOwned() + 1);
                    amount.setText("Amount Owned: " + stock.getNumOwned());
                }
            }
        });
        Button reduceStockButton = findViewById(R.id.reduceStockButton);
        reduceStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = findViewById(R.id.removeStockSpinner);
                TextView amount = findViewById(R.id.stockOwnedView);
                Stock stock = (Stock) spinner.getSelectedItem();
                if (stock!=null) {
                    if (stock.getNumOwned() > 0) {
                        stock.setNumOwned(stock.getNumOwned() - 1);
                        amount.setText("Amount Owned: " + stock.getNumOwned());
                    }
                }
            }
        });
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < deletedStocks.size(); i++){
                    USER.addStock(deletedStocks.get(i));
                }
                deletedStocks.clear();
                loadHomeFragment();
            }
        });
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText passwordEntry = findViewById(R.id.editTextPassword);
                String password = passwordEntry.getText().toString();
                if (UserUtils.validatePassword(password) == 1){
                    USER.setPassword(password);
                }

                EditText emailEntry = findViewById(R.id.emailEditText);
                String email = emailEntry.getText().toString();
                if (UserUtils.validateEmail(email) == 1) {
                    USER.setEmail(email);
                }

                EditText capitalEntry = findViewById(R.id.capitalEditText);
                double capital = Double.parseDouble(capitalEntry.getText().toString()) * 100.0;
                if (capital > 0){
                    USER.setCapital((int)capital);
                }
                saveUserToDatabase();
                loadHomeFragment();
            }
        });

    }
    public void loadAddStockFragment(){
        findViewById(R.id.loginLayout).setVisibility(View.GONE);
        findViewById(R.id.homeLayout).setVisibility(View.GONE);
        findViewById(R.id.accountEditFragment).setVisibility(View.GONE);
        findViewById(R.id.addStockFragment).setVisibility(View.VISIBLE);
        findViewById(R.id.simFragment).setVisibility(View.GONE);
        changes = false;

        Button doneButton = findViewById(R.id.newStockDoneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changes){
                    saveUserToDatabase();
                }
                loadHomeFragment();
            }
        });
        Button stockSearchButton = findViewById(R.id.stockSearchButton);
        EditText tickerInput = findViewById(R.id.tickerInputEditText);
        TextView stockDataOutput = findViewById(R.id.newStockDataTextView);
        stockSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ticker = tickerInput.getText().toString();
                String stockData = StockUtils.getBasicStockData(ticker);
                stockDataOutput.setText(stockData);
            }
        });
        Button trackNewButton = findViewById(R.id.trackNewStockButton);
        trackNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stockDataOutput.getText().toString().contentEquals("Invalid Ticker")){
                    StockUtils.temp.apiCall();
                    USER.addStock(StockUtils.temp);
                    changes = true;
                }
            }
        });
    }
    public void refreshStockSpinner(){
        Spinner spinner = findViewById(R.id.removeStockSpinner);
        ArrayAdapter<Stock> adapter = new ArrayAdapter<Stock>(this, android.R.layout.simple_spinner_item, USER.getStocks());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
    public void loadSimulationFragment(){
        findViewById(R.id.loginLayout).setVisibility(View.GONE);
        findViewById(R.id.homeLayout).setVisibility(View.GONE);
        findViewById(R.id.accountEditFragment).setVisibility(View.GONE);
        findViewById(R.id.addStockFragment).setVisibility(View.GONE);
        findViewById(R.id.simFragment).setVisibility(View.VISIBLE);

        TextView simTicker = findViewById(R.id.simTickerView);
        simTicker.setText(USER.getStocks().get(displayedStock).getTicker());

        TextView userDataDisplay = findViewById(R.id.userWorthView1);
        double capital = ((double)USER.getCapital())/100.0;
        userDataDisplay.setText("Capital: " + capital + " Worth: " + USER.getCurrentWorth());

        Button runButton = findViewById(R.id.simRunButton);
        Button doneButton = findViewById(R.id.simDoneButton);

        TextView transactionView = findViewById(R.id.transactionView);
        transactionView.setMovementMethod(new ScrollingMovementMethod());
        transactionView.setText("");

        TextView simDataDisplay = findViewById(R.id.simWorthView);
        simDataDisplay.setText("Possible Capital: ??? Possible Worth: ???");

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTransactionListView();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHomeFragment();
            }
        });

    }
    public void updateTransactionListView(){
        EditText buyThresholdEdit = findViewById(R.id.buyThresholdEdit);
        double buyThresh = Double.parseDouble(buyThresholdEdit.getText().toString());

        EditText sellThresholdEdit = findViewById(R.id.sellThresholdAmount);
        double sellThresh = Double.parseDouble(sellThresholdEdit.getText().toString());

        EditText buyAmountEdit = findViewById(R.id.buyAmountEdit);
        int buyAmount = Integer.parseInt(buyAmountEdit.getText().toString());

        EditText sellAmountEdit = findViewById(R.id.sellAmountEdit);
        int sellAmount = Integer.parseInt(sellAmountEdit.getText().toString());

        Simulator sim = new Simulator(USER, USER.getStocks().get(displayedStock), buyThresh, sellThresh, buyAmount, sellAmount );
        sim.runSimulation();

        String output = "";
        ArrayList<String> transactions = sim.getTransactions();
        if (transactions.size() > 0) {
            for (int i = 0; i < transactions.size(); i++) {
                output += transactions.get(i) + "\n";
            }
        }
        else {
            output = "During the time period the stock never went below the buy threshold or above the sell threshold.";
        }

        TextView simDataDisplay = findViewById(R.id.simWorthView);
        double possibleCapital = ((double)sim.getSimulatedCapital())/100.0;
        simDataDisplay.setText("Possible Capital: " + possibleCapital + " Possible Worth: " + sim.getSimulatedWorth());

        TextView transactionView = findViewById(R.id.transactionView);
        transactionView.setMovementMethod(new ScrollingMovementMethod());
        transactionView.setText(output);
    }
    public void attemptLogin(){
        final EditText usernameEntry = findViewById(R.id.usernameEntry);
        String username = usernameEntry.getText().toString();

        CollectionReference userData = database.collection("userdata");
        DocumentReference userDoc = userData.document(username);

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final TextView errorTextView = findViewById(R.id.errorMessageTextView);
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        final EditText passwordEntry = findViewById(R.id.passwordEntry);
                        String password = passwordEntry.getText().toString();
                        String docPass = (String) doc.get("password");
                        if (password.contentEquals(docPass)){
                            errorTextView.setVisibility(View.INVISIBLE);
                            loadHomeFragment();
                            findViewById(R.id.loadingPage).setVisibility(View.VISIBLE);
                            findViewById(R.id.loadingView).setVisibility(View.VISIBLE);
                            loadUserFromDatabase(doc);
                            //loadHomeFragment();
                        }
                        else {
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText("Password Incorrect.");
                        }

                    }
                    else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("No Such User Exists.");
                        }
                    }
                else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Database Error.");
                }
            }
        });
    }
    public void saveUserToDatabase(){
        DocumentReference userTable = database.document("userdata/"+ USER.getUsername());
        Map<String, Object> user1 = new HashMap<String, Object>();
        user1.put("username", USER.getUsername());
        user1.put("password", USER.getPassword());
        user1.put("email", USER.getEmail());
        user1.put("capital", USER.getCapital());
        user1.put("numStocks", USER.getNumStocks());
        userTable.set(user1);

        for (int i = 0; i < USER.getNumStocks(); i++){
            DocumentReference stockTable = database.document("stockdata/"+ USER.getUsername() + "_" + i);
            Map<String, Object> stockData = new HashMap<String, Object>();
            Stock stock = USER.getStocks().get(i);
            stockData.put("ticker", stock.getTicker());
            stockData.put("company", stock.getCompany());
            stockData.put("numOwned", stock.getNumOwned());
            stockTable.set(stockData);
        }

    }

    public void loadUserFromDatabase(DocumentSnapshot userData){
        USER.setUsername((String)userData.get("username"));
        USER.setPassword((String)userData.get("password"));
        USER.setEmail((String)userData.get("email"));
        Long cap = (long)userData.get("capital");
        USER.setCapital(cap.intValue());
        USER.setNumStocks(0);
        Long num = (long)userData.get("numStocks");
        this.stockDownloadProgress = num.intValue();

        CollectionReference stockData = database.collection("stockdata");
        for (int i = 0; i < this.stockDownloadProgress; i++) {
            DocumentReference stockDoc = stockData.document(USER.getUsername() + "_" + i);

            stockDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    final TextView errorTextView = findViewById(R.id.errorMessageTextView);
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            Long num = (long)doc.get("numOwned");
                            Stock stock = new Stock ((String)doc.get("ticker"), (String)doc.get("company"), num.intValue());
                            stock.apiCall();
                            USER.addStock(stock);
                            USER.setPrevNumStocks(USER.getPrevNumStocks() + 1);
                            if (USER.getNumStocks() >= stockDownloadProgress){
                                loadHomeFragment();
                            }
                            findViewById(R.id.loadingPage).setVisibility(View.GONE);
                            findViewById(R.id.loadingView).setVisibility(View.GONE);

                        } else {
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText("Error getting stock data");
                        }
                    } else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Database Error getting stock data.");
                    }
                }
            });
        }
        if (USER.getNumStocks() == 0) {
            findViewById(R.id.loadingPage).setVisibility(View.GONE);
            findViewById(R.id.loadingView).setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (USER.getNumStocks() > 0) {
            if (position == 1) {
                loadSimulationFragment();
            }else if (position == 2) {
                //loadSimulationFragment();
            }else if (position == 3) {
                //loadSimulationFragment();
            }else if (position == 4) {
                //loadSimulationFragment();
            }else if (position == 5) {
                //loadSimulationFragment();
            }else if (position == 6) {
                //loadSimulationFragment();
            }else if (position == 7) {
                //loadSimulationFragment();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
