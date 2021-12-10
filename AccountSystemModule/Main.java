/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.accountsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math.*;
import java.util.Scanner;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Teresuki
 */
public class Main {

    enum LoginOption {
        SIGN_UP,
        SIGN_IN,
        QUIT
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException, IOException {
        //Account acc1 = new Account();

        LoginOption loginOption;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter \"SIGN_UP\" to create new account");
        System.out.println("Enter \"SIGN_IN\" to login");
        System.out.println("Enter \"QUIT\" to quit program");

        while (true) {
            try {
                loginOption = LoginOption.valueOf(sc.nextLine());
                break;
            } catch (Exception IllegalArgumentException) {
                System.out.println("Invalid Option, please try again.");
            }
        }

        String username;
        String password;
        String accountFileName = "account.csv";
        Scanner scanner = new Scanner(System.in);
        AccountDataStorage ads = new AccountDataStorage();
        ads.importFromSource(accountFileName);
        if (loginOption == LoginOption.SIGN_UP) {
            System.out.println("SIGN_UP chose.");
            //try catch here?
            System.out.println("Enter your username:");
            username = scanner.nextLine();
            System.out.println("Enter your password:");
            password = scanner.nextLine();

            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            var hashedPassword = factory.generateSecret(spec).getEncoded();

            Account newAccount = new Account(username, hashedPassword, salt);
            ads.insertAccount(newAccount);

        } else if (loginOption == LoginOption.SIGN_IN) {
            System.out.println("SIGN_IN chose.");

            //try catch here?
            System.out.println("Enter your username:");
            username = scanner.nextLine();
            System.out.println("Enter your password:");
            password = scanner.nextLine();

            var thisAccount = ads.getAccount(username);
            if (thisAccount == null) {
                System.out.println("Username or/and password incorrect.");
                return;
            }

            if (thisAccount.passwordIsValid(password)) {
                System.out.println("Login successfully");
            } else {
                System.out.println("Username or/and password incorrect.");
            }

        } else if (loginOption == LoginOption.QUIT) {
            System.out.println("Exiting program.");
        }

    }

}
