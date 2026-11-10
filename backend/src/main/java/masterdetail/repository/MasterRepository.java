package masterdetail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import masterdetail.entity.Master;

public interface MasterRepository extends JpaRepository<Master, Long> {

    boolean existsByNumber(Integer number);
    
}
