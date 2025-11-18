import { IWall, NewWall } from './wall.model';

export const sampleWithRequiredData: IWall = {
  id: 22644,
  name: 'parched bowed',
  height: 25357,
  width: 11996,
  minSlope: 15,
  maxSlope: 36,
  rows: 22632,
  columns: 10214,
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
};

export const sampleWithPartialData: IWall = {
  id: 2729,
  name: 'fence',
  height: 7482,
  width: 12499,
  minSlope: 77,
  maxSlope: 22,
  rows: 30485,
  columns: 25254,
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
};

export const sampleWithFullData: IWall = {
  id: 24944,
  name: 'punctuation',
  height: 2567,
  width: 29405,
  minSlope: 55,
  maxSlope: 5,
  rows: 23099,
  columns: 30586,
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
};

export const sampleWithNewData: NewWall = {
  name: 'scaly',
  height: 22249,
  width: 644,
  minSlope: 43,
  maxSlope: 2,
  rows: 19071,
  columns: 14784,
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
