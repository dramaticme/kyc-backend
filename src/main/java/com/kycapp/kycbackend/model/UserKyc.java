package com.kycapp.kycbackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

@Document(collection = "user_kyc")
public class UserKyc {
    @Id
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(regexp = "\\d{12}", message = "Aadhaar must be 12 digits")
    private String aadhaarNumber;

    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "PAN must be valid (e.g. ABCDE1234F)")
    private String panNumber;

    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

    @Email(message = "Invalid email format")
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
