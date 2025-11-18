import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHold } from '../hold.model';
import { HoldService } from '../service/hold.service';

const holdResolve = (route: ActivatedRouteSnapshot): Observable<null | IHold> => {
  const id = route.params.id;
  if (id) {
    return inject(HoldService)
      .find(id)
      .pipe(
        mergeMap((hold: HttpResponse<IHold>) => {
          if (hold.body) {
            return of(hold.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default holdResolve;
