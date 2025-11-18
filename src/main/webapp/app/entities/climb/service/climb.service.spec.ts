import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IClimb } from '../climb.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../climb.test-samples';

import { ClimbService, RestClimb } from './climb.service';

const requireRestSample: RestClimb = {
  ...sampleWithRequiredData,
  topDate: sampleWithRequiredData.topDate?.toJSON(),
};

describe('Climb Service', () => {
  let service: ClimbService;
  let httpMock: HttpTestingController;
  let expectedResult: IClimb | IClimb[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ClimbService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Climb', () => {
      const climb = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(climb).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Climb', () => {
      const climb = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(climb).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Climb', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Climb', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Climb', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addClimbToCollectionIfMissing', () => {
      it('should add a Climb to an empty array', () => {
        const climb: IClimb = sampleWithRequiredData;
        expectedResult = service.addClimbToCollectionIfMissing([], climb);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(climb);
      });

      it('should not add a Climb to an array that contains it', () => {
        const climb: IClimb = sampleWithRequiredData;
        const climbCollection: IClimb[] = [
          {
            ...climb,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClimbToCollectionIfMissing(climbCollection, climb);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Climb to an array that doesn't contain it", () => {
        const climb: IClimb = sampleWithRequiredData;
        const climbCollection: IClimb[] = [sampleWithPartialData];
        expectedResult = service.addClimbToCollectionIfMissing(climbCollection, climb);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(climb);
      });

      it('should add only unique Climb to an array', () => {
        const climbArray: IClimb[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const climbCollection: IClimb[] = [sampleWithRequiredData];
        expectedResult = service.addClimbToCollectionIfMissing(climbCollection, ...climbArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const climb: IClimb = sampleWithRequiredData;
        const climb2: IClimb = sampleWithPartialData;
        expectedResult = service.addClimbToCollectionIfMissing([], climb, climb2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(climb);
        expect(expectedResult).toContain(climb2);
      });

      it('should accept null and undefined values', () => {
        const climb: IClimb = sampleWithRequiredData;
        expectedResult = service.addClimbToCollectionIfMissing([], null, climb, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(climb);
      });

      it('should return initial array if no Climb is added', () => {
        const climbCollection: IClimb[] = [sampleWithRequiredData];
        expectedResult = service.addClimbToCollectionIfMissing(climbCollection, undefined, null);
        expect(expectedResult).toEqual(climbCollection);
      });
    });

    describe('compareClimb', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClimb(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 8939 };
        const entity2 = null;

        const compareResult1 = service.compareClimb(entity1, entity2);
        const compareResult2 = service.compareClimb(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 8939 };
        const entity2 = { id: 16534 };

        const compareResult1 = service.compareClimb(entity1, entity2);
        const compareResult2 = service.compareClimb(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 8939 };
        const entity2 = { id: 8939 };

        const compareResult1 = service.compareClimb(entity1, entity2);
        const compareResult2 = service.compareClimb(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
