package lk.ijse.gdse.webposbackend.api.util;

import lk.ijse.gdse.webposbackend.dto.CustomerDTO;
import lk.ijse.gdse.webposbackend.dto.ItemDTO;
import lk.ijse.gdse.webposbackend.dto.OrderDTO;
import lk.ijse.gdse.webposbackend.dto.OrderDetailsDTO;
import lk.ijse.gdse.webposbackend.entity.Customer;
import lk.ijse.gdse.webposbackend.entity.Item;
import lk.ijse.gdse.webposbackend.entity.Order;

import java.sql.Date;
import java.util.ArrayList;

public class Convertor {

    public CustomerDTO fromCustomer(Customer customer){
        return new CustomerDTO(customer.getCustomerID(), customer.getName(), customer.getAddress(), customer.getContact());
    }

    public Customer toCustomer(CustomerDTO customerDTO){
        return new Customer(customerDTO.getId(), customerDTO.getName(), customerDTO.getAddress(), customerDTO.getContact());
    }

    public ItemDTO formItem(Item item){
        return new ItemDTO(item.getItemCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand());
    }

    public Item toItem(ItemDTO itemDTO){
        return new Item(itemDTO.getItemCode(), itemDTO.getDescription(), itemDTO.getUnitPrice(), itemDTO.getQtyOnHand());
    }

    public OrderDTO fromOrder(Order order){
        return new OrderDTO(order.getOrderID(), order.getCustomerID(), String.valueOf(order.getOrderDate()), new ArrayList<>());
    }

    public Order toOrder(OrderDTO orderDTO){
        return new Order(orderDTO.getOrderID(), orderDTO.getCustomerID(), Date.valueOf(orderDTO.getOrderDate()));
    }

    public OrderDetailsDTO fromOrderDetails(lk.ijse.gdse.webposbackend.entity.OrderDetails orderDetails){
        return new OrderDetailsDTO(orderDetails.getOrderID(), orderDetails.getItemCode(), orderDetails.getUnitPrice(), orderDetails.getQty(), orderDetails.getTotal());
    }

    public lk.ijse.gdse.webposbackend.entity.OrderDetails toOrderDetails(OrderDetailsDTO orderDetailsDTO){
        return new lk.ijse.gdse.webposbackend.entity.OrderDetails(orderDetailsDTO.getOrderID(), orderDetailsDTO.getItemCode(), orderDetailsDTO.getUnitPrice(), orderDetailsDTO.getQty(), orderDetailsDTO.getTotal());
    }
}
