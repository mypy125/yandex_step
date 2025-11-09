package com.mygitgor.cart_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "carts")
@ToString(callSuper = true, exclude = "cartItems")
@EqualsAndHashCode(callSuper = true,exclude = "cartItems")
public class Cart extends BaseEntity{

    @Column(name = "user_id")
    private String userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    private double totalSellingPrice;
    private Integer totalItem;
    private Integer totalMrpPrice;
    private Integer discount;
    private String couponCode;

    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    public void removeCartItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

    public static Cart create(String userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotalItem(0);
        cart.setTotalMrpPrice(0);
        cart.setTotalSellingPrice(0);
        cart.setDiscount(0);
        return cart;
    }
}
