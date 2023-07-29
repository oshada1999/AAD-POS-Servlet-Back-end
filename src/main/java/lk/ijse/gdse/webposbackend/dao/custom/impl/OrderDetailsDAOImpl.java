package lk.ijse.gdse.webposbackend.dao.custom.impl;

import lk.ijse.gdse.webposbackend.dao.custom.OrderDetailsDAO;
import lk.ijse.gdse.webposbackend.dao.exception.ConstrainViolationException;
import lk.ijse.gdse.webposbackend.dao.util.DBUtil;
import lk.ijse.gdse.webposbackend.entity.OrderDetails;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDAOImpl implements OrderDetailsDAO {

    private Connection connection;

    public OrderDetailsDAOImpl(Connection connection) {

        this.connection = connection;
    }

    @Override
    public List<OrderDetails> getAll() throws SQLException {

        return getList(DBUtil.executeQuery(connection, "SELECT*FROM OrderDetails"));

    }

    @Override
    public OrderDetails save(OrderDetails orderDetails) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "INSERT INTO OrderDetails VALUES(?,?,?,?,?)", orderDetails.getOrderID(),
                orderDetails.getItemCode(), orderDetails.getUnitPrice(), orderDetails.getQty(), orderDetails.getTotal()))
            throw new ConstrainViolationException("Failed to save order details !");

        return orderDetails;
    }

    @Override
    public OrderDetails update(OrderDetails orderDetails) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "UPDATE OrderDetails SET unitPrice=?, qty=?, total=? WHERE orderID=? AND itemCode=?",
                orderDetails.getUnitPrice(), orderDetails.getQty(), orderDetails.getTotal(), orderDetails.getOrderID(),
                orderDetails.getItemCode())) throw new ConstrainViolationException("Failed to update order details !");

        return orderDetails;
    }

    @Override
    public void delete(String pk) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "DELETE FROM OrderDetails WHERE orderID=?", pk))
            throw new ConstrainViolationException("Failed to delete order details !");
    }

    private List<OrderDetails> getList(ResultSet resultSet) throws SQLException {

        List<OrderDetails> orderDetailsList = new ArrayList<>();

        while (resultSet.next()) {

            orderDetailsList.add(new OrderDetails(resultSet.getString("OrderID"), resultSet.getString("itemCode"),
                    resultSet.getDouble("unitPrice"), resultSet.getInt("qty"), resultSet.getDouble("total")));
        }

        return orderDetailsList;
    }
}
