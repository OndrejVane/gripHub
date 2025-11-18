import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IClimb, NewClimb } from '../climb.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClimb for edit and NewClimbFormGroupInput for create.
 */
type ClimbFormGroupInput = IClimb | PartialWithRequiredKeyOf<NewClimb>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IClimb | NewClimb> = Omit<T, 'topDate'> & {
  topDate?: string | null;
};

type ClimbFormRawValue = FormValueOf<IClimb>;

type NewClimbFormRawValue = FormValueOf<NewClimb>;

type ClimbFormDefaults = Pick<NewClimb, 'id' | 'topDate' | 'isTop'>;

type ClimbFormGroupContent = {
  id: FormControl<ClimbFormRawValue['id'] | NewClimb['id']>;
  attempts: FormControl<ClimbFormRawValue['attempts']>;
  topDate: FormControl<ClimbFormRawValue['topDate']>;
  rate: FormControl<ClimbFormRawValue['rate']>;
  note: FormControl<ClimbFormRawValue['note']>;
  isTop: FormControl<ClimbFormRawValue['isTop']>;
  boulder: FormControl<ClimbFormRawValue['boulder']>;
  climbedBy: FormControl<ClimbFormRawValue['climbedBy']>;
};

export type ClimbFormGroup = FormGroup<ClimbFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClimbFormService {
  createClimbFormGroup(climb: ClimbFormGroupInput = { id: null }): ClimbFormGroup {
    const climbRawValue = this.convertClimbToClimbRawValue({
      ...this.getFormDefaults(),
      ...climb,
    });
    return new FormGroup<ClimbFormGroupContent>({
      id: new FormControl(
        { value: climbRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      attempts: new FormControl(climbRawValue.attempts, {
        validators: [Validators.min(0)],
      }),
      topDate: new FormControl(climbRawValue.topDate),
      rate: new FormControl(climbRawValue.rate, {
        validators: [Validators.min(1), Validators.max(5)],
      }),
      note: new FormControl(climbRawValue.note),
      isTop: new FormControl(climbRawValue.isTop),
      boulder: new FormControl(climbRawValue.boulder),
      climbedBy: new FormControl(climbRawValue.climbedBy, {
        validators: [Validators.required],
      }),
    });
  }

  getClimb(form: ClimbFormGroup): IClimb | NewClimb {
    return this.convertClimbRawValueToClimb(form.getRawValue() as ClimbFormRawValue | NewClimbFormRawValue);
  }

  resetForm(form: ClimbFormGroup, climb: ClimbFormGroupInput): void {
    const climbRawValue = this.convertClimbToClimbRawValue({ ...this.getFormDefaults(), ...climb });
    form.reset(
      {
        ...climbRawValue,
        id: { value: climbRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClimbFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      topDate: currentTime,
      isTop: false,
    };
  }

  private convertClimbRawValueToClimb(rawClimb: ClimbFormRawValue | NewClimbFormRawValue): IClimb | NewClimb {
    return {
      ...rawClimb,
      topDate: dayjs(rawClimb.topDate, DATE_TIME_FORMAT),
    };
  }

  private convertClimbToClimbRawValue(
    climb: IClimb | (Partial<NewClimb> & ClimbFormDefaults),
  ): ClimbFormRawValue | PartialWithRequiredKeyOf<NewClimbFormRawValue> {
    return {
      ...climb,
      topDate: climb.topDate ? climb.topDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
