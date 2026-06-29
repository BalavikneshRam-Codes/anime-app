import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'search', loadComponent: () => import('./search-results/search-results.component').then(m => m.SearchResultsComponent) },
  { path: 'anime/:id', loadComponent: () => import('./anime-detail/anime-detail.component').then(m => m.AnimeDetailComponent) },
  { path: 'login', loadComponent: () => import('./auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'signup', loadComponent: () => import('./auth/sign-up/sign-up.component').then(m => m.SignUpComponent) },
  { path: 'collection/:type', loadComponent: () => import('./collection/collection.component').then(m => m.CollectionComponent) },
  { path: '**', redirectTo: '' }
];
