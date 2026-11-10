package masterdetail.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;
import masterdetail.entity.Detail;
import masterdetail.entity.Master;
import masterdetail.dto.DetailRequest;
import masterdetail.dto.DetailResponse;
import masterdetail.repository.DetailRepository;
import masterdetail.repository.MasterRepository;

@Service 
public class DetailService {

    private final DetailRepository detailRepository;
    private final MasterRepository masterRepository;

    public DetailService(DetailRepository detailRepository, MasterRepository masterRepository){
        this.detailRepository = detailRepository;
        this.masterRepository = masterRepository;
    }

    @Transactional
    public DetailResponse createDetail(Long masterId, DetailRequest request){
        Master master = masterRepository.findById(masterId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Документ не найден")
        );
        Detail detail = new Detail(request.name(), request.totalDet());
        master.addDetail(detail);
        Detail saved = detailRepository.save(detail);
        DetailResponse response = new DetailResponse(
            saved.getId(),
            saved.getName(),
            saved.getTotalDet(),
            masterId
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<DetailResponse> getDetailsByMasterId(Long masterId){
        masterRepository.findById(masterId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Документ не найден")
        );
        List<Detail> details = detailRepository.findByDocumentId(masterId);
        List<DetailResponse> result = new ArrayList<>();
        for (Detail detail: details){
            DetailResponse response = new DetailResponse(
                detail.getId(),
                detail.getName(),
                detail.getTotalDet(),
                masterId
            );
            result.add(response);
        }
        return result;
    }

    @Transactional
    public DetailResponse updateDetail(Long masterId, Long detailId, DetailRequest request){
        Master master = masterRepository.findById(masterId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Документ не найден")
        );
        Detail detail = detailRepository.findById(detailId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Спецификация не найдена")
        );
        if (!detail.getDocument().getId().equals(masterId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Спецификация находится не в этом документе");
        }
        detail.setName(request.name());
        detail.setTotalDet(request.totalDet());
        master.recalculateTotal();
        DetailResponse response = new DetailResponse(
            detailId,
            detail.getName(),
            detail.getTotalDet(),
            masterId
        );
        return response;
    }

    @Transactional
    public void deleteDetail(Long masterId, Long detailId){
        Master master = masterRepository.findById(masterId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Документ не найден")
        );
        Detail detail = detailRepository.findById(detailId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Спецификация не найдена")
        );
        if (!detail.getDocument().getId().equals(masterId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Спецификация находится не в этом документе");
        }
        master.getDetails().remove(detail);
        master.recalculateTotal();
        detailRepository.delete(detail);

    }
}
