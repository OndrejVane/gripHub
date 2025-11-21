import { IHold, NewHold } from './hold.model';

export const sampleWithRequiredData: IHold = {
  id: 3269,
};

export const sampleWithPartialData: IHold = {
  id: 14773,
  photoCoordinatesY: 13689,
  row: 21950,
  holdType: 'JUG',
};

export const sampleWithFullData: IHold = {
  id: 12508,
  photoCoordinatesX: 22741,
  photoCoordinatesY: 18700,
  column: 9672,
  row: 7977,
  holdType: 'FOOTHOLD',
  holdDifficulty: 'HARD',
};

export const sampleWithNewData: NewHold = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
