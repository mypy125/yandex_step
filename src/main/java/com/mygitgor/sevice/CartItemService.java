package com.mygitgor.sevice;

import com.mygitgor.model.CartItem;
import com.mygitgor.client.PercentDiscountService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class CartItemService {
    private PercentDiscountService percentDiscountService;

    public void updatePercentDiscountUser(String userId, List<CartItem> items){
        int userPercentDiscount = getUserDiscount(userId);
        for(CartItem item : items){
            BigDecimal calculatePercent = getPercent(item.getPrice(),userPercentDiscount);
            item.setPrice(calculatePercent);
        }
    }

    private BigDecimal getPercent(BigDecimal item, int percent){
        return item
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private int getUserDiscount(String userId){
       return percentDiscountService.getPercentByUser(userId);
    }
}
