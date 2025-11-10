package com.mygitgor.order_service.domain;

import com.mygitgor.order_service.dto.clientDto.CartItemDto;
import com.mygitgor.order_service.dto.clientDto.ProductDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_items")
@ToString(callSuper = true, exclude = "order")
@EqualsAndHashCode(callSuper = true,exclude = "order")
public class OrderItem extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private UUID productId;

    private String productTitle;
    private String productImage;
    private String productColor;
    private String productSku;

    private String size;
    private Integer quantity;
    private Integer mrpPrice;
    private Integer sellingPrice;

    private UUID sellerId;
    private String brand;

    public Integer getTotalPrice() {
        return sellingPrice != null ? sellingPrice * quantity : 0;
    }

    public Integer getTotalMrpPrice() {
        return mrpPrice != null ? mrpPrice * quantity : 0;
    }

    public static OrderItem createFromCartItem(CartItemDto cartItem, ProductDto product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(cartItem.getProductId());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setSize(cartItem.getSize());
        orderItem.setMrpPrice(cartItem.getMrpPrice());
        orderItem.setSellingPrice(cartItem.getSellingPrice());

        orderItem.setProductTitle(product.getTitle());
        orderItem.setProductImage(!product.getImages().isEmpty() ? product.getImages().get(0) : null);
        orderItem.setProductColor(product.getColor());
        orderItem.setProductSku(product.getSku());
        orderItem.setSellerId(product.getSellerId());
        orderItem.setBrand(product.getBrand());

        return orderItem;
    }

    public void validate() {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (mrpPrice == null || mrpPrice < 0) {
            throw new IllegalArgumentException("MRP price must be non-negative");
        }
        if (sellingPrice == null || sellingPrice < 0) {
            throw new IllegalArgumentException("Selling price must be non-negative");
        }
        if (sellingPrice > mrpPrice) {
            throw new IllegalArgumentException("Selling price cannot be higher than MRP price");
        }
    }

    public void updateProductInfo(ProductDto product) {
        this.productTitle = product.getTitle();
        this.productImage = !product.getImages().isEmpty() ? product.getImages().get(0) : null;
        this.productColor = product.getColor();
        this.productSku = product.getSku();
        this.sellerId = product.getSellerId();
        this.brand = product.getBrand();
    }
}
