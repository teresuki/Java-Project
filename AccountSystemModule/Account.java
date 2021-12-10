/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.accountsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Teresuki
 */
public class Account {

    private String username;
    private byte[] hashedPassword;
    private byte[] salt;

    Account() {

    }

    Account(String username, byte[] hashedPassword, byte[] salt) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
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

    String getUsername() {
        return this.username;
    }

    String getHashedPasswordString() {
        return Base64.getEncoder().encodeToString(this.hashedPassword);
    }

    String getSaltString() {
        return Base64.getEncoder().encodeToString(this.salt);
    }

}
