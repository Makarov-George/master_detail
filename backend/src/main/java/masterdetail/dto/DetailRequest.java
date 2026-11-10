package masterdetail.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DetailRequest(
    @NotBlank String name,
    @NotNull @PositiveOrZero BigDecimal totalDet
) {
    
}
