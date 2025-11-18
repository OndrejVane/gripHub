import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'gripHubApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'wall',
    data: { pageTitle: 'gripHubApp.wall.home.title' },
    loadChildren: () => import('./wall/wall.routes'),
  },
  {
    path: 'hold',
    data: { pageTitle: 'gripHubApp.hold.home.title' },
    loadChildren: () => import('./hold/hold.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
