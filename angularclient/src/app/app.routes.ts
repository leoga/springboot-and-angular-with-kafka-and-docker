import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: 'spaceship/create', loadComponent: () => import('./spaceship-form/spaceship-form.component').then(m => m.SpaceshipFormComponent) },
  { path: 'spaceship/edit/:id', loadComponent: () => import('./spaceship-form/spaceship-form.component').then(m => m.SpaceshipFormComponent) },
  { path: 'spaceship/list', loadComponent: () => import('./spaceship-list/spaceship-list.component').then(m => m.SpaceshipListComponent) },
  { path: '', redirectTo: '/spaceship/list', pathMatch: 'full' }
];
