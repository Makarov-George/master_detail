import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Master } from "./master.model";
import { MasterRequest } from "./master-request.model";
@Injectable({
    providedIn : 'root'
})
export class MasterService {
    private apiUrl = 'http://localhost:8080/api/masters';

    constructor(private http: HttpClient) {

    }

    getMasters() {
        return this.http.get<Master[]>(this.apiUrl);
    }

    getMasterById(id: number) {
        return this.http.get<Master>(`${this.apiUrl}/${id}`)
    }

    deleteMaster(id: number) {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    createMaster(request: MasterRequest) {
        return this.http.post<Master>(this.apiUrl, request);
    }

    updateMaster(id: number, request: MasterRequest) {
        return this.http.put<Master>(`${this.apiUrl}/${id}`, request);
    }
}