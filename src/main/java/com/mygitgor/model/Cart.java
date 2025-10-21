package com.mygitgor.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Cart {
    private String id;
    private String userId;
    private List<CartItem>items;
    private BigDecimal amount;

    public Cart(){}
    public Cart(String id, List<CartItem> items, BigDecimal amount) {
        this.id = id;
        this.items = items;
        this.amount = amount;
    }

    public String getId(){ return id;}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id) && Objects.equals(userId, cart.userId) && Objects.equals(items, cart.items) && Objects.equals(amount, cart.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, items, amount);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", items=" + items +
                ", amount=" + amount +
                '}';
    }
}
