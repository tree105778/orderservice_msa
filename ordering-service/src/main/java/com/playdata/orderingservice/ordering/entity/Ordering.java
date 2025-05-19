package com.playdata.orderingservice.ordering.entity;

import com.playdata.orderingservice.ordering.dto.OrderingListResDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Ordering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
     */

    // 프로젝트가 나눠지면서 Ordering 쪽에서는 User 엔터티에 대한 정보를 확인할 수 없다.
    // 클라이언트 단에서 넘어오는 정보만 저장할 수 있다.
    @JoinColumn
    private Long userId;

    //    MSA에선 도메인별로 분리돼 있어서 FK처럼 다른 서비스의 ID를 바로 못 가져올 수도 있음.
//    그러니까 서비스 간 통신 실패 상황을 대비해서 최소 식별자(email 같은 거)는
//    자기 도메인 안에 보관하는 게 정석이다. (나중에 재처리에서 사용하기 위해 설정)
    private String userEmail;

    @Column(columnDefinition = "TEXT") // TEXT 타입은 일반적인 VARCHAR보다 더 큰 문자열을 저장할 수 있는 타입.
    @Setter
    private String originalRequestJson;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)
    private List<OrderDetail> orderDetails;

    // dto 변환 메서드
    public OrderingListResDto fromEntity(
            String email, Map<Long, String> productIdToNameMap) {
        List<OrderDetail> orderDetailList = this.getOrderDetails();

        List<OrderingListResDto.OrderDetailDto> orderDetailDtos
                = new ArrayList<>();

        // OrderDetail 엔터티를 OrderDetailDto로 변환해야 합니다.
        for (OrderDetail orderDetail : orderDetailList) {
            OrderingListResDto.OrderDetailDto orderDetailDto
                    = orderDetail.fromEntity(productIdToNameMap);
            orderDetailDtos.add(orderDetailDto);
        }

        return OrderingListResDto.builder()
                .id(id)
                .userEmail(email)
                .orderStatus(orderStatus)
                .orderDetails(orderDetailDtos)
                .build();
    }
}
