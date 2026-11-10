package masterdetail.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MasterResponse(Long id, Integer number, LocalDate date, BigDecimal total, String note){

}
