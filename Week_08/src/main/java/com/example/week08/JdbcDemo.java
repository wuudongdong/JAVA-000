package com.example.week08;

import java.sql.*;
import java.util.Random;

public class JdbcDemo {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:13308/sharding_db", "sharding", "sharding");
        String insert = "insert into order_master (customer_id, payment_money) VALUE (?, 100.00)";
        PreparedStatement statement = connection.prepareStatement(insert);

        // 新增1000条记录
        for (int i = 1; i <= 1000; i++) {
            statement.setLong(1, new Random().nextInt(100000));
            statement.addBatch();
        }
        statement.executeBatch();

//        // 查询
//        String query = "select * from order_master";
//        Statement statement = connection.createStatement();
//        ResultSet resultSet = statement.executeQuery(query);
//        while (resultSet.next()) {
//            String id = resultSet.getString("order_id");
//            String userId = resultSet.getString("customer_id");
//            String money = resultSet.getString("payment_money");
//            System.out.println("order_id:" + id + ",userId:" + userId + ",money:" + money);
//        }

//
//        // 修改一条数据
//        String update = "update user set PASSWORD = '654321' where USER_NAME = 'kevin'";
//        Statement updateStatement = connection.createStatement();
//        updateStatement.execute(update);
//
//        // 删除数据
//        String delete = "delete from user where USER_NAME = 'kevin'";
//        Statement deleteStatement = connection.createStatement();
//        deleteStatement.executeUpdate(delete);

        connection.close();
    }
}
