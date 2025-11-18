import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHold, NewHold } from '../hold.model';

export type PartialUpdateHold = Partial<IHold> & Pick<IHold, 'id'>;

export type EntityResponseType = HttpResponse<IHold>;
export type EntityArrayResponseType = HttpResponse<IHold[]>;

@Injectable({ providedIn: 'root' })
export class HoldService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/holds');

  create(hold: NewHold): Observable<EntityResponseType> {
    return this.http.post<IHold>(this.resourceUrl, hold, { observe: 'response' });
  }

  update(hold: IHold): Observable<EntityResponseType> {
    return this.http.put<IHold>(`${this.resourceUrl}/${this.getHoldIdentifier(hold)}`, hold, { observe: 'response' });
  }

  partialUpdate(hold: PartialUpdateHold): Observable<EntityResponseType> {
    return this.http.patch<IHold>(`${this.resourceUrl}/${this.getHoldIdentifier(hold)}`, hold, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHold>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHold[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getHoldIdentifier(hold: Pick<IHold, 'id'>): number {
    return hold.id;
  }

  compareHold(o1: Pick<IHold, 'id'> | null, o2: Pick<IHold, 'id'> | null): boolean {
    return o1 && o2 ? this.getHoldIdentifier(o1) === this.getHoldIdentifier(o2) : o1 === o2;
  }

  addHoldToCollectionIfMissing<Type extends Pick<IHold, 'id'>>(
    holdCollection: Type[],
    ...holdsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const holds: Type[] = holdsToCheck.filter(isPresent);
    if (holds.length > 0) {
      const holdCollectionIdentifiers = holdCollection.map(holdItem => this.getHoldIdentifier(holdItem));
      const holdsToAdd = holds.filter(holdItem => {
        const holdIdentifier = this.getHoldIdentifier(holdItem);
        if (holdCollectionIdentifiers.includes(holdIdentifier)) {
          return false;
        }
        holdCollectionIdentifiers.push(holdIdentifier);
        return true;
      });
      return [...holdsToAdd, ...holdCollection];
    }
    return holdCollection;
  }
}
