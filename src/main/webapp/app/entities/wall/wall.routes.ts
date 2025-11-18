import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import WallResolve from './route/wall-routing-resolve.service';

const wallRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/wall.component').then(m => m.WallComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/wall-detail.component').then(m => m.WallDetailComponent),
    resolve: {
      wall: WallResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/wall-update.component').then(m => m.WallUpdateComponent),
    resolve: {
      wall: WallResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/wall-update.component').then(m => m.WallUpdateComponent),
    resolve: {
      wall: WallResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default wallRoute;
