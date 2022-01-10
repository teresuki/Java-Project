/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.accountsystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
/**
 *
 * @author Teresuki
 */
public class Main {


    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException, IOException, SQLException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Password:");
        String DBPassword = scanner.nextLine();
        EHealthDatabase ehdb = new EHealthDatabase(DBPassword);
        
        ehdb.importAndPrintDBUser();
        
        ehdb.loginOptionStart();
    }

}
