import dayjs from 'dayjs/esm';

import { IClimb, NewClimb } from './climb.model';

export const sampleWithRequiredData: IClimb = {
  id: 28742,
};

export const sampleWithPartialData: IClimb = {
  id: 19603,
  note: 'bar after accomplished',
  isTop: false,
};

export const sampleWithFullData: IClimb = {
  id: 3537,
  attempts: 24475,
  topDate: dayjs('2025-11-18T19:38'),
  rate: 2,
  note: 'agile',
  isTop: false,
};

export const sampleWithNewData: NewClimb = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
