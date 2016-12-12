import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { ServicesProvider } from '../../providers/services-provider';
/*
  Generated class for the NewService page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-new-service',
  templateUrl: 'new-service.html'
})
export class NewServicePage {

  service: {
    name?: string,
    phone?: string,
    openingHours?: string,
    email?: string,
    street?: string,
    streetNumber?: string,
    zip?: string,
    city?: string} = {};

  constructor(public navCtrl: NavController, private servicesProvider: ServicesProvider) {}

  ionViewDidLoad() {
  }

  create(){
    this.servicesProvider.postNewService(
      {name: this.service.name,
        phone: this.service.phone,
        openinigHours: this.service.openingHours,
        email: this.service.email,
        street: this.service.street,
        streetNumber: this.service.streetNumber,
        zip: this.service.zip,
        city: this.service.city}
    ).then(
      () => {
        
      }
    )
  }
}
