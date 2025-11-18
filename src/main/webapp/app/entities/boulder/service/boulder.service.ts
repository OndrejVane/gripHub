import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBoulder, NewBoulder } from '../boulder.model';

export type PartialUpdateBoulder = Partial<IBoulder> & Pick<IBoulder, 'id'>;

export type EntityResponseType = HttpResponse<IBoulder>;
export type EntityArrayResponseType = HttpResponse<IBoulder[]>;

@Injectable({ providedIn: 'root' })
export class BoulderService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/boulders');

  create(boulder: NewBoulder): Observable<EntityResponseType> {
    return this.http.post<IBoulder>(this.resourceUrl, boulder, { observe: 'response' });
  }

  update(boulder: IBoulder): Observable<EntityResponseType> {
    return this.http.put<IBoulder>(`${this.resourceUrl}/${this.getBoulderIdentifier(boulder)}`, boulder, { observe: 'response' });
  }

  partialUpdate(boulder: PartialUpdateBoulder): Observable<EntityResponseType> {
    return this.http.patch<IBoulder>(`${this.resourceUrl}/${this.getBoulderIdentifier(boulder)}`, boulder, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBoulder>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBoulder[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBoulderIdentifier(boulder: Pick<IBoulder, 'id'>): number {
    return boulder.id;
  }

  compareBoulder(o1: Pick<IBoulder, 'id'> | null, o2: Pick<IBoulder, 'id'> | null): boolean {
    return o1 && o2 ? this.getBoulderIdentifier(o1) === this.getBoulderIdentifier(o2) : o1 === o2;
  }

  addBoulderToCollectionIfMissing<Type extends Pick<IBoulder, 'id'>>(
    boulderCollection: Type[],
    ...bouldersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const boulders: Type[] = bouldersToCheck.filter(isPresent);
    if (boulders.length > 0) {
      const boulderCollectionIdentifiers = boulderCollection.map(boulderItem => this.getBoulderIdentifier(boulderItem));
      const bouldersToAdd = boulders.filter(boulderItem => {
        const boulderIdentifier = this.getBoulderIdentifier(boulderItem);
        if (boulderCollectionIdentifiers.includes(boulderIdentifier)) {
          return false;
        }
        boulderCollectionIdentifiers.push(boulderIdentifier);
        return true;
      });
      return [...bouldersToAdd, ...boulderCollection];
    }
    return boulderCollection;
  }
}
