import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';

/*
  Generated class for the ForgotPassword page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-forgot-password',
  templateUrl: 'forgot-password.html'
})
export class ForgotPassword {

  constructor(public navCtrl: NavController) {}

  ionViewDidLoad() {
    console.log('Hello ForgotPassword Page');
  }

  send(){
    this.navCtrl.pop();
  }

}
