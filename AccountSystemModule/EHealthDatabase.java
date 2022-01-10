package com.mycompany.accountsystem;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class EHealthDatabase {

    enum LoginOption {
        SIGN_UP,
        SIGN_IN,
        QUIT
    }

    private static String HOSTNAME = "ehealth-db.cqajckw84dii.us-east-1.rds.amazonaws.com";
    private static String PORT = "3306";
    private static String DATABASENAME = "ehealth"; // Or Schema Name

    private static final String URL = "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/ " + DATABASENAME;
    private static String DBUSERNAME = "admin";
    private static String DBPASSWORD = "";
    private Connection connection;

    public EHealthDatabase(String DBPASSWORD) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        this(DBPASSWORD, "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/ " + DATABASENAME, DBUSERNAME);
    }

    public EHealthDatabase(
            String DBPASSWORD,
            String URL,
            String DBUSERNAME) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {

        loadJDBCDriver();

        this.DBPASSWORD = DBPASSWORD;
        connectToDatabase();

    }

    void loadJDBCDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("JDBC Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }
    }

    void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(URL, DBUSERNAME, DBPASSWORD);
            System.out.println("Database connected! Welcome " + DBUSERNAME);
        } catch (SQLException e) {
            System.out.println("Cannot connect to the database!");
            System.out.println(e.toString());
        }
    }

    void loginOptionStart() throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException {
        LoginOption loginOption;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter \"SIGN_UP\" to create new account");
        System.out.println("Enter \"SIGN_IN\" to login");
        System.out.println("Enter \"QUIT\" to quit program");

        while (true) {
            try {
                loginOption = LoginOption.valueOf(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("Invalid Option, please try again.");
                System.out.println(e.toString());
            }
        }

        if (null != loginOption) {
            switch (loginOption) {
                case SIGN_UP ->
                    signUp();
                case SIGN_IN ->
                    signIn();
                case QUIT -> {
                    System.out.println("Exiting Program, terminating connection.");
                    connection.close();
                }
                default -> {
                }
            }
        }
    }

    void signUp() throws NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
        Account newAccount = new Account();

        UUID accountID = UUID.randomUUID(); //Generates random UUID
        while (isUUIDExist(accountID.toString())) {
            accountID = UUID.randomUUID();
        }
        newAccount.setAccountID(accountID);

        Scanner scanner = new Scanner(System.in);

        for (boolean goodInput = false;;) {
            System.out.println("Enter your username:");
            newAccount.setUsername(scanner.nextLine());
            goodInput = newAccount.isUsernameValid(newAccount.getUsername());
            if (goodInput == false) {
                System.out.println("Username is Invalid, please try again.");
                continue;
            }
            goodInput = !isUsernameExist(newAccount.getUsername()); //Username must NOT already existed
            if (goodInput == false) {
                System.out.println("Username \"" + newAccount.getUsername() + "\" is taken, please try another username.");
                continue;
            }
            break;
        }

        String password;
        do {
            System.out.println("Enter your password:");
            password = scanner.nextLine();
        } while (!newAccount.isPasswordValid(password));

        byte[] salt = generateSalt(); // Salt
        newAccount.setSalt(salt);

        byte[] hashedPassword = generateHashedPassword(password, salt); // hashedPassword
        newAccount.setHashedPassword(hashedPassword);

        for (boolean goodInput = false;;) {
            System.out.println("Enter your email:");
            newAccount.setEmail(scanner.nextLine());
            goodInput = newAccount.isEmailValid(newAccount.getEmail());
            if (goodInput == false) {
                System.out.println("Email is invalid, please try again.");
                continue;
            }
            goodInput = !isEmailExist(newAccount.getEmail());
            if (goodInput == false) {
                System.out.println("Email \"" + newAccount.getEmail() + "\" is has already been registered, please try another Email.");
                continue;
            }
            break;
        }

        System.out.println("Enter your first name:");
        newAccount.setFirstName(scanner.nextLine());

        System.out.println("Enter your last name:");
        newAccount.setLastName(scanner.nextLine());

        System.out.println("Enter your address:");
        newAccount.setAddress(scanner.nextLine());

        System.out.println("Enter your Insurance ID:");
        newAccount.setInsuranceID(scanner.nextLine());

        System.out.println("Enter your Insurance Type (Public/Private):");
        try {
            String temp = scanner.nextLine();
            if (!temp.isEmpty()) // is not empty    
            {
                newAccount.setInsuranceType(Account.InsuranceType.valueOf(temp));
            }
        } catch (Exception e) {
            System.out.println("Invalid Insurance Type Input! Setting Insurance Type to empty.");
            System.out.println(e.toString());
            //return;
        }

        System.out.println("Enter your Gender(M/F/O):");
        try {
            String temp = scanner.nextLine();
            if (!temp.isEmpty()) // is not empty
            {
                newAccount.setGender(Account.Gender.valueOf(temp));
            }
        } catch (Exception e) {
            System.out.println("Invalid Gender Input! Setting Gender to empty.");
            System.out.println(e.toString());
            //return;
        }

        System.out.println("Enter your date of birth (yyyy-mm-dd):");
        try {
            java.sql.Date temp = java.sql.Date.valueOf(scanner.nextLine());
            newAccount.setDateOfBirth(temp);
        } catch (IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                System.out.println("Invalid Date Input! Setting Date to empty.");
            } else {
                System.out.println(e.toString());
                return;
            }

        }

        insertDBUser(newAccount);

    }

    void signIn() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {

        Scanner scanner = new Scanner(System.in);//IO

        System.out.println("Enter your username:");//IO
        String username = scanner.nextLine();//IO

        //Check if username exist in Database:
        if (isUsernameExist(username) == false) {
            //Comment out in final
            System.out.println("Username does not exist");
            return;
        }

        System.out.println("Enter your password:");//IO
        String password = scanner.nextLine();//IO

        //Get salt from Database:
        byte[] salt = getSaltFromDBUsername(username);
        
        System.out.println(Base64.getEncoder().encodeToString(salt));
        
        byte[] hashedPassword = generateHashedPassword(password, salt);

        System.out.println(Base64.getEncoder().encodeToString(hashedPassword));
        
        if (isHashedPasswordCorrect(username, hashedPassword)) {
            System.out.println("Login successfully!");
        } else {
            System.out.println("Password is incorrect");
        }
    }

    void insertDBUser(Account newAccount) throws SQLException {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO user" + " (accountID, username, hashedPassword, salt, email, firstName, lastName, address, insuranceID, insuranceType, gender, dateOfBirth)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, newAccount.getAccountIDString());
            stmt.setString(2, newAccount.getUsername());
            //stmt.setString(3, newAccount.getHashedPasswordString());
            stmt.setBytes(3, newAccount.getHashedPassword());
            //stmt.setString(4, newAccount.getSaltString());
            stmt.setBytes(4, newAccount.getSalt());
            stmt.setString(5, newAccount.getEmail());
            stmt.setString(6, newAccount.getFirstName());
            stmt.setString(7, newAccount.getLastName());
            stmt.setString(8, newAccount.getAddress());
            stmt.setString(9, newAccount.getInsuranceID());
            stmt.setString(10, newAccount.getInsuranceTypeString());
            stmt.setString(11, newAccount.getGenderString());
            stmt.setDate(12, newAccount.getDateOfBirth());

            stmt.executeUpdate();
            System.out.println("Account created Successfully!");
        } catch (Exception e) {

            System.out.println("Insert Failed!");
            System.out.println(e.toString());
        }
    }
    
    void importAndPrintDBUser() throws SQLException {
        //Import Data From Database:
        System.out.println("Importing Data from user");
        Statement stm = connection.createStatement();
        ResultSet rs = stm.executeQuery("SELECT * FROM user");
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnNum = rsmd.getColumnCount();

        for (int i = 1; i <= columnNum; ++i) {
            System.out.printf("%-22s", rsmd.getColumnName(i));
        }
        System.out.println("");

        while (rs.next()) {
            //Column name
            for (int i = 1; i <= columnNum; ++i) {
                System.out.printf("%-22s", rs.getString(i));
            }
            System.out.println("");
        }
    }
    
    boolean isUUIDExist(String newUUID) throws SQLException {
        String query = "SELECT accountID FROM user WHERE accountID = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, newUUID);
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }

    boolean isUsernameExist(String newUsername) throws SQLException {
        String query = "SELECT username FROM user WHERE username = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, newUsername);
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }

    boolean isEmailExist(String newEMail) throws SQLException {
        String query = "SELECT email FROM user WHERE email = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, newEMail);
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }

    byte[] getSaltFromDBUsername(String DBUsername) throws SQLException {
        String query = "SELECT salt FROM user WHERE username= ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, DBUsername);
        ResultSet rs = pst.executeQuery();
        rs.next();
        byte[] importedSalt = rs.getBytes(1);
        return importedSalt;
    }

    boolean isHashedPasswordCorrect(String DBUsername, byte[] hashedPassword) throws SQLException {
        String query = "SELECT hashedPassword FROM user WHERE username= ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, DBUsername);
        ResultSet rs = pst.executeQuery();
        rs.next();
        byte[] importedHashedPassword = rs.getBytes(1);
        return Arrays.equals(hashedPassword, importedHashedPassword);
        //return hashedPassword.equals(importedHashedPassword);
    }

    byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    byte[] generateHashedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hashedPassword = factory.generateSecret(spec).getEncoded();
        return hashedPassword;
    }

    String generateRandomString(int stringLength) {
        Random rand = new Random();
        String result = "";
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < stringLength; i++) {

            int randNum = rand.nextInt(alphabet.length());
            result += alphabet.charAt(randNum);
        }
        return result;
    }

    void clearUserTable() throws SQLException {
        String query = "DELETE FROM user";
        Statement st = connection.createStatement();
        st.execute(query);
    }
}
