import { IBoulder, NewBoulder } from './boulder.model';

export const sampleWithRequiredData: IBoulder = {
  id: 30391,
  slope: 29,
};

export const sampleWithPartialData: IBoulder = {
  id: 2172,
  grade: 24294,
  slope: 90,
};

export const sampleWithFullData: IBoulder = {
  id: 193,
  name: 'each',
  grade: 19905,
  note: 'assail gee hastily',
  slope: 66,
};

export const sampleWithNewData: NewBoulder = {
  slope: 69,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
