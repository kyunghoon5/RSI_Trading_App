package com_group_cs370_fall21_tradingapp.github.rsitradingapp.controllers;

import android.util.Log;

import java.util.regex.Pattern;

public class UserUtils {
    public static int validateUsername(String username){
        if (username.length() == 0){    //if the username is empty string return -1
            return -1;
        }
        if (username.length() < 5){     //if the username is under 5 chars return -2
            return -2;
        }
        if (username.length() > 16){    //if the username is over 16 chars return -3
            return  -3;
        }
        Pattern pattern = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]"); //regex pattern for special characters
        if (pattern.matcher(username).find()){//if the username contains special chars, return -4
            return - 4;
        }
        return 1;                       //username passes, so return 1
    }
    public static int validatePassword(String password){
        if (password.length() == 0){    //if the password is empty string return -1
            return -1;
        }
        if (password.length() < 5){     //if the password is under 5 chars return -2
            return -2;
        }
        if (password.length() > 16){    //if the password is over 16 chars return -3
            return  -3;
        }
        Pattern pattern = Pattern.compile("^(?=.*[0-9])"); //regex pattern for numeric chars
        if (!pattern.matcher(password).find()){
            return -4;
        }
        pattern = Pattern.compile("^(?=.*[a-z])"); //regex pattern for lowercase chars
        if (!pattern.matcher(password).find()){
            return -5;
        }
        pattern = Pattern.compile("^(?=.*[A-Z])"); //regex pattern for uppercase chars
        if (!pattern.matcher(password).find()){
            return -6;
        }
        pattern = Pattern.compile("^(?=.*[@#$%^*!&+=])"); //regex pattern for special characters
        if (!pattern.matcher(password).find()){
            return -7;
        }
        pattern = Pattern.compile("^(?=\\S+$)"); //regex pattern for whitespace
        if (!pattern.matcher(password).find()){
            return -8;
        }
        return 1;                       //password passes, so return 1
    }
    public static int validateEmail(String email){
        if (email.length() == 0){    //if the email is empty string return -1
            return -1;
        }
        if (email.length() < 3){     //if the password is under 5 chars return -2
            return -2;
        }
        Pattern pattern = Pattern.compile("^(.+)@(.+)$"); //regex pattern for numeric chars
        if (!pattern.matcher(email).find()){
            return -3;
        }
        return 1;
    }
    public static String getUsernameErrorMsg(int errorNumber){
        if (errorNumber == -1){    //if the username is empty string
            return "No username was entered!";
        }
        if (errorNumber == -2){     //if the username is under 5 chars
            return "Username cannot be under 5 characters";
        }
        if (errorNumber == -3){    //if the username is over 16 chars
            return  "Username cannot be over 16 characters!";
        }
        if (errorNumber == -4){     //if the username contains special chars
            return "Username cannot contain special characters!";
        }
        return "Unknown Username Error!";
    }
    public static String getPasswordErrorMsg(int errorNumber){
        if (errorNumber == -1){
            return "No password was entered!";
        }
        if (errorNumber == -2){
            return "Password cannot be under 5 characters";
        }
        if (errorNumber == -3){
            return  "Password cannot be over 16 characters!";
        }
        if (errorNumber == -4){
            return "Password must contain at least 1 number!";
        }
        if (errorNumber == -5){
            return "Password must contain at least 1 lowercase letter!";
        }
        if (errorNumber == -6){
            return "Password must contain at least 1 uppercase letter!";
        }
        if (errorNumber == -7){
            return "Password must contain at least 1 special character!";
        }
        if (errorNumber == -8){
            return "Password cannot contain any white space!";
        }
        return "Unknown Password Error!";
    }
    public static String getEmailErrorMessage(int errorNumber){
        if (errorNumber == -1){
            return "No email was entered!";
        }
        return "Email is not valid!";
    }
}
