package masterdetail.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.validation.Valid;
import masterdetail.service.MasterService;
import masterdetail.dto.MasterRequest;
import masterdetail.dto.MasterResponse;


@RestController
@RequestMapping("/api/masters")
@CrossOrigin(origins="http://localhost:4200")
public class MasterController {
    private final MasterService service;
    
    public MasterController(MasterService service){
        this.service = service;
    }

    @GetMapping
    public List<MasterResponse> getAllMasters(){
        return service.getAllMasters();
    }

    @GetMapping("/{id}")
    public MasterResponse getMasterById(@PathVariable Long id){
        return service.getMasterById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MasterResponse createMaster(@Valid @RequestBody MasterRequest request){
        return service.createMaster(request);
    }

    @PutMapping("/{id}")
    public MasterResponse updateMaster(
        @PathVariable Long id,
        @Valid @RequestBody MasterRequest request
    ) {
        return service.updateMaster(id, request);
    }

    @ResponseStatus (HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteMaster(@PathVariable Long id){
        service.deleteMaster(id);
    }
}
