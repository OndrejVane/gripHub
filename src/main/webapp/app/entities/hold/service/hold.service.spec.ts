import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IHold } from '../hold.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../hold.test-samples';

import { HoldService } from './hold.service';

const requireRestSample: IHold = {
  ...sampleWithRequiredData,
};

describe('Hold Service', () => {
  let service: HoldService;
  let httpMock: HttpTestingController;
  let expectedResult: IHold | IHold[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(HoldService);
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

    it('should create a Hold', () => {
      const hold = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(hold).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Hold', () => {
      const hold = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(hold).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Hold', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Hold', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Hold', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addHoldToCollectionIfMissing', () => {
      it('should add a Hold to an empty array', () => {
        const hold: IHold = sampleWithRequiredData;
        expectedResult = service.addHoldToCollectionIfMissing([], hold);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hold);
      });

      it('should not add a Hold to an array that contains it', () => {
        const hold: IHold = sampleWithRequiredData;
        const holdCollection: IHold[] = [
          {
            ...hold,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHoldToCollectionIfMissing(holdCollection, hold);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Hold to an array that doesn't contain it", () => {
        const hold: IHold = sampleWithRequiredData;
        const holdCollection: IHold[] = [sampleWithPartialData];
        expectedResult = service.addHoldToCollectionIfMissing(holdCollection, hold);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hold);
      });

      it('should add only unique Hold to an array', () => {
        const holdArray: IHold[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const holdCollection: IHold[] = [sampleWithRequiredData];
        expectedResult = service.addHoldToCollectionIfMissing(holdCollection, ...holdArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const hold: IHold = sampleWithRequiredData;
        const hold2: IHold = sampleWithPartialData;
        expectedResult = service.addHoldToCollectionIfMissing([], hold, hold2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hold);
        expect(expectedResult).toContain(hold2);
      });

      it('should accept null and undefined values', () => {
        const hold: IHold = sampleWithRequiredData;
        expectedResult = service.addHoldToCollectionIfMissing([], null, hold, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hold);
      });

      it('should return initial array if no Hold is added', () => {
        const holdCollection: IHold[] = [sampleWithRequiredData];
        expectedResult = service.addHoldToCollectionIfMissing(holdCollection, undefined, null);
        expect(expectedResult).toEqual(holdCollection);
      });
    });

    describe('compareHold', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHold(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20041 };
        const entity2 = null;

        const compareResult1 = service.compareHold(entity1, entity2);
        const compareResult2 = service.compareHold(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20041 };
        const entity2 = { id: 23041 };

        const compareResult1 = service.compareHold(entity1, entity2);
        const compareResult2 = service.compareHold(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20041 };
        const entity2 = { id: 20041 };

        const compareResult1 = service.compareHold(entity1, entity2);
        const compareResult2 = service.compareHold(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
