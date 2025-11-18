import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWall, NewWall } from '../wall.model';

export type PartialUpdateWall = Partial<IWall> & Pick<IWall, 'id'>;

export type EntityResponseType = HttpResponse<IWall>;
export type EntityArrayResponseType = HttpResponse<IWall[]>;

@Injectable({ providedIn: 'root' })
export class WallService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/walls');

  create(wall: NewWall): Observable<EntityResponseType> {
    return this.http.post<IWall>(this.resourceUrl, wall, { observe: 'response' });
  }

  update(wall: IWall): Observable<EntityResponseType> {
    return this.http.put<IWall>(`${this.resourceUrl}/${this.getWallIdentifier(wall)}`, wall, { observe: 'response' });
  }

  partialUpdate(wall: PartialUpdateWall): Observable<EntityResponseType> {
    return this.http.patch<IWall>(`${this.resourceUrl}/${this.getWallIdentifier(wall)}`, wall, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IWall>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IWall[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getWallIdentifier(wall: Pick<IWall, 'id'>): number {
    return wall.id;
  }

  compareWall(o1: Pick<IWall, 'id'> | null, o2: Pick<IWall, 'id'> | null): boolean {
    return o1 && o2 ? this.getWallIdentifier(o1) === this.getWallIdentifier(o2) : o1 === o2;
  }

  addWallToCollectionIfMissing<Type extends Pick<IWall, 'id'>>(
    wallCollection: Type[],
    ...wallsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const walls: Type[] = wallsToCheck.filter(isPresent);
    if (walls.length > 0) {
      const wallCollectionIdentifiers = wallCollection.map(wallItem => this.getWallIdentifier(wallItem));
      const wallsToAdd = walls.filter(wallItem => {
        const wallIdentifier = this.getWallIdentifier(wallItem);
        if (wallCollectionIdentifiers.includes(wallIdentifier)) {
          return false;
        }
        wallCollectionIdentifiers.push(wallIdentifier);
        return true;
      });
      return [...wallsToAdd, ...wallCollection];
    }
    return wallCollection;
  }
}
