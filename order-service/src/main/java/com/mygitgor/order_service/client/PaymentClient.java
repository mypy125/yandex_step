package com.mygitgor.order_service.client;

import com.mygitgor.order_service.dto.OrderDto;
import com.mygitgor.order_service.dto.PaymentLinkResponse;
import com.mygitgor.order_service.dto.PaymentMethod;
import com.mygitgor.order_service.dto.clientDto.PaymentOrderDto;

import java.util.Set;

public class PaymentClient {
    public PaymentLinkResponse createPaypalPaymentLink(String userId, PaymentOrderDto paymentOrder){
        return new PaymentLinkResponse();
    }

    public PaymentLinkResponse createStripePaymentLink(String userId, PaymentOrderDto paymentOrder){
        return new PaymentLinkResponse();
    }

    public PaymentOrderDto createPaymentOrder(String userId, Set<OrderDto> orders, PaymentMethod paymentMethod){
        return new PaymentOrderDto();
    }

}
