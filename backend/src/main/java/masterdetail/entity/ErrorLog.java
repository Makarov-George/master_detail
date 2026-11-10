package masterdetail.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;



@Entity
@Table(name="error_log")
public class ErrorLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "error_type", nullable = false)
    private String errorType;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "document_number")
    private Integer documentNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ErrorLog() {
    }

    public ErrorLog(String errorType, String errorMessage, Integer documentNumber) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.documentNumber = documentNumber;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
