package lk.ijse.gdse.webposbackend.dao.custom.impl;

import lk.ijse.gdse.webposbackend.dao.custom.ItemDAO;
import lk.ijse.gdse.webposbackend.dao.exception.ConstrainViolationException;
import lk.ijse.gdse.webposbackend.dao.util.DBUtil;
import lk.ijse.gdse.webposbackend.entity.Item;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl implements ItemDAO {

    private Connection connection;

    public ItemDAOImpl(Connection connection) {

        this.connection = connection;
    }

    @Override
    public List<Item> getAll() throws SQLException {

        return getList(DBUtil.executeQuery(connection, "SELECT * FROM Item"));

    }

    @Override
    public Item save(Item item) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "INSERT INTO Item VALUES(?,?,?,?)", item.getItemCode(),
                item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()))
            throw new ConstrainViolationException("Failed to save item !");

        return item;

    }

    @Override
    public Item update(Item item) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "UPDATE Item SET description=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?",
                item.getDescription(), item.getUnitPrice(), item.getQtyOnHand(), item.getItemCode()))
            throw new ConstrainViolationException("Failed to update item !");

        return item;
    }

    @Override
    public void delete(String pk) throws SQLException {

        if (!DBUtil.executeUpdate(connection, "DELETE FROM Item WHERE ItemCode=?", pk))
            throw new ConstrainViolationException("Failed to delete item !");

    }

    @Override
    public boolean reduceItemQty(String itemCode, double qty) throws SQLException {

        return DBUtil.executeUpdate(connection, "UPDATE Item SET qtyOnHand=qtyOnHand-? WHERE itemCode=?", qty, itemCode);

    }

    private List<Item> getList(ResultSet resultSet) throws SQLException {

        List<Item> itemList = new ArrayList<>();

        while (resultSet.next()) {

            itemList.add(new Item(resultSet.getString("itemCode"), resultSet.getString("description"),
                    resultSet.getDouble("unitPrice"), resultSet.getInt("qtyOnHand")));
        }

        return itemList;
    }
}
