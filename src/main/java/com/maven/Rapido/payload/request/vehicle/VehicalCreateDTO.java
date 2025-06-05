package com.maven.Rapido.payload.request.vehicle;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicalCreateDTO {
    private String name;
    private String description;
    private String slug;
}
