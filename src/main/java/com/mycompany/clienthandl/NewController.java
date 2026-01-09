package com.mycompany.clienthandl;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;

public class NewController {

    @FXML private ComboBox<Integer> cmbClient;
    @FXML private ComboBox<String> cmbPayment;
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private TextField txtTotal;
    @FXML private TextField txtDeposit;
    @FXML private Label lblMsg;

    private Connection conn;

    @FXML
    public void initialize() {
        connectDB();

        cmbPayment.getItems().addAll(
                "ANNUAL", "HALF_ANNUAL", "QUARTER", "MONTHLY"
        );

        loadClients();
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521/XE",
                    "hr", "hr"
            );
            conn.setAutoCommit(false);
        } catch (SQLException e) {
        }
    }

    private void loadClients() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT CLIENT_ID FROM CLIENTS")) {

            while (rs.next()) {
                cmbClient.getItems().add(rs.getInt(1));
            }
        } catch (SQLException e) {
        }
    }

    @FXML
    private void saveContract() {
        try {
            String sql =
                "INSERT INTO CONTRACTS " +
                "(CONTRACT_ID, CLIENT_ID, CONTRACT_STARTDATE, CONTRACT_ENDDATE, " +
                " CONTRACT_TOTAL_FEES, CONTRACT_DEPOSIT_FEES, CONTRACT_PAYMENT_TYPE) " +
                "VALUES (SEQ_CONTRACT_ID.NEXTVAL, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, cmbClient.getValue());
            ps.setDate(2, Date.valueOf(dpStart.getValue()));
            ps.setDate(3, Date.valueOf(dpEnd.getValue()));
            ps.setDouble(4, Double.parseDouble(txtTotal.getText()));
            ps.setDouble(5, Double.parseDouble(txtDeposit.getText()));
            ps.setString(6, cmbPayment.getValue());

            ps.executeUpdate();
            conn.commit();

            lblMsg.setText("Contract saved & installments updated");

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            lblMsg.setText("Error occurred");
        }
    }
}
