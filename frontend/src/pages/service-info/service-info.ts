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

  error: boolean = false;
  errorMessage: string = "";
  newService: boolean  = false;
  serviceID: number;
  newShop: boolean = false;
  shopID: number;
  service = {
    type: "",
    duration: 0,
    description: ""
  };
  types = [];
  customType: boolean = false;
  selectedType: string  = "";

  constructor(public navCtrl: NavController, public navParams: NavParams, public validator: ValidatorProvider, public servicesProvider: ServicesProvider,
  public alertCtrl: AlertController) {
    this.newShop = navParams.get('newShop');
    this.newService = navParams.get('newService');
    this.shopID = navParams.get('shopID');

    if(!this.newService){
      this.serviceID = navParams.get('serviceID');
      let navService = navParams.get('service');
      this.service = {
        type: navService.name,
        duration: navService.dauer,
        description: navService.kommentar
      };
      this.reloadData();
    }
  }

  ionViewDidLoad() : void{
  }

  ionViewWillEnter() : void{
    this.servicesProvider.getAllServiceTypes()
      .subscribe(
        (data) => {
          console.log("Get all service types: ", data);
          this.types = [];
          var self = this;
          data.forEach(function(type){
            self.types.push(type.name);
          });
        }
      )
  }

  resetError() : void{
    this.error = false;
    this.errorMessage = "";
  }

  registerError(message: string) : void{
    this.error = true;
    this.errorMessage = message;
  }

  // only if editing existing service
  reloadData() : void{
     this.servicesProvider.getService(this.serviceID, this.shopID)
       .subscribe(
         (service) => {
           this.service = {
             duration: service.dauer,
             type: service.name,
             description: service.kommentar
           }
         },
         (error) => this.registerError(error.message || "Couldn't retrieve service from server")
       );
  }

  checkInput() : void{
    if(!this.validator.serviceDescription(this.service.description)){
      this.registerError("Description not valid");
    }
    if(!this.validator.serviceType(this.service.type)){
      this.registerError("Service type not valid");
    }
  }

  save() : void{
    this.service.type = this.selectedType;

    this.resetError();
    this.checkInput();
    if(this.error) return;
    console.log("trying to edit service");

    if(this.newService){
      this.createService();
    } else {
      this.editService();
    }
  }

  proceed() : void{
    this.service.type = this.selectedType;

    this.resetError();
    this.checkInput();
    if(this.error) return;

    console.log("trying to save service");

    this.createService();
  }

  editService() : void{
    this.servicesProvider.editService(this.shopID, this.serviceID, this.service)
      .subscribe(
        () => this.navCtrl.pop(),
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while editing service: ", jsonError);
          this.registerError(jsonError.message);
        }
      );
  }

  createService() : void{
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
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while creating new service: ", jsonError);
          this.registerError(jsonError.message);
        }
      );
  }

  addCustomType() : void{
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
