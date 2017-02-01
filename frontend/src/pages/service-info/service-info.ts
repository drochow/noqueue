import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { AlertController } from 'ionic-angular';
import { ToastController } from 'ionic-angular';
// custom providers
import { ValidatorProvider } from '../../providers/validator-provider';
import { ServicesProvider } from '../../providers/services-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
// custom pages
import { CoworkersPage } from '../coworkers/coworkers';



/*
  Generated class for the ServiceInfo page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-service-info',
  templateUrl: 'service-info.html',
  providers: [ServicesProvider, ConnectivityProvider],
  entryComponents: [CoworkersPage]
})
export class ServiceInfoPage {

// declare variables used by the HTML template (ViewModel)

  error: boolean = false;
  newService: boolean  = false;
  serviceID: number;
  newShop: boolean = false;
  shopID: number;
  service = {
    type:  "",
    duration: 0,
    description: ""
  };
  types: any = [];
  customType: boolean = false;
  selectedType: string  = "";
  validationRules: any;
  isValid = {
    description: true,
    duration:  true,
    type: true
  };
  allFieldsValid: boolean = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public validator: ValidatorProvider, public servicesProvider: ServicesProvider,
  public alertCtrl: AlertController, public connectivity: ConnectivityProvider, public toast : ToastController) {
    this.validationRules = {
      description: this.validator.rules.serviceDescription,
      duration: this.validator.rules.duration,
      type: this.validator.rules.serviceType
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
    this.connectivity.checkNetworkConnection();
    this.servicesProvider.getAllServiceTypes()
      .subscribe(
        (data) => {
          this.types = [];
          var self = this;
          data.forEach(function(type){
            self.types.push(type.name);
          });
        },
        (error) => this.registerError("Couldn't fetch data from server.")
      )
  }

// ViewModel logic (working with the data)

  resetError() : void{
    this.error = false;
  }

  registerError(message: string) : void{
    this.error = true;
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }


  checkDescription(){
    this.isValid.description = this.validator.serviceDescription(this.service.description);
    this.checkAllFields();
  }

  checkType(){
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

    this.createService();
  }

  editService() : void{
    this.service.type = this.selectedType;
    this.servicesProvider.editService(this.shopID, this.serviceID, this.service)
      .subscribe(
        () => this.navCtrl.pop(),
        (error) => {
          this.registerError("Couldn't edit the service.")
        }
      );
  }

  createService() : void{
    this.service.type = this.selectedType;
    this.servicesProvider.createService(this.shopID, this.service)
      .subscribe(
        (id) => {
          if(this.newShop){
            this.navCtrl.push(CoworkersPage, {newShop: true, shopID: this.shopID});
          } else {
            this.navCtrl.pop();
          }
        },
        (error) => {
          this.registerError("Couldn't save the service.")
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
          handler: data => {
          }
        },
        {
          text: 'OK',
          handler: data => {
            this.types.push(data.customtype);
            this.selectedType = data.customtype;
          }
        }
      ]
    });
    confirm.present();
  }

}
