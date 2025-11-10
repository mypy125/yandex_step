package com.mygitgor.product_service.mapper;

import com.mygitgor.product_service.domain.Product;
import com.mygitgor.product_service.dto.CreateProductRequest;
import com.mygitgor.product_service.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toDetailedDto(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntityFromCreateRequest(CreateProductRequest request);
}
