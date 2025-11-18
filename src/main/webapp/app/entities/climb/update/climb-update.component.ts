import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBoulder } from 'app/entities/boulder/boulder.model';
import { BoulderService } from 'app/entities/boulder/service/boulder.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ClimbService } from '../service/climb.service';
import { IClimb } from '../climb.model';
import { ClimbFormGroup, ClimbFormService } from './climb-form.service';

@Component({
  selector: 'jhi-climb-update',
  templateUrl: './climb-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClimbUpdateComponent implements OnInit {
  isSaving = false;
  climb: IClimb | null = null;

  bouldersSharedCollection: IBoulder[] = [];
  usersSharedCollection: IUser[] = [];

  protected climbService = inject(ClimbService);
  protected climbFormService = inject(ClimbFormService);
  protected boulderService = inject(BoulderService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClimbFormGroup = this.climbFormService.createClimbFormGroup();

  compareBoulder = (o1: IBoulder | null, o2: IBoulder | null): boolean => this.boulderService.compareBoulder(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ climb }) => {
      this.climb = climb;
      if (climb) {
        this.updateForm(climb);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const climb = this.climbFormService.getClimb(this.editForm);
    if (climb.id !== null) {
      this.subscribeToSaveResponse(this.climbService.update(climb));
    } else {
      this.subscribeToSaveResponse(this.climbService.create(climb));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClimb>>): void {
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

  protected updateForm(climb: IClimb): void {
    this.climb = climb;
    this.climbFormService.resetForm(this.editForm, climb);

    this.bouldersSharedCollection = this.boulderService.addBoulderToCollectionIfMissing<IBoulder>(
      this.bouldersSharedCollection,
      climb.boulder,
    );
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, climb.climbedBy);
  }

  protected loadRelationshipsOptions(): void {
    this.boulderService
      .query()
      .pipe(map((res: HttpResponse<IBoulder[]>) => res.body ?? []))
      .pipe(map((boulders: IBoulder[]) => this.boulderService.addBoulderToCollectionIfMissing<IBoulder>(boulders, this.climb?.boulder)))
      .subscribe((boulders: IBoulder[]) => (this.bouldersSharedCollection = boulders));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.climb?.climbedBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
