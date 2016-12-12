import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import {ServicesPage} from "../services-page/services-page";
import {QueuePosition} from "../queue-position/queue-position";
import {MyService} from "../my-service/my-service";
import {Account} from "../account/account";
import {LoginPage} from "../login/login";

/*
  Generated class for the Main page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-main',
  templateUrl: 'main.html',
  entryComponents:[ ServicesPage, QueuePosition, MyService, Account ]
})
export class MainPage {

  tab1: any = {};
  tab2: any = {};
  tab3: any = {};
  tab4: any = {};

  constructor(public navCtrl: NavController) {
    this.tab1 = ServicesPage;
    this.tab2 = QueuePosition;
    this.tab3 = MyService;
    this.tab4 = Account;
  }

  ionViewDidLoad() {
  }

}
