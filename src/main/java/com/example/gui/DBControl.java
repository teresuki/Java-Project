package com.example.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ConnectException;

public class DBControl {


    //Change event
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username){
        Parent_root = "null";

        if (username != null){
            try{
                FXMLLoader loader = new FXMLLoader(DBControl.class.getResource(fxmlFile));
                root = loader.load();
                AfterLoginController afterLoginController = loader.getController();
                AfterLoginController.setUserInformation(username);
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(DBControl.class.getResource(fxmlFile));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }


    // Sign in for a new account
    public static void signUpUser(ActionEvent event, String username, String password){
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try{
            connection = DriverManager.getConnection("jbdc:mysql://localhost:3306/Ehealth", "root", "toor");
            psCheckUserExists = connection.preparedStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()){
                System.out.println("User already exist!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username.");
                alert.show();
            } else{
                psInsert = connection.preparedStatement("INSERT INTO users (username, password VALUES (?, ?)");
                psInsert = setString(1, username);
                psInsert = setString(2, password);
                psInsert.executeUpdate();

                changeScene(event, "AfterLogin.fxml", "Welcome!", username);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (resultSet != null){
                try{
                    resultSet.close();
                } catch (SQLExeption e){
                    e.printStackTrace();

                }
            }
        }

    }
}
