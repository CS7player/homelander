import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonButton } from '@ionic/angular/standalone';

@Component({
 selector: 'app-alert',
 templateUrl: './alert.page.html',
 standalone: true,
 imports: [CommonModule, FormsModule, IonButton]
})
export class AlertPage implements OnInit {
 @Input() title: string = 'Alert';
 @Input() alertMessage: string = '';
 @Input() show: boolean = false;
 @Input() type: string = 'danger';
 @Output() showChange = new EventEmitter<boolean>();
 alertClass: string = 'bg-gray-500';
 constructor() { }
 ngOnInit() {
  switch (this.type) {
   case 'success': this.alertClass = 'bg-green-500 text-white'; break;
   case 'danger': this.alertClass = 'bg-red-500 text-white'; break;
   case 'warning': this.alertClass = 'bg-orange-500 text-white'; break;
   case 'info': this.alertClass = 'bg-blue-500 text-white'; break;
   case 'yellow': this.alertClass = 'bg-yellow-400 text-black'; break;
  }
 }
 closeAlert() {
  this.show = false
  this.showChange.emit(false);
 }

}