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
import masterdetail.dto.DetailRequest;
import masterdetail.dto.DetailResponse;
import masterdetail.service.DetailService;

@RestController
@RequestMapping("/api/masters/{masterId}/details")
@CrossOrigin(origins="http://localhost:4200")
public class DetailController {
    private final DetailService service;

    public DetailController(DetailService service){
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DetailResponse createDetail(@PathVariable Long masterId, @Valid @RequestBody DetailRequest request){
        return service.createDetail(masterId, request);
    }

    @GetMapping
    public List<DetailResponse> getDetailsByMasterId(@PathVariable Long masterId){
        return service.getDetailsByMasterId(masterId);
    }

    @PutMapping("/{detailId}")
    public DetailResponse updateDetail(
        @PathVariable Long masterId,
        @PathVariable Long detailId,
        @Valid @RequestBody DetailRequest request
    ){
        return service.updateDetail(masterId, detailId, request);
    }

    @ResponseStatus (HttpStatus.NO_CONTENT)
    @DeleteMapping("/{detailId}")
    public void deleteDetail(@PathVariable Long masterId, @PathVariable Long detailId){
        service.deleteDetail(masterId, detailId);
    }
}
