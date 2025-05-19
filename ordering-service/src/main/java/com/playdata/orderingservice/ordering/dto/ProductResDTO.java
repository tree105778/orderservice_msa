package com.playdata.orderingservice.ordering.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResDTO {

    private Long id;
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private String imagePath;
}
