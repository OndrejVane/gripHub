import { Component, ElementRef, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IBoulder } from 'app/entities/boulder/boulder.model';
import { BoulderService } from 'app/entities/boulder/service/boulder.service';
import { WallService } from '../service/wall.service';
import { IWall } from '../wall.model';
import { WallFormGroup, WallFormService } from './wall-form.service';

@Component({
  selector: 'jhi-wall-update',
  templateUrl: './wall-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class WallUpdateComponent implements OnInit {
  isSaving = false;
  wall: IWall | null = null;

  bouldersSharedCollection: IBoulder[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected wallService = inject(WallService);
  protected wallFormService = inject(WallFormService);
  protected boulderService = inject(BoulderService);
  protected elementRef = inject(ElementRef);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: WallFormGroup = this.wallFormService.createWallFormGroup();

  compareBoulder = (o1: IBoulder | null, o2: IBoulder | null): boolean => this.boulderService.compareBoulder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ wall }) => {
      this.wall = wall;
      if (wall) {
        this.updateForm(wall);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('gripHubApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector(`#${idInput}`)) {
      this.elementRef.nativeElement.querySelector(`#${idInput}`).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const wall = this.wallFormService.getWall(this.editForm);
    if (wall.id !== null) {
      this.subscribeToSaveResponse(this.wallService.update(wall));
    } else {
      this.subscribeToSaveResponse(this.wallService.create(wall));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IWall>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(wall: IWall): void {
    this.wall = wall;
    this.wallFormService.resetForm(this.editForm, wall);

    this.bouldersSharedCollection = this.boulderService.addBoulderToCollectionIfMissing<IBoulder>(
      this.bouldersSharedCollection,
      ...(wall.boulders ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.boulderService
      .query()
      .pipe(map((res: HttpResponse<IBoulder[]>) => res.body ?? []))
      .pipe(
        map((boulders: IBoulder[]) =>
          this.boulderService.addBoulderToCollectionIfMissing<IBoulder>(boulders, ...(this.wall?.boulders ?? [])),
        ),
      )
      .subscribe((boulders: IBoulder[]) => (this.bouldersSharedCollection = boulders));
  }
}
