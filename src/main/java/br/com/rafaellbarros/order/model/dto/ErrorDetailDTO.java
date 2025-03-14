package br.com.rafaellbarros.order.model.dto;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ErrorDetailDTO {
    private String statusCode;
    private String message;
}
