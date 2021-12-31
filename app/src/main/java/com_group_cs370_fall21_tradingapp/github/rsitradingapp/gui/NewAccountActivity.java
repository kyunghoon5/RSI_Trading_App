package com_group_cs370_fall21_tradingapp.github.rsitradingapp.gui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com_group_cs370_fall21_tradingapp.github.rsitradingapp.R;
import com_group_cs370_fall21_tradingapp.github.rsitradingapp.controllers.UserUtils;

public class NewAccountActivity extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        final EditText usernameEntry = findViewById(R.id.usernameEntry2);
        final EditText passwordEntry1 = findViewById(R.id.passwordEntry2);
        final EditText passwordEntry2 = findViewById(R.id.passwordEntry3);
        final EditText emailEntry = findViewById(R.id.emailEntry);
        final TextView errorTextView = findViewById(R.id.errorMessageTextView2);
        Button createButton = findViewById(R.id.createButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEntry.getText().toString();
                int result = UserUtils.validateUsername(username);
                if (result != 1){
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(UserUtils.getUsernameErrorMsg(result));
                    return;
                }
                String password1 = passwordEntry1.getText().toString();
                result = UserUtils.validatePassword(password1);
                if (result != 1){
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(UserUtils.getPasswordErrorMsg(result));
                    return;
                }
                String password2 = passwordEntry2.getText().toString();
                if (!password2.contentEquals(password1)){
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Passwords do not match");
                    return;
                }
                String email = emailEntry.getText().toString();
                result = UserUtils.validateEmail(email);
                if (result != 1){
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText(UserUtils.getPasswordErrorMsg(result));
                    return;
                }
                attemptAccountCreation(username);
            }
        });

    }
    public void attemptAccountCreation(String username){
        CollectionReference userData = database.collection("userdata");
        DocumentReference userDoc = userData.document(username);

        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final TextView errorTextView = findViewById(R.id.errorMessageTextView2);
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Username is already in use!");
                    }
                    else {
                        createNewAccount();
                        errorTextView.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Database Error.");
                }
            }
        });
    }
    public void createNewAccount(){
        final EditText usernameEntry = findViewById(R.id.usernameEntry2);
        final EditText passwordEntry = findViewById(R.id.passwordEntry2);
        final EditText emailEntry = findViewById(R.id.emailEntry);

        String username = usernameEntry.getText().toString();
        String password = passwordEntry.getText().toString();
        String email = emailEntry.getText().toString();

        DocumentReference userTable = database.document("userdata/"+ username);
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("username", username);
        user.put("password", password);
        user.put("email", email);
        user.put("capital", 0);
        user.put("numStocks", 0);
        userTable.set(user);
    }
}
