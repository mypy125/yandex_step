package com.mygitgor.order_service.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "carts")
@ToString(callSuper = true, exclude = "cartItems")
@EqualsAndHashCode(callSuper = true, exclude = "cartItems")
public class Cart extends BaseEntity{
    private UUID userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CartItem> cartItems = new HashSet<>();

    private double totalSellingPrice;
    private int totalItem;
    private int totalMrpPrice;
    private int discount;
    private String couponCode;

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

    public static Cart create(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalItem(0);
        cart.setTotalMrpPrice(0);
        cart.setTotalSellingPrice(0);
        cart.setDiscount(0);
        return cart;
    }
}
