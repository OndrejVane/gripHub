export interface IWall {
  id: number;
  name?: string | null;
  height?: number | null;
  width?: number | null;
  minSlope?: number | null;
  maxSlope?: number | null;
  rows?: number | null;
  columns?: number | null;
  photo?: string | null;
  photoContentType?: string | null;
}

export type NewWall = Omit<IWall, 'id'> & { id: null };
