import { Component, signal, computed } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Master} from './master.model';
import {OnInit} from '@angular/core';
import { FormsModule, NumberValueAccessor } from '@angular/forms';
import { MasterService } from './master.service';
import { MasterRequest } from './master-request.model';
import { Detail } from './detail.model';
import { DetailRequest } from './detail-request.model';
import { finalize } from 'rxjs';
import { DetailService } from './detail.service';
import { Observable } from 'rxjs';

@Component({
  imports: [RouterOutlet, FormsModule],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})

export class App implements OnInit {

  constructor(
    private masterService : MasterService,
    private detailService: DetailService
  ){}

  currentPage = signal(1);
  pageSize = signal(5);

  title = 'Master - Detail';
  documents = signal<Master[]>([]);
  details = signal<Detail[]>([]);

  newNumber : number = 0;
  newDate : string = '';
  newNote : string = '';
  newName: string = '';
  newTotal: number = 0;

  editNumber : number = 0;
  editDate : string = '';
  editNote : string = '';
  editName : string = '';
  editTotal : number = 0;

  errorMessage = signal('');
  isCreatingDocument = signal(false);
  isCreatingDetail = signal(false);
  isLoading = signal(false);
  selectedMasterId = signal<number | null>(null);
  editingMasterId = signal<number | null>(null);
  editingDetailId = signal<number | null>(null);

  ngOnInit(): void {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.masterService.getMasters().subscribe({
      next: (data) => {
        this.documents.set(data);
      },
      error: (err) => {
        console.error('Ошибка загрузки документов', err);
      }
    });
  }

  selectedDocument = computed(() => this.documents().find(document => document.id === this.selectedMasterId()) ?? null);
  editingDocument = computed(() => this.documents().find(document => document.id === this.editingMasterId()) ?? null);
  editingDetail = computed(() => this.details().find(detail => detail.id === this.editingDetailId()) ?? null);

  resetFormDocument(): void {
    this.newNumber = 0;
    this.newDate = '';
    this.newNote = '';
    this.errorMessage.set('');
  }

  resetEditFormDocument(): void {
    this.editNumber = 0;
    this.editDate = '';
    this.editNote = '';
    this.errorMessage.set('');
  }

  resetFormDetail(): void {
    this.newName = '';
    this.newTotal = 0;
    this.errorMessage.set('');
  }

  resetEditFormDetail(): void {
    this.editName = '';
    this.editTotal = 0;
    this.errorMessage.set('');
  }

  toCreateDocument(): void {
    this.isCreatingDocument.set(true);
  }

  createDocument(): void {
    if (this.isLoading()) {
      return;
    }
    if (this.newNumber <= 0 || !Number.isInteger(this.newNumber)) {
      this.errorMessage.set('Номер должен быть больше нуля и целым');
      return;
    }
    if (this.newDate === '') {
      this.errorMessage.set('Необходимо указать дату');
      return;
    }
    this.isLoading.set(true);
    const request : MasterRequest = {
    number: this.newNumber,
    date: this.newDate,
    note: this.newNote
    };
    
    this.masterService.createMaster(request).pipe(
        finalize(() => this.isLoading.set(false))
      ).subscribe({
      next : (createdDocument) => {this.documents.update(documents => [...documents, createdDocument]);
        this.resetFormDocument();
        console.log("Документ успешно создан");
      },
      error : (err) => {
        if (err.status === 409) {
          this.errorMessage.set('Документ с таким номером уже существует');
        }
        else {
          this.errorMessage.set('Не удалось создать документ');
        }
        console.error(err);
      }
    });
  }

  cancelCreateDocument(): void {
    this.resetFormDocument();
    this.isCreatingDocument.set(false);
  }

  removeDocumentById(id : number) : void {
    this.masterService.deleteMaster(id).subscribe({
      next : () => {this.documents.update(documents => documents.filter(document => document.id !== id));
        if (this.selectedMasterId() === id) {
          this.selectedMasterId.set(null);
          this.editingDetailId.set(null);
          this.isCreatingDetail.set(false);
          this.details.set([]);
          this.resetFormDetail();
          this.resetEditFormDetail();
        };
        if (this.editingMasterId() === id) {
          this.editingMasterId.set(null);
          this.resetEditFormDocument();
        }

      },
      error : (err) => {
        console.error("Ошибка удаления", err);
      }
    });
  }

  startEditDocument(document: Master): void {
    this.editingMasterId.set(document.id);
    this.editNumber = document.number;
    this.editDate = document.date;
    this.editNote = document.note;
  }

  saveEditDocument(): void {
    if (this.isLoading()) {
      return;
    }
    const id = this.editingMasterId();
    if (id === null) {
      return;
    }
    if (this.editNumber <= 0 || !Number.isInteger(this.editNumber)) {
      this.errorMessage.set('Номер должен быть больше нуля и целым');
      return;
    }
    if (this.editDate === '') {
      this.errorMessage.set('Необходимо указать дату');
      return;
    }
    this.isLoading.set(true);
    const request: MasterRequest = {
      number: this.editNumber,
      date: this.editDate,
      note: this.editNote
    }

    this.masterService.updateMaster(id, request).pipe(finalize(() => this.isLoading.set(false))).subscribe({
      next: (updatedDocument) => {
        this.documents.update(
          documents => documents.map(
            document => document.id === id ? updatedDocument : document
          )
        );
        this.resetEditFormDocument();
        this.editingMasterId.set(null);
        console.log("Документ успешно изменён");
      },
      error : (err) => {
        this.errorMessage.set("Не удалось изменить документ");
        console.error(err);
      }
    });
  }

  cancelEditDocument(): void {
    this.resetEditFormDocument();
    this.editingMasterId.set(null);
  }

  openDocument(id: number) {
    this.editingDetailId.set(null);
    this.selectedMasterId.set(id);
    this.details.set([]);
    this.detailService.getDetails(id).subscribe({
      next: (data) => {
        this.details.set(data);
      },
      error: (err) => {
        this.errorMessage.set("Не удалось открыть документ");
        console.error(err);
      }
    })
  }

  toCreateDetail() {
    this.resetFormDetail();
    this.isCreatingDetail.set(true);
  }
  
  createDetail() {
    if (this.isLoading()) {
      return;
    }
    if (this.newName === '') {
      this.errorMessage.set("Спецификация должна иметь название");
      return;
    }
    if (this.newTotal <= 0) {
      this.errorMessage.set("Сумма должна быть положительной");
      return;
    }
    const masterId = this.selectedMasterId();
    if (masterId === null) {
      this.errorMessage.set('Сначала выберите документ');
      return;
    }
    this.isLoading.set(true);
    const request: DetailRequest = {
      name: this.newName,
      totalDet: this.newTotal
    };
    this.detailService.createDetail(masterId, request).pipe(
        finalize(() => this.isLoading.set(false))
      ).subscribe({
      next: (createdDetail) => {this.details.update(details => [...details, createdDetail]);
        this.resetFormDetail();
        this.loadDocuments();
        console.log("Спецификация успешно создана");
      },
      error: (err) => {
        this.errorMessage.set("Не удалось создать спецификацию");
        console.error(err);
      }
    })
  }

  cancelCreateDetail(): void {
    this.resetFormDetail();
    this.isCreatingDetail.set(false);
  }

  removeDetailById(id : number) : void {
    const masterId = this.selectedMasterId();
    if (masterId === null) {
      this.errorMessage.set('Сначала выберите документ');
      return;
    }
    this.detailService.deleteDetail(masterId, id).subscribe({
      next : () => {this.details.update(details => details.filter(detail => detail.id !== id));
        this.editingDetailId.set(null);
        this.resetEditFormDetail();
        this.loadDocuments();
      },
      error : (err) => {
        console.error("Ошибка удаления", err);
      }
    });
  }

  startEditDetail(detail: Detail): void {
    this.editingDetailId.set(detail.id);
    this.editName = detail.name;
    this.editTotal = detail.totalDet;
  }

  saveEditDetail(): void {
    if (this.isLoading()) {
      return;
    }
    const id = this.editingDetailId();
    if (id === null) {
      return;
    }
    const masterId = this.selectedMasterId();
    if (masterId === null) {
      this.errorMessage.set('Сначала выберете документ');
      return;
    }
    if (this.editTotal <= 0) {
      this.errorMessage.set('Сумма должна быть больше нуля');
      return;
    }
    if (this.editName === '') {
      this.errorMessage.set('Спецификация должна иметь название');
      return;
    }
    this.isLoading.set(true);
    const request: DetailRequest = {
      name: this.editName,
      totalDet: this.editTotal
    }

    this.detailService.updateDetail(masterId, id, request).pipe(finalize(() => this.isLoading.set(false))).subscribe({
      next: (updatedDetail) => {
        this.details.update(
          details => details.map(
            detail => detail.id === id ? updatedDetail : detail
          )
        );
        this.resetEditFormDetail();
        this.loadDocuments();
        this.editingDetailId.set(null);
        console.log("Спецификация успешно изменена");
      },
      error : (err) => {
        this.errorMessage.set("Не удалось изменить спецификацию");
        console.error(err);
      }
    });
  }

  cancelEditDetail(): void {
    this.resetEditFormDetail();
    this.editingDetailId.set(null);
  }
}

