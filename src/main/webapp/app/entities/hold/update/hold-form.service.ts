import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IHold, NewHold } from '../hold.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHold for edit and NewHoldFormGroupInput for create.
 */
type HoldFormGroupInput = IHold | PartialWithRequiredKeyOf<NewHold>;

type HoldFormDefaults = Pick<NewHold, 'id'>;

type HoldFormGroupContent = {
  id: FormControl<IHold['id'] | NewHold['id']>;
  photoCoordinatesX: FormControl<IHold['photoCoordinatesX']>;
  photoCoordinatesY: FormControl<IHold['photoCoordinatesY']>;
  column: FormControl<IHold['column']>;
  row: FormControl<IHold['row']>;
  holdType: FormControl<IHold['holdType']>;
  holdDifficulty: FormControl<IHold['holdDifficulty']>;
  wall: FormControl<IHold['wall']>;
  boulder: FormControl<IHold['boulder']>;
};

export type HoldFormGroup = FormGroup<HoldFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HoldFormService {
  createHoldFormGroup(hold: HoldFormGroupInput = { id: null }): HoldFormGroup {
    const holdRawValue = {
      ...this.getFormDefaults(),
      ...hold,
    };
    return new FormGroup<HoldFormGroupContent>({
      id: new FormControl(
        { value: holdRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      photoCoordinatesX: new FormControl(holdRawValue.photoCoordinatesX),
      photoCoordinatesY: new FormControl(holdRawValue.photoCoordinatesY),
      column: new FormControl(holdRawValue.column),
      row: new FormControl(holdRawValue.row),
      holdType: new FormControl(holdRawValue.holdType),
      holdDifficulty: new FormControl(holdRawValue.holdDifficulty),
      wall: new FormControl(holdRawValue.wall),
      boulder: new FormControl(holdRawValue.boulder),
    });
  }

  getHold(form: HoldFormGroup): IHold | NewHold {
    return form.getRawValue() as IHold | NewHold;
  }

  resetForm(form: HoldFormGroup, hold: HoldFormGroupInput): void {
    const holdRawValue = { ...this.getFormDefaults(), ...hold };
    form.reset(
      {
        ...holdRawValue,
        id: { value: holdRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): HoldFormDefaults {
    return {
      id: null,
    };
  }
}
