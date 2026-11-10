package masterdetail.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MasterRequest(
    @NotNull @Positive Integer number,
    @NotNull LocalDate date,
    String note
) {
    
}
