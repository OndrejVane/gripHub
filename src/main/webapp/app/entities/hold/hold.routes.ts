import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import HoldResolve from './route/hold-routing-resolve.service';

const holdRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/hold.component').then(m => m.HoldComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/hold-detail.component').then(m => m.HoldDetailComponent),
    resolve: {
      hold: HoldResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/hold-update.component').then(m => m.HoldUpdateComponent),
    resolve: {
      hold: HoldResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/hold-update.component').then(m => m.HoldUpdateComponent),
    resolve: {
      hold: HoldResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default holdRoute;
