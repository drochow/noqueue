import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';

/*
  Generated class for the Account page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-account',
  templateUrl: 'account.html'
})
export class Account {

  constructor(public navCtrl: NavController) {}

  ionViewDidLoad() {
    console.log('Hello Account Page');
  }



}
