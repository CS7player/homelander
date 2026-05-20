import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { IonContent, IonInput, IonHeader, IonTitle, IonToolbar, IonFooter, IonButton, IonButtons, IonItem } from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LanChatService } from '../services/lan-chat';
import { IonIcon } from '@ionic/angular/standalone';

@Component({
 selector: 'app-chat',
 templateUrl: './chat.page.html',
 standalone: true,
 imports: [IonContent, IonInput, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule, IonFooter, IonButton, IonButtons, IonIcon]
})
export class ChatPage implements OnInit {

 @ViewChild(IonContent) content!: IonContent;

 private readonly router = inject(Router);
 readonly lan = inject(LanChatService);
 selectedUser: any;
 messageText = '';
 ngOnInit() {
  this.selectedUser = history.state.user;
 }

 ngAfterViewChecked() {
  this.scrollToBottom();
 }

 scrollToBottom() {
  this.content?.scrollToBottom(200);
 }

 toUserList() {
  this.router.navigate(['/users']);
 }

 async sendMessage() {
  if (!this.messageText.trim()) {
   return;
  }
  await this.lan.sendMessage(this.selectedUser.username, this.messageText);
  this.messageText = '';
  this.scrollToBottom();
 }
}