import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../boulder.test-samples';

import { BoulderFormService } from './boulder-form.service';

describe('Boulder Form Service', () => {
  let service: BoulderFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BoulderFormService);
  });

  describe('Service methods', () => {
    describe('createBoulderFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBoulderFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            grade: expect.any(Object),
            note: expect.any(Object),
            slope: expect.any(Object),
            wall: expect.any(Object),
          }),
        );
      });

      it('passing IBoulder should create a new form with FormGroup', () => {
        const formGroup = service.createBoulderFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            grade: expect.any(Object),
            note: expect.any(Object),
            slope: expect.any(Object),
            wall: expect.any(Object),
          }),
        );
      });
    });

    describe('getBoulder', () => {
      it('should return NewBoulder for default Boulder initial value', () => {
        const formGroup = service.createBoulderFormGroup(sampleWithNewData);

        const boulder = service.getBoulder(formGroup) as any;

        expect(boulder).toMatchObject(sampleWithNewData);
      });

      it('should return NewBoulder for empty Boulder initial value', () => {
        const formGroup = service.createBoulderFormGroup();

        const boulder = service.getBoulder(formGroup) as any;

        expect(boulder).toMatchObject({});
      });

      it('should return IBoulder', () => {
        const formGroup = service.createBoulderFormGroup(sampleWithRequiredData);

        const boulder = service.getBoulder(formGroup) as any;

        expect(boulder).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBoulder should not enable id FormControl', () => {
        const formGroup = service.createBoulderFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBoulder should disable id FormControl', () => {
        const formGroup = service.createBoulderFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
