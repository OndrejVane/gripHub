import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IWall } from 'app/entities/wall/wall.model';
import { WallService } from 'app/entities/wall/service/wall.service';
import { IBoulder } from '../boulder.model';
import { BoulderService } from '../service/boulder.service';
import { BoulderFormGroup, BoulderFormService } from './boulder-form.service';

@Component({
  selector: 'jhi-boulder-update',
  templateUrl: './boulder-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BoulderUpdateComponent implements OnInit {
  isSaving = false;
  boulder: IBoulder | null = null;

  wallsSharedCollection: IWall[] = [];

  protected boulderService = inject(BoulderService);
  protected boulderFormService = inject(BoulderFormService);
  protected wallService = inject(WallService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BoulderFormGroup = this.boulderFormService.createBoulderFormGroup();

  compareWall = (o1: IWall | null, o2: IWall | null): boolean => this.wallService.compareWall(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ boulder }) => {
      this.boulder = boulder;
      if (boulder) {
        this.updateForm(boulder);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const boulder = this.boulderFormService.getBoulder(this.editForm);
    if (boulder.id !== null) {
      this.subscribeToSaveResponse(this.boulderService.update(boulder));
    } else {
      this.subscribeToSaveResponse(this.boulderService.create(boulder));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBoulder>>): void {
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

  protected updateForm(boulder: IBoulder): void {
    this.boulder = boulder;
    this.boulderFormService.resetForm(this.editForm, boulder);

    this.wallsSharedCollection = this.wallService.addWallToCollectionIfMissing<IWall>(this.wallsSharedCollection, boulder.wall);
  }

  protected loadRelationshipsOptions(): void {
    this.wallService
      .query()
      .pipe(map((res: HttpResponse<IWall[]>) => res.body ?? []))
      .pipe(map((walls: IWall[]) => this.wallService.addWallToCollectionIfMissing<IWall>(walls, this.boulder?.wall)))
      .subscribe((walls: IWall[]) => (this.wallsSharedCollection = walls));
  }
}
