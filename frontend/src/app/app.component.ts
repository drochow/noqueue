import { Component } from '@angular/core';
import { Platform } from 'ionic-angular';
import { StatusBar } from 'ionic-native';
import '../app/rxjs-operators.ts';
import { DashboardPage } from '../pages/dashboard/dashboard';
import { AuthenticationProvider } from '../providers/authentication-provider';
import { HttpProvider } from '../providers/http-provider';
import { UserConfigurationProvider } from '../providers/user-configuration-provider';
import { ShopsProvider } from '../providers/shops-provider';
import { QueuesProvider } from '../providers/queues-provider';
import { UsersProvider } from '../providers/users-provider';
import { ValidatorProvider } from '../providers/validator-provider';


@Component({
  template: `<ion-nav [root]="rootPage"></ion-nav>`,
  providers: [AuthenticationProvider, HttpProvider, UserConfigurationProvider, ShopsProvider, QueuesProvider, UsersProvider, ValidatorProvider]
})
export class MyApp {
  rootPage: any;

  constructor(platform: Platform) {


    this.rootPage = DashboardPage;

    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      StatusBar.styleDefault();
    });
  }
}
