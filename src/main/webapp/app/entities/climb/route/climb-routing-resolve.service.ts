import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClimb } from '../climb.model';
import { ClimbService } from '../service/climb.service';

const climbResolve = (route: ActivatedRouteSnapshot): Observable<null | IClimb> => {
  const id = route.params.id;
  if (id) {
    return inject(ClimbService)
      .find(id)
      .pipe(
        mergeMap((climb: HttpResponse<IClimb>) => {
          if (climb.body) {
            return of(climb.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default climbResolve;
