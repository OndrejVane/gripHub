import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../hold.test-samples';

import { HoldFormService } from './hold-form.service';

describe('Hold Form Service', () => {
  let service: HoldFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HoldFormService);
  });

  describe('Service methods', () => {
    describe('createHoldFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHoldFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            photoCoordinatesX: expect.any(Object),
            photoCoordinatesY: expect.any(Object),
            column: expect.any(Object),
            row: expect.any(Object),
            holdType: expect.any(Object),
            holdDifficulty: expect.any(Object),
            wall: expect.any(Object),
          }),
        );
      });

      it('passing IHold should create a new form with FormGroup', () => {
        const formGroup = service.createHoldFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            photoCoordinatesX: expect.any(Object),
            photoCoordinatesY: expect.any(Object),
            column: expect.any(Object),
            row: expect.any(Object),
            holdType: expect.any(Object),
            holdDifficulty: expect.any(Object),
            wall: expect.any(Object),
          }),
        );
      });
    });

    describe('getHold', () => {
      it('should return NewHold for default Hold initial value', () => {
        const formGroup = service.createHoldFormGroup(sampleWithNewData);

        const hold = service.getHold(formGroup) as any;

        expect(hold).toMatchObject(sampleWithNewData);
      });

      it('should return NewHold for empty Hold initial value', () => {
        const formGroup = service.createHoldFormGroup();

        const hold = service.getHold(formGroup) as any;

        expect(hold).toMatchObject({});
      });

      it('should return IHold', () => {
        const formGroup = service.createHoldFormGroup(sampleWithRequiredData);

        const hold = service.getHold(formGroup) as any;

        expect(hold).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHold should not enable id FormControl', () => {
        const formGroup = service.createHoldFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHold should disable id FormControl', () => {
        const formGroup = service.createHoldFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
