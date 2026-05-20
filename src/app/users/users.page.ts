import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';
import { StorageService } from '../services/storage';
import { LanChatService } from '../services/lan-chat';

@Component({
 selector: 'app-users',
 templateUrl: './users.page.html',
 standalone: true,
 imports: [IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule]
})
export class UsersPage implements OnInit {

 private readonly router = inject(Router);
 private readonly storageService = inject(StorageService);
 readonly lan = inject(LanChatService);
 username = '';
 async ngOnInit() {
  await this.validateUsername();
 }

 async validateUsername() {
  this.username = await this.storageService.get('username') || '';
  if (this.username.length === 0) {
   this.router.navigate(['/welcome']);
   return;
  }
  // START LAN CHAT
  await this.lan.start(this.username);
 }

 onUserClick(user: any) {
  this.router.navigate(['/chat'], { state: { user } });
 }
}