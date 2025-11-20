package com.mygitgor.product_service.controller;

import com.mygitgor.product_service.dto.*;
import com.mygitgor.product_service.dto.client.*;
import com.mygitgor.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}/exist")
    public ResponseEntity<Boolean>existProductById(@PathVariable String productId){
        Boolean existById = productService.existProductById(UUID.fromString(productId));
        return ResponseEntity.ok(existById);
    }

    @PostMapping("/validate-stock")
    public ResponseEntity<StockValidationResponse> validateStock(@RequestBody StockValidationRequest request
    ){
        return null;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto>getProductById(@PathVariable String productId){
        ProductDto product = productService.getProductById(UUID.fromString(productId));
        return ResponseEntity.ok(product);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ProductDto>> getProductsBySellerId(@PathVariable UUID sellerId){
        return null;
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody CreateProductRequest request,
                                                    @RequestHeader("X-Seller-Id") UUID sellerId
    ){
        return null;
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID productId,
                                                    @RequestBody ProductDto productDto,
                                                    @RequestHeader("X-Seller-Id") UUID sellerId
    ){
        return null;
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable UUID productId,
                                                 @RequestHeader("X-Seller-Id") UUID sellerId
    ){
        return null;
    }

    @GetMapping("/{productId}/seller")
    public ResponseEntity<UUID> getProductSellerId(@PathVariable UUID productId
    ){
        return null;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size,
                                                           @RequestParam(defaultValue = "createdAt") String sortBy,
                                                           @RequestParam(defaultValue = "desc") String sortDirection
    ){
        return null;
    }

    @PatchMapping("/{productId}/quantity")
    public ResponseEntity<ProductDto> updateProductQuantity(@PathVariable UUID productId,
                                                            @RequestBody UpdateQuantityRequest request,
                                                            @RequestHeader("X-Seller-Id") UUID sellerId
    ){
        return null;
    }

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<ProductDto> updateProductStock(@PathVariable UUID productId,
                                                         @RequestBody UpdateStockRequest request,
                                                         @RequestHeader("X-Seller-Id") UUID sellerId
    ){
        return null;
    }

    @PatchMapping("/{productId}/price")
    public ResponseEntity<ProductDto> updateProductPrice(@PathVariable UUID productId,
                                                         @RequestBody UpdatePriceRequest request,
                                                         @RequestHeader("X-Seller-Id") UUID sellerId
    ){
        return null;
    }

    @PatchMapping("/{productId}/status")
    public ResponseEntity<ProductDto> updateProductStatus(@PathVariable UUID productId,
                                                          @RequestBody UpdateStatusRequest request,
                                                          @RequestHeader("X-Seller-Id") UUID sellerId
    ){
        return null;
    }

    @PostMapping("/bulk/status")
    public ResponseEntity<List<ProductDto>> bulkUpdateStatus(@RequestBody BulkStatusUpdateRequest request,
                                                             @RequestHeader("X-Seller-Id") UUID sellerId
    ){
        return null;
    }

    @GetMapping("/seller/{sellerId}/stats")
    public ResponseEntity<SellerProductStats> getSellerProductStats(@PathVariable UUID sellerId
    ){
        return null;
    }


}
