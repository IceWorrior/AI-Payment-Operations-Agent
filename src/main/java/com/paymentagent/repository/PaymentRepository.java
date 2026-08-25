package com.paymentagent.repository;

import com.paymentagent.database.Database;
import com.paymentagent.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    public List<Payment> findAll() {

        List<Payment> payments = new ArrayList<>();

        String sql =
                "SELECT id, amount, currency, status, payment_method " +
                "FROM payments " +
                "ORDER BY created_at";

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                payments.add(mapPayment(result));
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to fetch payments",
                    e
            );
        }

        return payments;
    }

    public Payment findById(String id) {

        String sql =
                "SELECT id, amount, currency, status, payment_method " +
                "FROM payments " +
                "WHERE id = ?";

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, id);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapPayment(result);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find payment",
                    e
            );
        }

        return null;
    }

    public Payment save(Payment payment) {

        String sql =
                "INSERT INTO payments " +
                "(id, amount, currency, status, payment_method) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, payment.getId());
            statement.setDouble(2, payment.getAmount());
            statement.setString(3, payment.getCurrency());
            statement.setString(4, payment.getStatus());
            statement.setString(5, payment.getPaymentMethod());

            statement.executeUpdate();

            return payment;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to save payment",
                    e
            );
        }
    }

    public List<Payment> findByFilters(
            String status,
            String paymentMethod,
            Double minAmount,
            Double maxAmount) {

        List<Payment> payments = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, amount, currency, status, payment_method " +
                "FROM payments WHERE 1=1"
        );

        List<Object> parameters = new ArrayList<>();

        if (status != null && !status.isBlank()) {

            sql.append(" AND status = ?");
            parameters.add(status);
        }

        if (paymentMethod != null && !paymentMethod.isBlank()) {

            sql.append(" AND payment_method = ?");
            parameters.add(paymentMethod);
        }

        if (minAmount != null) {

            sql.append(" AND amount >= ?");
            parameters.add(minAmount);
        }

        if (maxAmount != null) {

            sql.append(" AND amount <= ?");
            parameters.add(maxAmount);
        }

        sql.append(" ORDER BY created_at DESC");

        try (
                Connection connection = Database.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < parameters.size(); i++) {

                statement.setObject(
                        i + 1,
                        parameters.get(i)
                );
            }

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    payments.add(mapPayment(result));
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to filter payments",
                    e
            );
        }

        return payments;
    }

    private Payment mapPayment(ResultSet result)
            throws SQLException {

        return new Payment(
                result.getString("id"),
                result.getDouble("amount"),
                result.getString("currency"),
                result.getString("status"),
                result.getString("payment_method")
        );
    }
}