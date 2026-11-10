package masterdetail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import masterdetail.entity.Detail;

public interface DetailRepository extends JpaRepository<Detail, Long> {

    List<Detail> findByDocumentId(Long masterId);
}
