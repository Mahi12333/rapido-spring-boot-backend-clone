package com.maven.Rapido.payload.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponseDTO<T> {
//    private int statusCode;
    private T data;
    private String message;


    public CommonResponseDTO(T data, String message) {
        this.data = data;
        this.message = message;
    }

    // Getters and setters (or use Lombok: @Getter, @Setter, @AllArgsConstructor)
}
