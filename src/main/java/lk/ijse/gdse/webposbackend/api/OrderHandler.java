package lk.ijse.gdse.webposbackend.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.gdse.webposbackend.api.util.Convertor;
import lk.ijse.gdse.webposbackend.dao.DaoFactory;
import lk.ijse.gdse.webposbackend.dao.DaoType;
import lk.ijse.gdse.webposbackend.dao.custom.ItemDAO;
import lk.ijse.gdse.webposbackend.dao.custom.OrderDAO;
import lk.ijse.gdse.webposbackend.dao.custom.OrderDetailsDAO;
import lk.ijse.gdse.webposbackend.dao.exception.ConstrainViolationException;
import lk.ijse.gdse.webposbackend.dto.OrderDTO;
import lk.ijse.gdse.webposbackend.dto.OrderDetailsDTO;
import lk.ijse.gdse.webposbackend.dto.RespondsDTO;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.CharConversionException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

//@WebServlet(urlPatterns = "/order")
public class OrderHandler extends HttpServlet {

    private Connection connection;
    private OrderDAO orderDAO;
    private OrderDetailsDAO orderDetailsDAO;
    private ItemDAO itemDAO;
    private Convertor convertor;

    @Override
    public void init() throws ServletException {

        try {

            InitialContext initialContext = new InitialContext();
            DataSource pool = (DataSource) initialContext.lookup("java:comp/env/jdbc/Web_Pos");
            this.connection = pool.getConnection();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.orderDAO = DaoFactory.getInstance().getDao(DaoType.ORDER, connection);
        this.orderDetailsDAO = DaoFactory.getInstance().getDao(DaoType.ORDERDETAILS, connection);
        this.itemDAO = DaoFactory.getInstance().getDao(DaoType.ITEM, connection);
        this.convertor = new Convertor();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        resp.setContentType("application/json");

        Jsonb jsonb = JsonbBuilder.create();

        try {

            List<OrderDTO> list = orderDAO.getAll().stream().map(order -> convertor.fromOrder(order)).
                    collect(Collectors.toList());

            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            jsonb.toJson(new RespondsDTO(200, "Done", list), resp.getWriter());

        }catch (SQLException e){

            jsonb.toJson(new RespondsDTO(400, "Error", e.getLocalizedMessage()), resp.getWriter());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        resp.setContentType("application/json");

        Jsonb jsonb = JsonbBuilder.create();
        OrderDTO orderDTO = jsonb.fromJson(req.getReader(), OrderDTO.class);

        try {

            if(handleValidation(orderDTO)) {

                try {

                    connection.setAutoCommit(false);

                    if (orderDAO.save(convertor.toOrder(orderDTO)) == null)
                        throw new ConstrainViolationException("Failed to save order !");

                    for (OrderDetailsDTO orderDetailsDTO : orderDTO.getItemArray()) {

                        if (orderDetailsDAO.save(convertor.toOrderDetails(orderDetailsDTO)) == null)
                            throw new CharConversionException("Failed to save order details !");
                    }

                    for (OrderDetailsDTO orderDetailsDTO : orderDTO.getItemArray()) {

                        if (!itemDAO.reduceItemQty(orderDetailsDTO.getItemCode(), orderDetailsDTO.getQty()))
                            throw new CharConversionException("Failed to reduce item qty !");
                    }
                    connection.commit();

                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                    jsonb.toJson(new RespondsDTO(200, "Successfully order placed !", ""), resp.getWriter());

                }catch (SQLException t){
                    try {

                        connection.rollback();
                        jsonb.toJson(new RespondsDTO(400, "Error !", t.getLocalizedMessage()), resp.getWriter());
                        t.printStackTrace();

                    }catch (SQLException e){

                        jsonb.toJson(new RespondsDTO(400, "Error !", e.getLocalizedMessage()), resp.getWriter());
                        t.printStackTrace();

                    }finally {

                        try {

                            connection.setAutoCommit(true);

                        }catch (SQLException e){

                             e.printStackTrace();
                        }
                    }
                }
            }

        }catch (ConstrainViolationException e ){

            jsonb.toJson(new RespondsDTO(400, "Error !", e.getLocalizedMessage()), resp.getWriter());
            e.printStackTrace();
        }
    }

    private boolean handleValidation(OrderDTO orderDTO){

        if (orderDTO.getOrderID() == null || !orderDTO.getOrderID().matches("^(MD)(-)([0-9]{2,})$"))
            throw new ConstrainViolationException("Invalid order id !");

        if (orderDTO.getCustomerID() == null || !orderDTO.getCustomerID().matches("^(C)([0-9]{2,})$"))
            throw new ConstrainViolationException("Invalid customer id !");

        if (orderDTO.getOrderDate() == null || !orderDTO.getOrderDate().matches("^\\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2][0-9]|3[0-1])$"))
            throw new ConstrainViolationException("Invalid order date !");

        if (orderDTO.getItemArray() == null || orderDTO.getItemArray().isEmpty())
            throw new ConstrainViolationException("Order details are empty !");

        for (OrderDetailsDTO orderDetailsDTO: orderDTO.getItemArray()){

            if (orderDTO.getOrderID() == null || !orderDTO.getOrderID().matches("^(MD)(-)([0-9]{2,})$"))
                throw new ConstrainViolationException("Invalid order details order id !");

            if (orderDetailsDTO.getItemCode() == null || !orderDetailsDTO.getItemCode().matches("^(R)([0-9]{2,})$"))
                throw new ConstrainViolationException("Invalid order details item code !");

            if (String.valueOf(orderDetailsDTO.getUnitPrice()) == null || !String.valueOf(orderDetailsDTO.getUnitPrice()).matches("^\\d|.+$"))
                throw new ConstrainViolationException("Invalid item unit price !");

            if (orderDetailsDTO.getQty() < 0 || !String.valueOf(orderDetailsDTO.getQty()).matches("^\\d+$"))
                throw new ConstrainViolationException("Invalid item qty !");
        }
        return true;
    }
}
