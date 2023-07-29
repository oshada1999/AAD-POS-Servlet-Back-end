package lk.ijse.gdse.webposbackend.dao.custom.impl;

import lk.ijse.gdse.webposbackend.dao.custom.OrderDAO;
import lk.ijse.gdse.webposbackend.dao.exception.ConstrainViolationException;
import lk.ijse.gdse.webposbackend.dao.util.DBUtil;
import lk.ijse.gdse.webposbackend.entity.Order;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    private Connection connection;

    public OrderDAOImpl(Connection connection) {

        this.connection = connection;
    }

    @Override
    public List<Order> getAll() throws SQLException {

        return getList(DBUtil.executeQuery(connection, "SELECT * FROM Orders"));

    }

    @Override
    public Order save(Order order) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "INSERT INTO Orders VALUES(?,?,?)", order.getOrderID(),
                order.getCustomerID(), order.getOrderDate()))
            throw new ConstrainViolationException("Failed to save order !");

        return order;

    }

    @Override
    public Order update(Order order) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "UPDATE Orders SET customerID=?, date=? WHERE orderID=?",
                order.getCustomerID(), order.getOrderDate(), order.getOrderID()))
            throw new ConstrainViolationException("Failed to update order !");

        return order;

    }

    @Override
    public void delete(String pk) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "DELETE FROM Orders WHERE OrderID=?", pk))
            throw new ConstrainViolationException("Failed to delete Order !");

    }

    private List<Order> getList(ResultSet resultSet) throws SQLException {

        List<Order> orderList = new ArrayList<>();

        while (resultSet.next()) {

            orderList.add(new Order(resultSet.getString("OrderID"), resultSet.getString("customerID"),
                    resultSet.getDate("date")));
        }

        return orderList;
    }
}
