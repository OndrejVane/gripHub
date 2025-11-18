import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBoulder } from '../boulder.model';
import { BoulderService } from '../service/boulder.service';

const boulderResolve = (route: ActivatedRouteSnapshot): Observable<null | IBoulder> => {
  const id = route.params.id;
  if (id) {
    return inject(BoulderService)
      .find(id)
      .pipe(
        mergeMap((boulder: HttpResponse<IBoulder>) => {
          if (boulder.body) {
            return of(boulder.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default boulderResolve;
