import { Component } from '@angular/core';
import { Platform } from 'ionic-angular';
import { StatusBar } from 'ionic-native';
// import { HomePage } from '../pages/home/home';
import { LoginPage } from '../pages/login/login';
import { MainPage } from '../pages/main/main';
import '../app/rxjs-operators.ts';
import { AuthenticationProvider } from '../providers/authentication';


@Component({
  template: `<ion-nav [root]="rootPage"></ion-nav>`
})
export class MyApp {
  rootPage: any;

  constructor(platform: Platform, private auth: AuthenticationProvider) {
    if(auth.isLoggedIn()){
      this.rootPage = MainPage;
    } else {
      this.rootPage = LoginPage;
    }
    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      StatusBar.styleDefault();
    });
  }
}
