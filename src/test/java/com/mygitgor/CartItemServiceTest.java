package com.mygitgor;

import com.mygitgor.client.PercentDiscountService;
import com.mygitgor.model.CartItem;
import com.mygitgor.sevice.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CartItemServiceTest {
    @Mock
    private PercentDiscountService percentDiscountService;

    @InjectMocks
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updatePercentDiscountUser_shouldUpdatePricesCorrectly() {
        String userId = "user123";
        when(percentDiscountService.getPercentByUser(userId)).thenReturn(20);

        CartItem item1 = new CartItem();
        item1.setPrice(new BigDecimal("100.00"));

        CartItem item2 = new CartItem();
        item2.setPrice(new BigDecimal("50.00"));

        List<CartItem> items = Arrays.asList(item1, item2);

        cartItemService.updatePercentDiscountUser(userId, items);

        verify(percentDiscountService).getPercentByUser(userId);

        assertEquals(new BigDecimal("20.00"), item1.getPrice());
        assertEquals(new BigDecimal("10.00"), item2.getPrice());
    }

    @Test
    void updatePercentDiscountUser_shouldHandleEmptyList() {
        String userId = "user123";
        when(percentDiscountService.getPercentByUser(userId)).thenReturn(10);

        List<CartItem> items = List.of();

        cartItemService.updatePercentDiscountUser(userId, items);

        verify(percentDiscountService).getPercentByUser(userId);
        assertTrue(items.isEmpty());
    }

    @Test
    void updatePercentDiscountUser_shouldThrowIfPriceIsNull() {
        String userId = "user123";
        when(percentDiscountService.getPercentByUser(userId)).thenReturn(10);

        CartItem item = new CartItem();
        item.setPrice(null);
        List<CartItem> items = List.of(item);

        assertThrows(NullPointerException.class, () ->
                cartItemService.updatePercentDiscountUser(userId, items)
        );
    }
}
