/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.accountsystem;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Date;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Teresuki
 */
public class Account {

    enum InsuranceType {
        Public,
        Private
    }

    enum Gender {
        M,
        F,
        O
    }

    private UUID accountID;
    private String username;
    private byte[] hashedPassword;
    private byte[] salt;
    private String email;
    private String firstName = null;
    private String lastName = null;
    private String address = null;
    private String insuranceID = null;
    private InsuranceType insuranceType = null;
    private Gender gender = null;
    private Date dateOfBirth = null; //sql date

    //Constructor:
    Account() {

    }

    Account(String username, byte[] hashedPassword, byte[] salt) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    boolean isUsernameValid(String username) {
        if (username.isEmpty()) {
            return false;
        }
        return true;
    }

    boolean isPasswordValid(String password) {
        if (password.isEmpty()) {
            return false;
        }
        return true;
    }

    boolean isEmailValid(String email) {
        String EMAIL_PATTERN
                = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    void setStringToNull(String string) {
        string = null;
    }

    void setAccountInfo(String username, byte[] hashedPassword, byte[] salt) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    boolean passwordIsValid(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), this.salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        var hashedPassword = factory.generateSecret(spec).getEncoded();

        if (Arrays.equals(hashedPassword, this.hashedPassword)) {
            return true;
        }
        return false;

    }

    String getAccountIDString() {
        return this.accountID.toString();
    }

    String getUsername() {
        return this.username;
    }

    byte[] getHashedPassword()
    {
        return this.hashedPassword;
    }
    
    byte[] getSalt()
    {
        return this.salt;
    }
    
    String getHashedPasswordString() {
        return Base64.getEncoder().encodeToString(this.hashedPassword);
    }

    String getSaltString() {
        return Base64.getEncoder().encodeToString(this.salt);
    }

    String getEmail() {
        return this.email;
    }

    String getFirstName() {
        return this.firstName;
    }

    String getLastName() {
        return this.lastName;
    }

    String getAddress() {
        return this.address;
    }

    String getInsuranceID() {
        return this.insuranceID;
    }

    String getInsuranceTypeString() {
        String temp;
        try {
            temp = insuranceType.toString();
        } catch (NullPointerException e) {
            temp = null;
        }
        return temp;
    }

    String getGenderString() {
        String temp;
        try {
            temp = gender.toString();
        } catch (NullPointerException e) {
            temp = null;
        }
        return temp;
    }

    Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    void setSalt(byte[] salt) {
        this.salt = salt;
    }

    void setEmail(String email) {
        this.email = email;
    }

    void setFirstName(String firstName) {
        if(firstName != null && firstName.isEmpty())
            firstName = null;
        this.firstName = firstName;
    }

    void setLastName(String lastName) {
        if(lastName != null && lastName.isEmpty())
            lastName = null;
        this.lastName = lastName;
    }

    void setAddress(String address) {
        if(address != null && address.isEmpty())
            address = null;
        this.address = address;
    }

    void setInsuranceID(String insuranceID) {
        if(insuranceID != null && insuranceID.isEmpty())
            insuranceID = null;
        this.insuranceID = insuranceID;
    }

    void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }

    void setGender(Gender gender) {
        this.gender = gender;
    }

    void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
