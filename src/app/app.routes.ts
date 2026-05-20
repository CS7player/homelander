import { Routes } from '@angular/router';

export const routes: Routes = [
 {
  path: 'welcome',
  loadComponent: () => import('./welcome/welcome.page').then(m => m.WelcomePage)
 },
 {
  path: 'users',
  loadComponent: () => import('./users/users.page').then(m => m.UsersPage)
 },
 {
  path: 'chat',
  loadComponent: () => import('./chat/chat.page').then(m => m.ChatPage)
 },
 {
  path: 'alert',
  loadComponent: () => import('./alert/alert.page').then(m => m.AlertPage)
 },
 { path: '', redirectTo: 'welcome', pathMatch: 'full' },
];
