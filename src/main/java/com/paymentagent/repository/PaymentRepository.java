package com.paymentagent.repository;

import com.paymentagent.database.Database;
import com.paymentagent.model.Payment;
import com.paymentagent.model.PaymentMethodStats;
import com.paymentagent.model.PaymentStats;

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

    public PaymentStats getStats(){

        String sql = 
            "SELECT " +
            "COUNT(*) AS total_payments, " +
            "COUNT(*) FILTER (WHERE status = 'SUCCESS') AS successful_payments, " +
            "COUNT(*) FILTER (WHERE status = 'FAILED') AS failed_payments, " +
            "COUNT(*) FILTER (WHERE status = 'PENDING') AS pending_payments, " +
            "COALESCE(SUM(amount), 0) AS total_amount, " +
            "COALESCE(SUM(amount) FILTER (WHERE status = 'FAILED'), 0) AS failed_amount " +
            "FROM payments";

        try(
            Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
        ){

            if(result.next()){

                int totalPayments = result.getInt("total_payments");

                int successfulPayments = result.getInt("successful_payments");

                int failedPayments = result.getInt("failed_payments");

                int pendingPayments = result.getInt("pending_payments");

                double totalAmount = result.getDouble("total_amount");

                double failedAmount = result.getDouble("failed_amount");

                double failureRate = 0;

                if(totalPayments > 0 ){

                    failureRate = ((double)
                        failedPayments/totalPayments) * 100;

                }

                return new PaymentStats(
                    totalPayments,
                    successfulPayments,
                    failedPayments,
                    pendingPayments,
                    totalAmount,
                    failedAmount,
                    failureRate
                );

            }

        }
        catch (SQLException e){

            throw new RuntimeException(
                "Failed to calculate payment statistics",
                e
            );

        }

        return new PaymentStats(
            0,
            0,
            0,
            0,
            0,
            0,
            0
        );
    }

    public List<PaymentMethodStats> getPaymentMethodStats(){

        List<PaymentMethodStats> stats = new ArrayList<>();

        String sql = "SELECT payment_method, " +
            "COUNT(*) AS total_payments, " +
            "COUNT(*) FILTER (WHERE status = 'SUCCESS') AS successful_payments, " +
            "COUNT(*) FILTER (WHERE status = 'FAILED') AS failed_payments, " +
            "COALESCE(SUM(amount), 0) AS total_amount, " +
            "COALESCE(SUM(amount) FILTER (WHERE status = 'FAILED'), 0) AS failed_amount " +
            "FROM payments " +
            "GROUP BY payment_method " +
            "ORDER BY failed_payments DESC";
        
        try (
            Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
        ){

            while(result.next()){

                stats.add(
                    new PaymentMethodStats(
                        result.getString("payment_method"),
                        result.getInt("total_payments"),
                        result.getInt("successful_payments"),
                        result.getInt("failed_payments"),
                        result.getDouble("total_amount"),
                        result.getDouble("failed_amount")
                    )
                );
            }

        }
        catch(Exception e){

            throw new RuntimeException(
                "Failed to calculate payment method statistics",
                e
            );
        }

        return stats;

    }

}