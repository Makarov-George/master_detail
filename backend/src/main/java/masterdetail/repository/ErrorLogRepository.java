package masterdetail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import masterdetail.entity.ErrorLog;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    
}
