package com.mygitgor.cart_service.service.impl;

import com.mygitgor.cart_service.client.ProductClient;
import com.mygitgor.cart_service.domain.CartItem;
import com.mygitgor.cart_service.dto.ProductDto;
import com.mygitgor.cart_service.repository.CartItemRepository;
import com.mygitgor.cart_service.service.CartItemService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    @Override
    public CartItem updateCartItem(UUID userId, UUID cartItemId, CartItem cartItem) {
        CartItem existingItem = findCartItemById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("CartItem with id '%s' not found", cartItemId)));

        validateOwnership(existingItem, userId);

        ProductDto product = productClient.getProductById(existingItem.getProductId());
        Integer quantity = cartItem.getQuantity();

        validateProductForCart(product, quantity);

        existingItem.setQuantity(quantity);
        existingItem.setMrpPrice(quantity * product.getMrpPrice());
        existingItem.setSellingPrice(quantity * product.getSellingPrice());

        return cartItemRepository.save(existingItem);
    }

    @Override
    public void removeCartItem(UUID userId, UUID cartItemId) {
        CartItem existingItem = findCartItemById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("CartItem with id '%s' not found", cartItemId)));

        validateOwnership(existingItem, userId);
        cartItemRepository.delete(existingItem);
    }

    @Override
    public Optional<CartItem> findCartItemById(UUID id) {
        return cartItemRepository.findById(id);
    }

    private boolean existsById(UUID id) {
        return cartItemRepository.findById(id).isPresent();
    }

    private void validateOwnership(CartItem item, UUID userId) {
        String cartUserId = item.getCart().getUserId();
        if (!cartUserId.equals(userId.toString())) {
            throw new SecurityException("You cannot modify another user's cart item");
        }
    }

    private void validateProductForCart(ProductDto product, Integer requestedQuantity) {
        if (product.getMrpPrice() == null || product.getSellingPrice() == null) {
            throw new IllegalArgumentException("Product price information is missing");
        }

        Integer availableQuantity = product.getQuantity();
        if (availableQuantity == null) {
            throw new IllegalArgumentException("Product stock information is unavailable");
        }

        if (availableQuantity < requestedQuantity) {
            throw new IllegalArgumentException(
                    String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                            product.getTitle(), availableQuantity, requestedQuantity)
            );
        }

        if (product.getMrpPrice() <= 0 || product.getSellingPrice() <= 0) {
            throw new IllegalArgumentException("Product prices must be positive");
        }

        if (product.getSellingPrice() > product.getMrpPrice()) {
            throw new IllegalArgumentException("Selling price cannot be higher than MRP price");
        }
    }
}
