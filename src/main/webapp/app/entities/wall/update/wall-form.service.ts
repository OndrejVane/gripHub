import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IWall, NewWall } from '../wall.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWall for edit and NewWallFormGroupInput for create.
 */
type WallFormGroupInput = IWall | PartialWithRequiredKeyOf<NewWall>;

type WallFormDefaults = Pick<NewWall, 'id' | 'boulders'>;

type WallFormGroupContent = {
  id: FormControl<IWall['id'] | NewWall['id']>;
  name: FormControl<IWall['name']>;
  height: FormControl<IWall['height']>;
  width: FormControl<IWall['width']>;
  minSlope: FormControl<IWall['minSlope']>;
  maxSlope: FormControl<IWall['maxSlope']>;
  rows: FormControl<IWall['rows']>;
  columns: FormControl<IWall['columns']>;
  photo: FormControl<IWall['photo']>;
  photoContentType: FormControl<IWall['photoContentType']>;
  boulders: FormControl<IWall['boulders']>;
};

export type WallFormGroup = FormGroup<WallFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WallFormService {
  createWallFormGroup(wall: WallFormGroupInput = { id: null }): WallFormGroup {
    const wallRawValue = {
      ...this.getFormDefaults(),
      ...wall,
    };
    return new FormGroup<WallFormGroupContent>({
      id: new FormControl(
        { value: wallRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(wallRawValue.name, {
        validators: [Validators.required],
      }),
      height: new FormControl(wallRawValue.height, {
        validators: [Validators.required, Validators.min(0)],
      }),
      width: new FormControl(wallRawValue.width, {
        validators: [Validators.required, Validators.min(0)],
      }),
      minSlope: new FormControl(wallRawValue.minSlope, {
        validators: [Validators.required, Validators.min(0), Validators.max(90)],
      }),
      maxSlope: new FormControl(wallRawValue.maxSlope, {
        validators: [Validators.required, Validators.min(0), Validators.max(90)],
      }),
      rows: new FormControl(wallRawValue.rows, {
        validators: [Validators.required],
      }),
      columns: new FormControl(wallRawValue.columns, {
        validators: [Validators.required],
      }),
      photo: new FormControl(wallRawValue.photo, {
        validators: [Validators.required],
      }),
      photoContentType: new FormControl(wallRawValue.photoContentType),
      boulders: new FormControl(wallRawValue.boulders ?? []),
    });
  }

  getWall(form: WallFormGroup): IWall | NewWall {
    return form.getRawValue() as IWall | NewWall;
  }

  resetForm(form: WallFormGroup, wall: WallFormGroupInput): void {
    const wallRawValue = { ...this.getFormDefaults(), ...wall };
    form.reset(
      {
        ...wallRawValue,
        id: { value: wallRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): WallFormDefaults {
    return {
      id: null,
      boulders: [],
    };
  }
}
