import { IWall } from 'app/entities/wall/wall.model';
import { IBoulder } from 'app/entities/boulder/boulder.model';
import { HoldType } from 'app/entities/enumerations/hold-type.model';
import { Difficulty } from 'app/entities/enumerations/difficulty.model';

export interface IHold {
  id: number;
  photoCoordinatesX?: number | null;
  photoCoordinatesY?: number | null;
  column?: number | null;
  row?: number | null;
  holdType?: keyof typeof HoldType | null;
  holdDifficulty?: keyof typeof Difficulty | null;
  wall?: IWall | null;
  boulder?: IBoulder | null;
}

export type NewHold = Omit<IHold, 'id'> & { id: null };
