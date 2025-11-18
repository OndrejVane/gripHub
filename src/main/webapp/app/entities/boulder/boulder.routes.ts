import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import BoulderResolve from './route/boulder-routing-resolve.service';

const boulderRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/boulder.component').then(m => m.BoulderComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/boulder-detail.component').then(m => m.BoulderDetailComponent),
    resolve: {
      boulder: BoulderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/boulder-update.component').then(m => m.BoulderUpdateComponent),
    resolve: {
      boulder: BoulderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/boulder-update.component').then(m => m.BoulderUpdateComponent),
    resolve: {
      boulder: BoulderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default boulderRoute;
