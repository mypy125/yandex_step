package com.mygitgor;

import com.mygitgor.model.Cart;
import com.mygitgor.model.CartItem;
import com.mygitgor.repository.CartRepository;
import com.mygitgor.sevice.CartItemService;
import com.mygitgor.sevice.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCart_shouldCreateCartAndReturnTrue() {
        String userId = "user123";
        when(cartRepository.createCart(any(Cart.class))).thenReturn(true);

        boolean result = cartService.createCart(userId);

        assertTrue(result);
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).createCart(captor.capture());

        Cart savedCart = captor.getValue();
        assertEquals(userId, savedCart.getUserId());
        assertEquals(BigDecimal.ZERO, savedCart.getAmount());
    }

    @Test
    void getUserCart_shouldReturnCartFromRepository() {
        String userId = "user123";
        Cart expectedCart = new Cart();
        expectedCart.setUserId(userId);
        when(cartRepository.getUserCart(userId)).thenReturn(expectedCart);

        Cart result = cartService.getUserCart(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(cartRepository, times(1)).getUserCart(userId);
    }

    @Test
    void updateCartAmount_shouldCalculateTotalAmount() {
        String userId = "user123";
        CartItem item1 = new CartItem();
        item1.setPrice(new BigDecimal("10.50"));
        CartItem item2 = new CartItem();
        item2.setPrice(new BigDecimal("5.25"));
        CartItem item3 = new CartItem();
        item3.setPrice(null);

        List<CartItem> items = Arrays.asList(item1, item2, item3);
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(items);

        when(cartRepository.getUserCart(userId)).thenReturn(cart);

        Cart result = cartService.updateCartAmount(userId);

        verify(cartItemService).updatePercentDiscountUser(userId, items);
        assertEquals(new BigDecimal("15.75"), result.getAmount());
    }
}
