package edu.eci.dosw.tdd.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String id;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "El autor es obligatorio")
    private String author;

    @Positive(message = "El total de ejemplares debe ser mayor a 0")
    private int totalCopies;

    @Min(value = 0, message = "Los ejemplares disponibles no pueden ser negativos")
    private int availableCopies;
}