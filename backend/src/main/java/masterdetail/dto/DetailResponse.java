package masterdetail.dto;

import java.math.BigDecimal;

public record DetailResponse(Long id, String name, BigDecimal totalDet, Long documentId) {
    
}
