import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { DashboardPage } from '../dashboard/dashboard';

/*
  Generated class for the SplashScreen page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-splash-screen',
  templateUrl: 'splash-screen.html',
  entryComponents: [DashboardPage]
})
export class SplashScreenPage {

  constructor(public navCtrl: NavController, public navParams: NavParams, public auth: AuthenticationProvider) {}

  ionViewDidLoad() {
  }

  ionViewWillEnter(){
    this.auth.asyncSetup()
      .then(
        () => {
          let self = this;
          window.setTimeout(function(){
            self.navCtrl.setRoot(DashboardPage);
          }, 2000);
        },
        (error) => {
          console.log("Couldn't reed from Storage.");
        }
      )
  }

}
