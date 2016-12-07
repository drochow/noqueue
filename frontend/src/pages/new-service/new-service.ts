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

  constructor(public navCtrl: NavController, private servicesProvider: ServicesProvider) {}

  ionViewDidLoad() {
  }

  create(name: String, phone : String, openingHours : String, email : String, street : String, streetNumber : String, zip : String, city: String){
    this.servicesProvider.postNewService(
      {name, phone, openingHours, email, street, streetNumber, zip, city}
    ).then(
      () => {
        
      }
    )
  }
}
