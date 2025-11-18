import { IWall } from 'app/entities/wall/wall.model';

export interface IBoulder {
  id: number;
  name?: string | null;
  grade?: number | null;
  note?: string | null;
  slope?: number | null;
  walls?: IWall[] | null;
}

export type NewBoulder = Omit<IBoulder, 'id'> & { id: null };
