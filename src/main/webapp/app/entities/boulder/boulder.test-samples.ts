import { IBoulder, NewBoulder } from './boulder.model';

export const sampleWithRequiredData: IBoulder = {
  id: 30391,
  name: 'venture',
  grade: 13707,
  slope: 71,
};

export const sampleWithPartialData: IBoulder = {
  id: 6077,
  name: 'or how slowly',
  grade: 32761,
  slope: 4,
};

export const sampleWithFullData: IBoulder = {
  id: 193,
  name: 'each',
  grade: 19905,
  note: 'assail gee hastily',
  slope: 66,
};

export const sampleWithNewData: NewBoulder = {
  name: 'investigate deer napkin',
  grade: 28948,
  slope: 31,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
