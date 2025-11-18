import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IWall } from 'app/entities/wall/wall.model';
import { WallService } from 'app/entities/wall/service/wall.service';
import { IBoulder } from 'app/entities/boulder/boulder.model';
import { BoulderService } from 'app/entities/boulder/service/boulder.service';
import { HoldType } from 'app/entities/enumerations/hold-type.model';
import { Difficulty } from 'app/entities/enumerations/difficulty.model';
import { HoldService } from '../service/hold.service';
import { IHold } from '../hold.model';
import { HoldFormGroup, HoldFormService } from './hold-form.service';

@Component({
  selector: 'jhi-hold-update',
  templateUrl: './hold-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class HoldUpdateComponent implements OnInit {
  isSaving = false;
  hold: IHold | null = null;
  holdTypeValues = Object.keys(HoldType);
  difficultyValues = Object.keys(Difficulty);

  wallsSharedCollection: IWall[] = [];
  bouldersSharedCollection: IBoulder[] = [];

  protected holdService = inject(HoldService);
  protected holdFormService = inject(HoldFormService);
  protected wallService = inject(WallService);
  protected boulderService = inject(BoulderService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: HoldFormGroup = this.holdFormService.createHoldFormGroup();

  compareWall = (o1: IWall | null, o2: IWall | null): boolean => this.wallService.compareWall(o1, o2);

  compareBoulder = (o1: IBoulder | null, o2: IBoulder | null): boolean => this.boulderService.compareBoulder(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ hold }) => {
      this.hold = hold;
      if (hold) {
        this.updateForm(hold);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const hold = this.holdFormService.getHold(this.editForm);
    if (hold.id !== null) {
      this.subscribeToSaveResponse(this.holdService.update(hold));
    } else {
      this.subscribeToSaveResponse(this.holdService.create(hold));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHold>>): void {
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

  protected updateForm(hold: IHold): void {
    this.hold = hold;
    this.holdFormService.resetForm(this.editForm, hold);

    this.wallsSharedCollection = this.wallService.addWallToCollectionIfMissing<IWall>(this.wallsSharedCollection, hold.wall);
    this.bouldersSharedCollection = this.boulderService.addBoulderToCollectionIfMissing<IBoulder>(
      this.bouldersSharedCollection,
      hold.boulder,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.wallService
      .query()
      .pipe(map((res: HttpResponse<IWall[]>) => res.body ?? []))
      .pipe(map((walls: IWall[]) => this.wallService.addWallToCollectionIfMissing<IWall>(walls, this.hold?.wall)))
      .subscribe((walls: IWall[]) => (this.wallsSharedCollection = walls));

    this.boulderService
      .query()
      .pipe(map((res: HttpResponse<IBoulder[]>) => res.body ?? []))
      .pipe(map((boulders: IBoulder[]) => this.boulderService.addBoulderToCollectionIfMissing<IBoulder>(boulders, this.hold?.boulder)))
      .subscribe((boulders: IBoulder[]) => (this.bouldersSharedCollection = boulders));
  }
}
