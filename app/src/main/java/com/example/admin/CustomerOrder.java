package com.example.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomerOrder {
    private String orderId;
    private String customerId;
    private List<CustomerCartItem> items;
    private double subtotal;
    private double shippingCost;
    private double totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private String status; // "Processing", "Shipped", "Delivered", "Cancelled"
    private long orderDate;
    private Map<String, Object> additionalInfo;

    // Required empty constructor for Firebase
    public CustomerOrder() {
    }

    public CustomerOrder(String orderId, String customerId, List<CustomerCartItem> items,
                         double subtotal, double shippingCost, double totalAmount,
                         String shippingAddress, String paymentMethod, String status, long orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CustomerCartItem> getItems() {
        return items;
    }

    public void setItems(List<CustomerCartItem> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, Object> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Date getOrderDateAsDate() {
        return new Date(orderDate);
    }
}