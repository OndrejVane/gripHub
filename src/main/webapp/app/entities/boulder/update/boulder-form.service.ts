import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBoulder, NewBoulder } from '../boulder.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBoulder for edit and NewBoulderFormGroupInput for create.
 */
type BoulderFormGroupInput = IBoulder | PartialWithRequiredKeyOf<NewBoulder>;

type BoulderFormDefaults = Pick<NewBoulder, 'id' | 'walls'>;

type BoulderFormGroupContent = {
  id: FormControl<IBoulder['id'] | NewBoulder['id']>;
  name: FormControl<IBoulder['name']>;
  grade: FormControl<IBoulder['grade']>;
  note: FormControl<IBoulder['note']>;
  slope: FormControl<IBoulder['slope']>;
  walls: FormControl<IBoulder['walls']>;
};

export type BoulderFormGroup = FormGroup<BoulderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BoulderFormService {
  createBoulderFormGroup(boulder: BoulderFormGroupInput = { id: null }): BoulderFormGroup {
    const boulderRawValue = {
      ...this.getFormDefaults(),
      ...boulder,
    };
    return new FormGroup<BoulderFormGroupContent>({
      id: new FormControl(
        { value: boulderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(boulderRawValue.name, {
        validators: [Validators.required],
      }),
      grade: new FormControl(boulderRawValue.grade, {
        validators: [Validators.required, Validators.min(0)],
      }),
      note: new FormControl(boulderRawValue.note),
      slope: new FormControl(boulderRawValue.slope, {
        validators: [Validators.required, Validators.min(0), Validators.max(90)],
      }),
      walls: new FormControl(boulderRawValue.walls ?? []),
    });
  }

  getBoulder(form: BoulderFormGroup): IBoulder | NewBoulder {
    return form.getRawValue() as IBoulder | NewBoulder;
  }

  resetForm(form: BoulderFormGroup, boulder: BoulderFormGroupInput): void {
    const boulderRawValue = { ...this.getFormDefaults(), ...boulder };
    form.reset(
      {
        ...boulderRawValue,
        id: { value: boulderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BoulderFormDefaults {
    return {
      id: null,
      walls: [],
    };
  }
}
