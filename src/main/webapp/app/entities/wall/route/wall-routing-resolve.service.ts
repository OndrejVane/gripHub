import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWall } from '../wall.model';
import { WallService } from '../service/wall.service';

const wallResolve = (route: ActivatedRouteSnapshot): Observable<null | IWall> => {
  const id = route.params.id;
  if (id) {
    return inject(WallService)
      .find(id)
      .pipe(
        mergeMap((wall: HttpResponse<IWall>) => {
          if (wall.body) {
            return of(wall.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default wallResolve;
