import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Detail } from "./detail.model";
import { DetailRequest } from "./detail-request.model";

@Injectable({
    providedIn : 'root'
})
export class DetailService {
    private apiUrl = 'http://localhost:8080/api/masters';

    constructor(private http: HttpClient){

    }

    getDetails(masterId: number) {
        return this.http.get<Detail[]>(`${this.apiUrl}/${masterId}/details`);
    }

    createDetail(masterId: number, request: DetailRequest) {
        return this.http.post<Detail>(`${this.apiUrl}/${masterId}/details`, request);
    }

    deleteDetail(masterId: number, id: number) {
        return this.http.delete<Detail>(`${this.apiUrl}/${masterId}/details/${id}`);
    }

    updateDetail(masterId: number, id: number, request: DetailRequest) {
        return this.http.put<Detail>(`${this.apiUrl}/${masterId}/details/${id}`, request);
    }
}