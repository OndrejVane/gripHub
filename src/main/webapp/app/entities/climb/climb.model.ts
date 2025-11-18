import dayjs from 'dayjs/esm';
import { IBoulder } from 'app/entities/boulder/boulder.model';
import { IUser } from 'app/entities/user/user.model';

export interface IClimb {
  id: number;
  attempts?: number | null;
  topDate?: dayjs.Dayjs | null;
  rate?: number | null;
  note?: string | null;
  isTop?: boolean | null;
  boulder?: IBoulder | null;
  climbedBy?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewClimb = Omit<IClimb, 'id'> & { id: null };
