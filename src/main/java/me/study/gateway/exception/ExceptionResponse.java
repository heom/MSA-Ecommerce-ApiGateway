package me.study.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder @NoArgsConstructor
public class ExceptionResponse {
    private Date timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
