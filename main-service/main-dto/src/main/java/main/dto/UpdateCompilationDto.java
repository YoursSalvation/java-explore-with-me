package main.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationDto {

    @Size(max = 50)
    private String title;

    private Boolean pinned;

    private List<Long> events;
}