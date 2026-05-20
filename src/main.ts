import { bootstrapApplication } from '@angular/platform-browser';
import { RouteReuseStrategy, provideRouter, withPreloading, PreloadAllModules } from '@angular/router';
import { IonicRouteStrategy, provideIonicAngular } from '@ionic/angular/standalone';

import { importProvidersFrom } from '@angular/core';
import { IonicStorageModule } from '@ionic/storage-angular';
import { addIcons } from "ionicons"
import { chevronBackOutline, heart, send } from "ionicons/icons"
import { routes } from './app/app.routes';
import { AppComponent } from './app/app.component';

addIcons({
 send, heart, "chevron-back-outline": chevronBackOutline,
})

bootstrapApplication(AppComponent, {
 providers: [
  { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
  provideIonicAngular(),
  provideRouter(routes, withPreloading(PreloadAllModules)),
  importProvidersFrom(IonicStorageModule.forRoot())
 ],
});