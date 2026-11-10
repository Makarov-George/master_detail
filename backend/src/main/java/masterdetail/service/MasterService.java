package masterdetail.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import masterdetail.dto.MasterRequest;
import masterdetail.dto.MasterResponse;
import masterdetail.entity.Master;
import masterdetail.repository.MasterRepository;

@Service 
public class MasterService {
    private final MasterRepository repository;
    private final ErrorLogService errorLogService;

    public MasterService(MasterRepository repository, ErrorLogService errorLogService){
        this.repository = repository;
        this.errorLogService = errorLogService;
    }

    public List<MasterResponse> getAllMasters(){
        List<Master> masters = repository.findAll();
        List<MasterResponse> result = new ArrayList<>();
        for (Master master: masters){
            MasterResponse response = new MasterResponse(
                master.getId(),
                master.getNumber(),
                master.getDate(),
                master.getTotal(),
                master.getNote()
            );
        result.add(response);
        }
        return result;
    }

    public MasterResponse getMasterById(Long id){
        Master master = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Документ не найден")
        );

        MasterResponse response = new MasterResponse(
            master.getId(),
            master.getNumber(),
            master.getDate(),
            master.getTotal(),
            master.getNote()
        );
        return response;
    }

    @Transactional
    public MasterResponse createMaster(MasterRequest request){
        if (repository.existsByNumber(request.number())){
            String message = "Документ с номером " + request.number() + " уже существует";

            errorLogService.logError(
                "DUPLICATE_DOCUMENT_NUMBER",
                message,
                request.number()
            );

            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
        Master master = new Master(request.number(), request.date(), request.note());
        Master saved = repository.save(master);
        MasterResponse response = new MasterResponse(
            saved.getId(),
            saved.getNumber(),
            saved.getDate(),
            saved.getTotal(),
            saved.getNote()
        );
        return response;
    }

    @Transactional
    public MasterResponse updateMaster(Long id, MasterRequest request){
        Master master = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Документ не найден")
        );
        if (!master.getNumber().equals(request.number()) && repository.existsByNumber(request.number())){
            String message = "Документ с номером " + request.number() + " уже существует";

            errorLogService.logError(
                "DUPLICATE_DOCUMENT_NUMBER",
                message,
                request.number()
            );

            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        }
        master.setNumber(request.number());
        master.setDate(request.date());
        master.setNote(request.note());
        MasterResponse response = new MasterResponse(
            master.getId(),
            master.getNumber(),
            master.getDate(),
            master.getTotal(),
            master.getNote()
        );
        return response;
    }

    public void deleteMaster(Long id){
        Master master = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Документ не найден")
        );
        repository.delete(master);
    }
}
