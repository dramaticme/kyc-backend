package com.kycapp.kycbackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_kyc")
public class UserKyc {
    @Id
    private String id;

    private String name;
    private String aadhaarNumber;
    private String panNumber;
    private String phone;
    private String email;

    // constructor
    public UserKyc() {}

    public UserKyc(String name, String aadhaarNumber, String panNumber, String phone, String email) {
        this.name = name;
        this.aadhaarNumber = aadhaarNumber;
        this.panNumber = panNumber;
        this.phone = phone;
        this.email = email;
    }

    // getters & setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAadhaarNumber() {
        return aadhaarNumber;
    }
    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }
    public String getPanNumber() {
        return panNumber;
    }
    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
