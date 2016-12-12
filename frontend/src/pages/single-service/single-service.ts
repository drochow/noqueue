import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { HttpService } from '../../providers/http-service';

/*
  Generated class for the SingleService page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-single-service',
  templateUrl: 'single-service.html'
})
export class SingleService {

  service: any;

  constructor(public navCtrl: NavController, public params: NavParams, public httpService: HttpService) {
    this.service = this.httpService.getServiceWithId(parseInt(this.params.get("id"))).subscribe(
      (service) => this.service = service);
  }

  ionViewDidLoad() {
    // this.service = this.httpService.getServiceWithId(parseInt(this.params.get("id")));
  }

}
