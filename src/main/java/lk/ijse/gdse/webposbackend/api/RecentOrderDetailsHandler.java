package lk.ijse.gdse.webposbackend.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.gdse.webposbackend.dao.DaoFactory;
import lk.ijse.gdse.webposbackend.dao.DaoType;
import lk.ijse.gdse.webposbackend.dao.custom.QueryDAO;
import lk.ijse.gdse.webposbackend.dto.RecentOrderDetailsDTO;
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

//@WebServlet(urlPatterns = "/query")
public class RecentOrderDetailsHandler extends HttpServlet {

    private Connection connection;
    private QueryDAO queryDAO;

    @Override
    public void init() throws ServletException {

        try {

            InitialContext initialContext = new InitialContext();
            DataSource pool = (DataSource) initialContext.lookup("java:comp/env/jdbc/Web_Pos");
            this.connection = pool.getConnection();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.queryDAO = DaoFactory.getInstance().getDao(DaoType.QUERY, connection);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        resp.setContentType("application/json");

        Jsonb jsonb = JsonbBuilder.create();

        try {

            List<RecentOrderDetailsDTO> all = queryDAO.getAll();

            jsonb.toJson(new RespondsDTO(400, "Done !", all), resp.getWriter());

        }catch (SQLException e){

            jsonb.toJson(new RespondsDTO(400, "Error", e.getLocalizedMessage()), resp.getWriter());
            e.printStackTrace();
        }
    }
}
