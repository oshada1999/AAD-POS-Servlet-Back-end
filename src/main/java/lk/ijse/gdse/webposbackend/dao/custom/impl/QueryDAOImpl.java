package lk.ijse.gdse.webposbackend.dao.custom.impl;

import lk.ijse.gdse.webposbackend.dao.custom.QueryDAO;
import lk.ijse.gdse.webposbackend.dao.util.DBUtil;
import lk.ijse.gdse.webposbackend.dto.ItemDTO;
import lk.ijse.gdse.webposbackend.dto.RecentOrderDetailsDTO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryDAOImpl implements QueryDAO {

    private Connection connection;

    public QueryDAOImpl(Connection connection) {

        this.connection = connection;
    }

    @Override
    public List<RecentOrderDetailsDTO> getAll() throws SQLException {

        ResultSet rst1 = DBUtil.executeQuery(connection, "SELECT Orders.orderID, Orders.customerID, Orders.date, customer.name FROM Orders\n" +
                "INNER JOIN Customer ON orders.customerID = customer.customerID ORDER BY orders.orderID ASC");

        ResultSet rst2 = DBUtil.executeQuery(connection, "SELECT Orders.orderID, orderdetails.qty, orderdetails.total, item.itemCode, item.description,item.unitPrice FROM Orders\n" +
                "INNER JOIN Customer ON orders.customerID = customer.customerID INNER JOIN orderdetails on orders.orderID = orderdetails.orderID\n" +
                "INNER JOIN item on orderdetails.itemCode = item.itemCode ORDER BY orders.orderID ASC");

        List<RecentOrderDetailsDTO> recentOrderDetailList = new ArrayList<>();

        while (rst1.next()) {
            RecentOrderDetailsDTO dto = new RecentOrderDetailsDTO(rst1.getString("orderID"), rst1.getString("customerID"), rst1.getString("name"),
                    rst1.getString("date"), 0, new ArrayList<>());

            rst2.beforeFirst();

            while (rst2.next()) {
                if (dto.getOrderID().equalsIgnoreCase(rst2.getString("orderID"))) {
                    dto.setTotal(dto.getTotal() + rst2.getDouble("total"));

                    dto.getItemList().add(new ItemDTO(rst2.getString("itemCode"), rst2.getString("description"),
                            rst2.getDouble("unitPrice"), rst2.getInt("qty")));
                }
            }
            recentOrderDetailList.add(dto);
        }

        return recentOrderDetailList;
    }
}
