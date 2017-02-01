import { Component } from '@angular/core';
import { Platform } from 'ionic-angular';
import { StatusBar } from 'ionic-native';
import '../app/rxjs-operators.ts';
// custom providers:
import { AuthenticationProvider } from '../providers/authentication-provider';
import { HttpProvider } from '../providers/http-provider';
import { ValidatorProvider } from '../providers/validator-provider';
// custom pages:
import { SplashScreenPage } from '../pages/splash-screen/splash-screen';


@Component({
  template: `<ion-nav [root]="rootPage"></ion-nav>`,
  providers: [AuthenticationProvider, HttpProvider, ValidatorProvider]
})
export class MyApp {
  rootPage: any;

  constructor(platform: Platform) {

    this.rootPage = SplashScreenPage;

    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      StatusBar.styleDefault();
    });
  }
}
