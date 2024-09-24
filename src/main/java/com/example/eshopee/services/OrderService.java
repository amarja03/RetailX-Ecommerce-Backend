package com.example.eshopee.services;

import java.util.List;

import com.example.eshopee.payloads.OrderDTO;
import com.example.eshopee.payloads.OrderResponse;

public interface OrderService {
	
	OrderDTO placeOrder(String emailId, Long cartId, String paymentMethod);
	
	OrderDTO getOrder(String emailId, Long orderId);
	
	List<OrderDTO> getOrdersByUser(String emailId);
	
	OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	OrderDTO updateOrderStatus(String emailId, Long orderId, String orderStatus);
}
