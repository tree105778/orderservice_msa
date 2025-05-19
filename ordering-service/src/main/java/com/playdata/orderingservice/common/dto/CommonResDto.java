package com.playdata.orderingservice.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Setter @Getter @ToString
@NoArgsConstructor
public class CommonResDto<T> {
    private int statusCode;
    private String statusMessage;
    private T result; // 요청에 따라 전달할 데이터가 그때그때 다르니까 T 타입으로 선언함.

    public CommonResDto(HttpStatus httpStatus, String statusMessage, T result) {
        this.statusCode = httpStatus.value();
        this.statusMessage = statusMessage;
        this.result = result;
    }
}
