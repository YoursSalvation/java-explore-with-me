package main.dto;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double lat;
    private Double lon;
}
