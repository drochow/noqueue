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

// declare variables used by the HTML template (ViewModel)

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
  validationRules: any;
  isValid = {
    description: true,
    duration: true,
    type: true
  };
  allFieldsValid = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public validator: ValidatorProvider, public servicesProvider: ServicesProvider,
  public alertCtrl: AlertController) {
    this.validationRules = {
      description: "Must be 0-250 characters.",
      duration: "",
      type: "Must be 2-40 characters."
    };
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
      this.selectedType = this.service.type;
      this.checkInput();
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

// ViewModel logic (working with the data)

  resetError() : void{
    this.error = false;
    this.errorMessage = "";
  }

  registerError(message: string) : void{
    this.error = true;
    this.errorMessage = message;
  }

  // only if editing existing service
  // reloadData() : void{
  //    this.servicesProvider.getService(this.serviceID, this.shopID)
  //      .subscribe(
  //        (service) => {
  //          this.service = {
  //            duration: service.dauer,
  //            type: service.name,
  //            description: service.kommentar
  //          }
  //        },
  //        (error) => this.registerError(error.message || "Couldn't retrieve service from server")
  //      );
  // }

  checkDescription(){
    this.isValid.description = this.validator.serviceDescription(this.service.description);
    this.checkAllFields();
  }

  checkType(){
    console.log("Selected type: ", this.selectedType);
    this.isValid.type = this.validator.serviceType(this.selectedType);
    this.checkAllFields();
  }

  checkAllFields(){
    var valid = true;
    for(let attr in this.isValid){
      if(this.isValid[attr] == false) valid = false;
    }
    this.allFieldsValid = valid;
  }

  checkInput() : void{
    this.checkType();
    this.checkDescription();
  }

// ViewController logic (reacting to events)

  save() : void{
    this.service.type = this.selectedType;

    this.resetError();
    this.checkInput();
    if(!this.allFieldsValid) return;
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
    if(!this.allFieldsValid) return;

    console.log("trying to save service");

    this.createService();
  }

  editService() : void{
    this.service.type = this.selectedType;
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
    this.service.type = this.selectedType;
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
