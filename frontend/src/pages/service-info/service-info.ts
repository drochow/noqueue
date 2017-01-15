import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { AlertController } from 'ionic-angular';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ServicesProvider } from '../../providers/services-provider';
import { CoworkersPage } from '../coworkers/coworkers';


/*
  Generated class for the ServiceInfo page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-service-info',
  templateUrl: 'service-info.html',
  providers: [ServicesProvider],
  entryComponents: [CoworkersPage]
})
export class ServiceInfoPage {

  error = false;
  errorMessage = "";
  newService = false;
  serviceID: number;
  newShop = false;
  shopID: number;
  service = {
    type: "",
    duration: 0,
    description: ""
  };
  types = [];
  customType = false;
  selectedType = "";

  constructor(public navCtrl: NavController, public navParams: NavParams, public validator: ValidatorProvider, public servicesProvider: ServicesProvider,
  public alertCtrl: AlertController) {
    this.newShop = navParams.get('newShop');
    this.newService = navParams.get('newService');
    this.shopID = navParams.get('shopID');

    if(!this.newService){
     this.serviceID = navParams.get('serviceID');
      this.reloadData();
    }
  }

  ionViewDidLoad() {
  }

  ionViewWillEnter(){
    this.servicesProvider.getAllServiceTypes()
      .subscribe(
        (data) => this.types = data
      )
  }

  resetError(){
    this.error = false;
    this.errorMessage = "";
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }

  // only if editing existing service
  reloadData(){
    this.servicesProvider.getService(this.serviceID, this.shopID)
      .subscribe(
        (service) => {
          this.service = {
            duration: service.dauer,
            type: service.typ,
            description: service.kommentar
          }
        },
        (error) => this.registerError(error.message || "Couldn't retrieve service from server")
      );
  }

  checkInput(){
    if(!this.validator.serviceDescription(this.service.description)){
      this.registerError("Description not valid");
    }
    if(!this.validator.serviceType(this.service.type)){
      this.registerError("Service type not valid");
    }
  }

  save(){
    this.service.type = this.selectedType;

    this.resetError();
    this.checkInput();
    if(this.error) return;


    this.servicesProvider.editService(this.shopID, this.serviceID, this.service)
      .subscribe(
        () => this.navCtrl.pop(),
        (error) => this.registerError(error.message || "Error while saving service")
      );
  }

  proceed(){
    this.service.type = this.selectedType;

    this.resetError();
    this.checkInput();
    if(this.error) return;

    this.servicesProvider.createService(this.shopID, this.service)
      .subscribe(
        (id) => {
          console.log("Creating service with ID: ", id);
          if(this.newShop){
           this.navCtrl.push(CoworkersPage, {newShop: true, shopID: this.shopID});
          } else {
           this.navCtrl.pop();
          }
        },
        (error) => this.registerError(error.message || "Couldn't save the service")
      );
  }

  addCustomType(){
    let confirm = this.alertCtrl.create({
      title: 'Add custom service type',
      message: 'Please type in your custom type:',
      inputs: [
        {
          name: 'customtype',
          placeholder: 'Custom type'
        }
      ],
      buttons: [
        {
          text: 'Cancel',
          handler: data => {}
        },
        {
          text: 'OK',
          handler: data => {
            this.types.push(data.customtype);
          }
        }
      ]
    });
    confirm.present();
  }

}
