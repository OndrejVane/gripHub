import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../wall.test-samples';

import { WallFormService } from './wall-form.service';

describe('Wall Form Service', () => {
  let service: WallFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WallFormService);
  });

  describe('Service methods', () => {
    describe('createWallFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWallFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            height: expect.any(Object),
            width: expect.any(Object),
            minSlope: expect.any(Object),
            maxSlope: expect.any(Object),
            rows: expect.any(Object),
            columns: expect.any(Object),
            photo: expect.any(Object),
          }),
        );
      });

      it('passing IWall should create a new form with FormGroup', () => {
        const formGroup = service.createWallFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            height: expect.any(Object),
            width: expect.any(Object),
            minSlope: expect.any(Object),
            maxSlope: expect.any(Object),
            rows: expect.any(Object),
            columns: expect.any(Object),
            photo: expect.any(Object),
          }),
        );
      });
    });

    describe('getWall', () => {
      it('should return NewWall for default Wall initial value', () => {
        const formGroup = service.createWallFormGroup(sampleWithNewData);

        const wall = service.getWall(formGroup) as any;

        expect(wall).toMatchObject(sampleWithNewData);
      });

      it('should return NewWall for empty Wall initial value', () => {
        const formGroup = service.createWallFormGroup();

        const wall = service.getWall(formGroup) as any;

        expect(wall).toMatchObject({});
      });

      it('should return IWall', () => {
        const formGroup = service.createWallFormGroup(sampleWithRequiredData);

        const wall = service.getWall(formGroup) as any;

        expect(wall).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWall should not enable id FormControl', () => {
        const formGroup = service.createWallFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWall should disable id FormControl', () => {
        const formGroup = service.createWallFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
