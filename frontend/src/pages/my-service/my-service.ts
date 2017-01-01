import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { ModalController } from 'ionic-angular';
import {NewServicePage} from "../new-service/new-service";
import {ServicesProvider} from "../../providers/services-provider";

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

  services: any[];

  constructor(public navCtrl: NavController, public modalCtrl: ModalController, private servicesProvider: ServicesProvider) {}

  ionViewDidLoad() {
    this.fetchAllServices();
  }

  fetchAllServices(){
    // this.servicesProvider.getAllServices().then(
    //   (services) => {
    //     this.services = services;
    //   });
  }

  newService(){
    let modal = this.modalCtrl.create(NewServicePage);
    modal.present();
  }


}
