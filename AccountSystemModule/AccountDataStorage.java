/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.accountsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Teresuki
 */
public class AccountDataStorage {

    String m_fileName;
    HashMap<String, Account> m_accountHashMap = new HashMap();

    //Getting login data from file
    void importFromSource(String fileName) throws FileNotFoundException, IOException {
        m_fileName = fileName;

        BufferedReader reader = new BufferedReader(new FileReader(m_fileName));
        String line = null;
        Scanner sc = null;

        while ((line = reader.readLine()) != null) {
            sc = new Scanner(line);
            sc.useDelimiter(",");

            var thisAccount = new Account(
                    sc.next(),
                    Base64.getDecoder().decode(sc.next()),
                    Base64.getDecoder().decode(sc.next()));

            m_accountHashMap.put(thisAccount.getUsername(), thisAccount);
        }

    }

    Account getAccount(String userName) {
        if (m_accountHashMap.containsKey(userName)) {
            return m_accountHashMap.get(userName);
        }
        return null;
    }

    void insertAccount(Account newAccount) throws FileNotFoundException, IOException {
        //Check if username has already exist:
        if (m_accountHashMap.containsKey(newAccount.getUsername())) {
            System.out.println("Username is taken, please try other username.");
            return;
        }

        m_accountHashMap.put(newAccount.getUsername(), newAccount);

        var out = new BufferedWriter(new FileWriter(m_fileName, true));
        out.append(String.format(
                "%s,%s,%s\n",
                newAccount.getUsername(),
                newAccount.getHashedPasswordString(),
                newAccount.getSaltString()));

        out.close();
        System.out.println("Account created successfully!");
    }

}
