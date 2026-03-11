package com.example.admin;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private String orderId;
    private String userId;
    private String customerName;
    private String status;
    private double total;
    private String shippingAddress;
    private long orderDate;
    private Map<String, OrderItem> items;

    // Default constructor required for Firebase
    public Order() {
        this.orderDate = System.currentTimeMillis();
        this.status = "pending";
        this.items = new HashMap<>();
    }

    public Order(String orderId, String userId, String customerName, double total, String shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.total = total;
        this.shippingAddress = shippingAddress;
        this.orderDate = System.currentTimeMillis();
        this.status = "pending";
        this.items = new HashMap<>();
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public Map<String, OrderItem> getItems() {
        return items;
    }

    public void setItems(Map<String, OrderItem> items) {
        this.items = items;
    }

    public void addItem(String productId, OrderItem item) {
        if (this.items == null) {
            this.items = new HashMap<>();
        }
        this.items.put(productId, item);
    }

    // Inner class for order items
    public static class OrderItem {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
        private String imageUrl;

        // Default constructor required for Firebase
        public OrderItem() {
        }

        public OrderItem(String productId, String productName, double price, int quantity, String imageUrl) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.imageUrl = imageUrl;
        }

        // Getters and setters
        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}