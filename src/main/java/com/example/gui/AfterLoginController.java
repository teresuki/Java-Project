package com.example.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AfterLoginController implements Initializable {

    @FXML
    private Button button_logout;
    private Button button_book;
    private Button button_exportpdf;
    private Button button_editprofile;

    @FXML
    private Label label_welcome;

    public AfterLoginController(Button button_book) {
        this.button_book = button_book;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBControl.changeScene(event "sample.fxml", "Log in!", null);
            }
        };
    }

    public static void setUserInformation(String username){
        label_welcome.setText("Welcome" + username + "!");

    }


}
