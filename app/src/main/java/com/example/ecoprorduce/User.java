package com.example.ecoprorduce;

import java.util.EmptyStackException;

public class User {

    User(){}

    private int userId;
    private String email;
    private String passwordHash;
    private String passwordSalt;
    private String firstName;
    private String lastName;
    private String zipCode;
    private int transactionCount;
    private String state;
    private String account;

    public int getUserId() {return this.userId;}
    public void setUserId(int userId) {this.userId = userId;}

    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}

    public String getPasswordHash() {return this.passwordHash;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}

    public String getPasswordSalt() {return this.passwordSalt;}
    public void setPasswordSalt(String passwordSalt) {this.passwordSalt = passwordSalt;}

    public String getFirstName() {return this.firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return this.lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getZipCode() {return this.zipCode;}
    public void setZipCode(String zipCode) {this.zipCode = zipCode;}

    public int getTransactionCount() {return this.transactionCount;}
    public void setTransactionCount(int transactionCount) {this.transactionCount = transactionCount;}

    public String getState() {return this.state;}
    public void setState(String state) {this.state = state;}

    public String getAccount() {return this.account;}
    public void setAccount(String account) {this.account = email;}
}
