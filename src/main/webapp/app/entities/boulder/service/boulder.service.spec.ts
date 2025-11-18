import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IBoulder } from '../boulder.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../boulder.test-samples';

import { BoulderService } from './boulder.service';

const requireRestSample: IBoulder = {
  ...sampleWithRequiredData,
};

describe('Boulder Service', () => {
  let service: BoulderService;
  let httpMock: HttpTestingController;
  let expectedResult: IBoulder | IBoulder[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BoulderService);
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

    it('should create a Boulder', () => {
      const boulder = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(boulder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Boulder', () => {
      const boulder = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(boulder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Boulder', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Boulder', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Boulder', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBoulderToCollectionIfMissing', () => {
      it('should add a Boulder to an empty array', () => {
        const boulder: IBoulder = sampleWithRequiredData;
        expectedResult = service.addBoulderToCollectionIfMissing([], boulder);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(boulder);
      });

      it('should not add a Boulder to an array that contains it', () => {
        const boulder: IBoulder = sampleWithRequiredData;
        const boulderCollection: IBoulder[] = [
          {
            ...boulder,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBoulderToCollectionIfMissing(boulderCollection, boulder);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Boulder to an array that doesn't contain it", () => {
        const boulder: IBoulder = sampleWithRequiredData;
        const boulderCollection: IBoulder[] = [sampleWithPartialData];
        expectedResult = service.addBoulderToCollectionIfMissing(boulderCollection, boulder);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(boulder);
      });

      it('should add only unique Boulder to an array', () => {
        const boulderArray: IBoulder[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const boulderCollection: IBoulder[] = [sampleWithRequiredData];
        expectedResult = service.addBoulderToCollectionIfMissing(boulderCollection, ...boulderArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const boulder: IBoulder = sampleWithRequiredData;
        const boulder2: IBoulder = sampleWithPartialData;
        expectedResult = service.addBoulderToCollectionIfMissing([], boulder, boulder2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(boulder);
        expect(expectedResult).toContain(boulder2);
      });

      it('should accept null and undefined values', () => {
        const boulder: IBoulder = sampleWithRequiredData;
        expectedResult = service.addBoulderToCollectionIfMissing([], null, boulder, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(boulder);
      });

      it('should return initial array if no Boulder is added', () => {
        const boulderCollection: IBoulder[] = [sampleWithRequiredData];
        expectedResult = service.addBoulderToCollectionIfMissing(boulderCollection, undefined, null);
        expect(expectedResult).toEqual(boulderCollection);
      });
    });

    describe('compareBoulder', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBoulder(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 24244 };
        const entity2 = null;

        const compareResult1 = service.compareBoulder(entity1, entity2);
        const compareResult2 = service.compareBoulder(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 24244 };
        const entity2 = { id: 3829 };

        const compareResult1 = service.compareBoulder(entity1, entity2);
        const compareResult2 = service.compareBoulder(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 24244 };
        const entity2 = { id: 24244 };

        const compareResult1 = service.compareBoulder(entity1, entity2);
        const compareResult2 = service.compareBoulder(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
