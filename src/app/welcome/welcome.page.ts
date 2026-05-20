import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonItem, IonInput, IonButton } from '@ionic/angular/standalone';
import { StorageService } from '../services/storage';
import { AlertPage } from "../alert/alert.page";
import { Router } from '@angular/router';

@Component({
 selector: 'app-welcome',
 templateUrl: './welcome.page.html',
 standalone: true,
 imports: [IonContent, CommonModule, FormsModule, IonItem, IonInput, IonButton, AlertPage]
})
export class WelcomePage implements OnInit {

 private readonly router = inject(Router)
 private readonly storageService = inject(StorageService)
 constructor() { }
 username = ""
 showAlert: boolean = false
 async ngOnInit() {
  this.username = await this.storageService.get("username") || ""
 }

 async onEnter() {
  if (this.username.length < 4) {
   return
  }
  await this.storageService.set("username", this.username);
  this.router.navigate(['/users']);
 }

}
