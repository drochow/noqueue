import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';

/*
  Generated class for the QueuePosition page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-queue-position',
  templateUrl: 'queue-position.html'
})
export class QueuePosition {

  constructor(public navCtrl: NavController) {}

  ionViewDidLoad() {
    console.log('Hello QueuePosition Page');
  }

}
