import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ClimbResolve from './route/climb-routing-resolve.service';

const climbRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/climb.component').then(m => m.ClimbComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/climb-detail.component').then(m => m.ClimbDetailComponent),
    resolve: {
      climb: ClimbResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/climb-update.component').then(m => m.ClimbUpdateComponent),
    resolve: {
      climb: ClimbResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/climb-update.component').then(m => m.ClimbUpdateComponent),
    resolve: {
      climb: ClimbResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default climbRoute;
