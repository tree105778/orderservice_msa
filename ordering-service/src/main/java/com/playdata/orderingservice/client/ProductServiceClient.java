package com.playdata.orderingservice.client;

import com.playdata.orderingservice.common.dto.CommonResDto;
import com.playdata.orderingservice.ordering.dto.ProductResDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/product/{prodId}")
    CommonResDto<ProductResDTO> findById(@PathVariable Long prodId);

    @PutMapping("/product/updateQuantity")
    ResponseEntity<?> updateQuantity(@RequestBody ProductResDTO productResDTO);

    @PostMapping("/product/products")
    CommonResDto<List<ProductResDTO>> getProducts(@RequestBody List<Long> productIds);
}
