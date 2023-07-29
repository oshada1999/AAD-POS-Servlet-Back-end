package lk.ijse.gdse.webposbackend.dao;

import lk.ijse.gdse.webposbackend.dao.custom.impl.*;

import java.sql.Connection;

public class DaoFactory {

    private static DaoFactory daoFactory;

    private DaoFactory(){}

    public static DaoFactory getInstance(){

        return daoFactory == null ? (daoFactory = new DaoFactory()) : daoFactory;
    }

    public <T extends SuperDAO>T getDao(DaoType daoType, Connection connection){

        switch (daoType){
            case CUSTOMER:
                return (T) new CustomerDAOImpl(connection);
            case ITEM:
                return (T) new ItemDAOImpl(connection);
            case ORDER:
                return (T) new OrderDAOImpl(connection);
            case ORDERDETAILS:
                return (T) new OrderDetailsDAOImpl(connection);
            case QUERY:
                return (T) new QueryDAOImpl(connection);
            default:
                return null;
        }
    }
}
