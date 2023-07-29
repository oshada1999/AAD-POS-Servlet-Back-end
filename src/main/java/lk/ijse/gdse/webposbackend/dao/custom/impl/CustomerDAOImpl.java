package lk.ijse.gdse.webposbackend.dao.custom.impl;

import lk.ijse.gdse.webposbackend.dao.custom.CustomerDAO;
import lk.ijse.gdse.webposbackend.dao.exception.ConstrainViolationException;
import lk.ijse.gdse.webposbackend.dao.util.DBUtil;
import lk.ijse.gdse.webposbackend.entity.Customer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAOImpl implements CustomerDAO {

    private Connection connection;

    public CustomerDAOImpl(Connection connection) {

        this.connection = connection;
    }

    @Override
    public List<Customer> getAll() throws SQLException {

        return getList(DBUtil.executeQuery(connection, "SELECT * FROM Customer"));

    }

    @Override
    public Customer save(Customer customer) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "INSERT INTO Customer VALUES(?,?,?,?)", customer.getCustomerID(),
                customer.getName(), customer.getAddress(), customer.getContact()))
            throw new ConstrainViolationException("Failed to save customer !");

        return customer;

    }

    @Override
    public Customer update(Customer customer) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "UPDATE Customer SET name=?, address=?, contact=? WHERE customerID=?",
                customer.getName(), customer.getAddress(), customer.getContact(), customer.getCustomerID()))
            throw new ConstrainViolationException("Failed to update customer !");

        return customer;

    }

    @Override
    public void delete(String pk) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "DELETE FROM Customer WHERE customerID=?", pk))
            throw new ConstrainViolationException("Failed to delete customer !");

    }

    private List<Customer> getList(ResultSet resultSet) throws SQLException {

        List<Customer> customerList = new ArrayList<>();

        while (resultSet.next()) {

            customerList.add(new Customer(resultSet.getString("customerID"), resultSet.getString("name"),
                    resultSet.getString("address"), resultSet.getString("contact")));
        }

        return customerList;
    }
}
