package com.mygitgor.order_service.mapping;

import com.mygitgor.order_service.domain.PaymentDetails;
import com.mygitgor.order_service.dto.PaymentDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentDetailsMapper {

    PaymentDetailsDto toDto(PaymentDetails entity);
    PaymentDetails toEntity(PaymentDetailsDto dto);

    List<PaymentDetailsDto> toDtoList(List<PaymentDetails> entities);
    List<PaymentDetails> toEntityList(List<PaymentDetailsDto> dto);
}