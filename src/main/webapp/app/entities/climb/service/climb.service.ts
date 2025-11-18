import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClimb, NewClimb } from '../climb.model';

export type PartialUpdateClimb = Partial<IClimb> & Pick<IClimb, 'id'>;

type RestOf<T extends IClimb | NewClimb> = Omit<T, 'topDate'> & {
  topDate?: string | null;
};

export type RestClimb = RestOf<IClimb>;

export type NewRestClimb = RestOf<NewClimb>;

export type PartialUpdateRestClimb = RestOf<PartialUpdateClimb>;

export type EntityResponseType = HttpResponse<IClimb>;
export type EntityArrayResponseType = HttpResponse<IClimb[]>;

@Injectable({ providedIn: 'root' })
export class ClimbService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/climbs');

  create(climb: NewClimb): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(climb);
    return this.http.post<RestClimb>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(climb: IClimb): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(climb);
    return this.http
      .put<RestClimb>(`${this.resourceUrl}/${this.getClimbIdentifier(climb)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(climb: PartialUpdateClimb): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(climb);
    return this.http
      .patch<RestClimb>(`${this.resourceUrl}/${this.getClimbIdentifier(climb)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestClimb>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestClimb[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getClimbIdentifier(climb: Pick<IClimb, 'id'>): number {
    return climb.id;
  }

  compareClimb(o1: Pick<IClimb, 'id'> | null, o2: Pick<IClimb, 'id'> | null): boolean {
    return o1 && o2 ? this.getClimbIdentifier(o1) === this.getClimbIdentifier(o2) : o1 === o2;
  }

  addClimbToCollectionIfMissing<Type extends Pick<IClimb, 'id'>>(
    climbCollection: Type[],
    ...climbsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const climbs: Type[] = climbsToCheck.filter(isPresent);
    if (climbs.length > 0) {
      const climbCollectionIdentifiers = climbCollection.map(climbItem => this.getClimbIdentifier(climbItem));
      const climbsToAdd = climbs.filter(climbItem => {
        const climbIdentifier = this.getClimbIdentifier(climbItem);
        if (climbCollectionIdentifiers.includes(climbIdentifier)) {
          return false;
        }
        climbCollectionIdentifiers.push(climbIdentifier);
        return true;
      });
      return [...climbsToAdd, ...climbCollection];
    }
    return climbCollection;
  }

  protected convertDateFromClient<T extends IClimb | NewClimb | PartialUpdateClimb>(climb: T): RestOf<T> {
    return {
      ...climb,
      topDate: climb.topDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restClimb: RestClimb): IClimb {
    return {
      ...restClimb,
      topDate: restClimb.topDate ? dayjs(restClimb.topDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestClimb>): HttpResponse<IClimb> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestClimb[]>): HttpResponse<IClimb[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
