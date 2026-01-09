/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clienthandl;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientController {

    // -------------------- DB Config --------------------
    private final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XE";
    private final String USER = "hr";
    private final String PASS = "hr";

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    // -------------------- FXML Elements --------------------
    // labels
    @FXML
    private Label lblcontract;
    @FXML
    private Label lblclient;
    @FXML
    private Label lblstart;
    @FXML
    private Label lblend;
    @FXML
    private Label lbltotal;
    @FXML
    private Label lbldeposit;
    @FXML
    private Label lblpayment;

    // Buttons
    @FXML
    private Button btnNew;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnFirst;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnLast;

    // -------------------- Actions Contollers --------------------
    // initializations
    @FXML
    private void initialize() {
        connectDB();
        loadResultSet();

        try {
            if (rs != null && rs.first()) {
                displayContract();
            }
        } catch (SQLException e) {
        }
    }

    // Connection Method
    private void connectDB() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,//can move forward and backward
                    ResultSet.CONCUR_READ_ONLY); //No updates         

            System.out.println("Connected To DB Successfully..!!!");
        } catch (SQLException ex) {
        }
    }

    // Load Data From Database
    private void loadResultSet() {
        try {
            String sql = "SELECT * FROM CONTRACTS ORDER BY CONTRACT_ID";
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {

        }
    }

    // Methods for Labels Action
    private void displayContract() {
        try {
            if (rs != null && !rs.isClosed()) {
                lblclient.setText(String.valueOf(rs.getInt("CLIENT_ID")));
                lblcontract.setText(String.valueOf(rs.getInt("CONTRACT_ID")));
                lblstart.setText(String.valueOf(rs.getDate("CONTRACT_STARTDATE").toString()));
                lblend.setText(String.valueOf(rs.getDate("CONTRACT_ENDDATE").toString()));
                lbltotal.setText(String.valueOf(rs.getFloat("CONTRACT_TOTAL_FEES")));
                lbldeposit.setText(String.valueOf(rs.getFloat("CONTRACT_DEPOSIT_FEES")));
                lblpayment.setText(String.valueOf(rs.getString("CONTRACT_PAYMENT_TYPE")));
            }
        } catch (SQLException ex) {
        }
    }

    // -------------------- Buttons Actions --------------------
    // Get First Client
    @FXML
    private void onFirstClick() {
        try {
            if (rs.first()) {
                displayContract();
            }
        } catch (SQLException ex) {
        }
    }

    //Get Last Client
    @FXML
    private void onLastClick() {
        try {
            if (rs.last()) {
                displayContract();
            }
        } catch (SQLException ex) {
        }
    }

    //Get Next Client
    @FXML
    private void onNextClick() {
        try {
            if (rs.next()) {
                displayContract();
            }
        } catch (SQLException ex) {
        }
    }

    //Get previous Client
    @FXML
    private void onPreviousClick() {
        try {
            if (rs.previous()) {
                displayContract();
            }
        } catch (SQLException ex) {
        }
    }

    //Add New Client
    @FXML
    private void onNewClick() {
        try {
            FXMLLoader loader
                    = new FXMLLoader(getClass().getResource("NewClient.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Contract");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Update The Parent Again
            loadResultSet();
            if (rs.last()) {
                displayContract();
            }

        } catch (IOException | SQLException e) {
        }
    }

    //Delete Client
    @FXML
    private void onDeleteClick() {
        // Get the currently displayed contract ID
        String contractIdStr = lblcontract.getText();

        if (contractIdStr == null || contractIdStr.isEmpty()) {
            System.out.println("No contract selected to delete.");
            return;
        }

        int contractId = Integer.parseInt(contractIdStr);

        // Confirm deletion with the user
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Contract");
        alert.setHeaderText("Are you sure you want to delete this contract?");
        alert.setContentText("Contract ID: " + contractId);

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                String sql = "DELETE FROM CONTRACTS WHERE CONTRACT_ID = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, contractId);
                int rowsDeleted = ps.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Contract deleted successfully.");

                    // Optionally refresh the result set and UI
                    loadResultSet();       // reload contracts from DB
                    if (rs != null && rs.first()) {
                        displayContract();
                    }
                } else {
                    System.out.println("No contract found with ID " + contractId);
                }

            } catch (SQLException e) {
            }
        }
    }

    // -------------------- Close Everything --------------------
    public void closeConnection() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
            System.out.println("DB connection closed.");
        } catch (SQLException e) {

        }
    }
}
