import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
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
    city?: string
  } = {};

  editingService = false;

  constructor(public navCtrl: NavController, private servicesProvider: ServicesProvider, public params: NavParams) {
    let id = this.params.get('serviceId');
    if (id !== undefined){
      this.editingService = true;
      this.fetchService(id)
    }
  }

  ionViewDidLoad() {
  }

  fetchService(id){
    this.servicesProvider.getService(id).then(
      (result) => {
        this.service = {
          name: result.name,
          phone: result.tel,
          email: result.kontaktEmail,
          openingHours: result.oeffnungszeiten,
          street: result.adresse.strasse,
          streetNumber: result.adresse.hausNummer,
          zip: result.adresse.plz,
          city: result.adresse.stadt
        }
      }
    )
  }

  mapService() : any {
    return {name: this.service.name,
      tel: this.service.phone,
      oeffnungszeiten: this.service.openingHours,
      kontaktEmail: this.service.email,
      adresse : {
        strasse: this.service.street,
        hausNummer: this.service.streetNumber,
        plz: this.service.zip,
        stadt: this.service.city
      }
     }
  }

  create(){
    this.servicesProvider.postNewService(
      this.mapService()
    ).then(
      () => {
        this.navCtrl.pop();
      }
    )
  }

  save(){
    this.servicesProvider.putService(
      this.params.get('serviceId'),
      this.mapService()
    ).then(
      () => {
        this.navCtrl.pop();
      }
    )
  }
}
