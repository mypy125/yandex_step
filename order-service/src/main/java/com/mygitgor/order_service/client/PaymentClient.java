package com.mygitgor.order_service.client;

import com.mygitgor.order_service.dto.PaymentLinkResponse;
import com.mygitgor.order_service.dto.clientDto.PaymentOrderDto;

public class PaymentClient {
    public PaymentLinkResponse createPaypalPaymentLink(String userId, PaymentOrderDto paymentOrder){
        return new PaymentLinkResponse();
    }

    public PaymentLinkResponse createStripePaymentLink(String userId, PaymentOrderDto paymentOrder){
        return new PaymentLinkResponse();
    }

}
