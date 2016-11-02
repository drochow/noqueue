import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';

/*
  Generated class for the MyService page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-service',
  templateUrl: 'my-service.html'
})
export class MyService {

  constructor(public navCtrl: NavController) {}

  ionViewDidLoad() {
    console.log('Hello MyService Page');
  }

}
