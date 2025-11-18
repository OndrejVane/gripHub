import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../climb.test-samples';

import { ClimbFormService } from './climb-form.service';

describe('Climb Form Service', () => {
  let service: ClimbFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClimbFormService);
  });

  describe('Service methods', () => {
    describe('createClimbFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createClimbFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            attempts: expect.any(Object),
            topDate: expect.any(Object),
            rate: expect.any(Object),
            note: expect.any(Object),
            isTop: expect.any(Object),
            boulder: expect.any(Object),
            climbedBy: expect.any(Object),
          }),
        );
      });

      it('passing IClimb should create a new form with FormGroup', () => {
        const formGroup = service.createClimbFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            attempts: expect.any(Object),
            topDate: expect.any(Object),
            rate: expect.any(Object),
            note: expect.any(Object),
            isTop: expect.any(Object),
            boulder: expect.any(Object),
            climbedBy: expect.any(Object),
          }),
        );
      });
    });

    describe('getClimb', () => {
      it('should return NewClimb for default Climb initial value', () => {
        const formGroup = service.createClimbFormGroup(sampleWithNewData);

        const climb = service.getClimb(formGroup) as any;

        expect(climb).toMatchObject(sampleWithNewData);
      });

      it('should return NewClimb for empty Climb initial value', () => {
        const formGroup = service.createClimbFormGroup();

        const climb = service.getClimb(formGroup) as any;

        expect(climb).toMatchObject({});
      });

      it('should return IClimb', () => {
        const formGroup = service.createClimbFormGroup(sampleWithRequiredData);

        const climb = service.getClimb(formGroup) as any;

        expect(climb).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IClimb should not enable id FormControl', () => {
        const formGroup = service.createClimbFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewClimb should disable id FormControl', () => {
        const formGroup = service.createClimbFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
