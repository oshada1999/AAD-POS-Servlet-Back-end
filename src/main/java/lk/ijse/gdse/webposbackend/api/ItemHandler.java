package lk.ijse.gdse.webposbackend.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.gdse.webposbackend.api.util.Convertor;
import lk.ijse.gdse.webposbackend.dao.DaoFactory;
import lk.ijse.gdse.webposbackend.dao.DaoType;
import lk.ijse.gdse.webposbackend.dao.custom.ItemDAO;
import lk.ijse.gdse.webposbackend.dao.exception.ConstrainViolationException;
import lk.ijse.gdse.webposbackend.dto.ItemDTO;
import lk.ijse.gdse.webposbackend.dto.RespondsDTO;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

//@WebServlet(urlPatterns = "/item")
public class ItemHandler extends HttpServlet {

    private Connection connection;
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

        this.itemDAO = DaoFactory.getInstance().getDao(DaoType.ITEM,connection);
        this.convertor = new Convertor();
    }

    private boolean handleValidation(ItemDTO itemDTO) {

        if (itemDTO.getItemCode() == null || !itemDTO.getItemCode().matches("^(R)([0-9]{2,})$"))
            throw new ConstrainViolationException("Invalid item code !");

        if (itemDTO.getDescription() == null || !itemDTO.getDescription().matches("^([\\w ]{1,})"))
            throw new ConstrainViolationException("Invalid item description !");

        if (itemDTO.getUnitPrice() == null || !String.valueOf(itemDTO.getUnitPrice()).matches("^\\d|.+$"))
            throw new ConstrainViolationException("Invalid item unit price !");

        if (itemDTO.getQtyOnHand() < 0 || !String.valueOf(itemDTO.getQtyOnHand()).matches("^\\d+$"))
            throw new ConstrainViolationException("Invalid item qty !");

        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        resp.setContentType("application/json");

        Jsonb jsonb = JsonbBuilder.create();

        try {

            List<ItemDTO> list = itemDAO.getAll().stream().map(item -> convertor.formItem(item)).
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
        ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);

        System.out.println(itemDTO);

        try {

            if (handleValidation(itemDTO)) {

                if (itemDAO.save(convertor.toItem(itemDTO)) != null) {

                    jsonb.toJson(new RespondsDTO(200, "Successfully added !", ""), resp.getWriter());
                }
            }
        } catch (SQLException | ConstrainViolationException e) {
            jsonb.toJson(new RespondsDTO(400, "Error !", e.getLocalizedMessage()), resp.getWriter());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        resp.setContentType("application/json");

        Jsonb jsonb = JsonbBuilder.create();
        ItemDTO itemDTO = jsonb.fromJson(req.getReader(), ItemDTO.class);

        try {

            if (handleValidation(itemDTO)) {

                if (itemDAO.update(convertor.toItem(itemDTO)) != null) {

                    jsonb.toJson(new RespondsDTO(200, "Successfully update !", ""), resp.getWriter());
                }
            }
        } catch (SQLException | ConstrainViolationException e) {
            jsonb.toJson(new RespondsDTO(400, "Error !", e.getLocalizedMessage()), resp.getWriter());
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        resp.setContentType("application/json");

        Jsonb jsonb = JsonbBuilder.create();
        String itemCode = req.getParameter("itemCode");

        try {

            if (itemCode == null || !itemCode.matches("^(R)([0-9]{2,})$"))
                throw new ConstrainViolationException("Invalid item code !");

            itemDAO.delete(itemCode) ;

            jsonb.toJson(new RespondsDTO(200, "Successfully deleted !", ""), resp.getWriter());


        } catch (SQLException | ConstrainViolationException e) {
            jsonb.toJson(new RespondsDTO(400, "Error !", e.getLocalizedMessage()), resp.getWriter());
            e.printStackTrace();
        }
    }
}
