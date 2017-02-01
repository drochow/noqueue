import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ToastController } from 'ionic-angular';
// custom providers
import { ShopsProvider } from '../../providers/shops-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
// custom pages
import { ServiceInfoPage } from '../service-info/service-info';


/*
  Generated class for the ShopInfo page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-shop-info',
  templateUrl: 'shop-info.html',
  providers: [ShopsProvider, ConnectivityProvider],
  entryComponents: [ ServiceInfoPage ]
})
export class ShopInfoPage {

// declare variables used by the HTML template (ViewModel)

  error: boolean = false;
  shop = {
    name: "",
    phone:  "",
    email: "",
    openingHours: "",
    address: {
      city:  "",
      zip: "",
      streetNr: "",
      street: ""
    }
  };
  newShop: boolean;
  // when editing an existing shop:
  shopID: number;
  validationRules: any;
  isValid = {
    shopName: true,
    email: true,
    phone: true,
    openingHours: true,
    street: true,
    streetNr: true,
    zip:  true,
    city: true,
    address: true
  };
  allFieldsValid: boolean = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider,
  public validator: ValidatorProvider, public connectivity: ConnectivityProvider, public toast : ToastController) {
    this.validationRules = {
      shopName: this.validator.rules.shopName,
      email: this.validator.rules.email,
      phone: this.validator.rules.phone,
      openingHours: this.validator.rules.openingHours,
      street: this.validator.rules.street,
      streetNr: this.validator.rules.streetNumber,
      zip: this.validator.rules.zip,
      city: this.validator.rules.city
    };

    this.newShop = navParams.get('newShop');
    if(!this.newShop){
      this.shopID = navParams.get('shopID');
      this.reloadData();
    }
  }

  ionViewDidLoad() : void{
  }

  ionViewWillEnter(): void{
    this.connectivity.checkNetworkConnection();
  }

  // ViewModel logic - working with the data

  checkShopName() : void{
    this.isValid.shopName = this.validator.shopName(this.shop.name);
    this.checkAllFields();
  }

  checkEmail() : void{
    this.isValid.email = this.validator.email(this.shop.email);
    this.checkAllFields();
  }

  checkPhone() : void{
    this.isValid.phone = this.validator.phone(this.shop.phone);
    this.checkAllFields();
  }

  checkOpeningHours() : void{
    this.isValid.openingHours = this.validator.openingHours(this.shop.openingHours);
    this.checkAllFields();
  }

  checkStreet() : void{
    this.isValid.street = this.validator.street(this.shop.address.street);
    this.checkAddress();
    this.checkAllFields();
  }

  checkStreetNr() : void{
    this.isValid.streetNr = this.validator.streetNumber(this.shop.address.streetNr);
    this.checkAddress();
    this.checkAllFields();
  }

  checkZip() : void{
    this.isValid.zip = this.validator.zip(this.shop.address.zip);
    this.checkAddress();
    this.checkAllFields();
  }

  checkCity() : void{
    this.isValid.city = this.validator.city(this.shop.address.city);
    this.checkAddress();
    this.checkAllFields();
  }

  checkAddress() : void{
    var allEmpty = this.validator.allEmpty(this.shop.address.street, this.shop.address.streetNr, this.shop.address.zip, this.shop.address.city);
    if(allEmpty){
      this.isValid.address = true;
      this.isValid.street = true;
      this.isValid.streetNr = true;
      this.isValid.zip = true;
      this.isValid.city = true;
      return;
    }
    this.isValid.address = this.isValid.street && this.isValid.streetNr && this.isValid.zip && this.isValid.city;
  }

  checkAllFields() : void{
    var valid = true;
    for(let attr in this.isValid){
      if(this.isValid[attr] == false) valid = false;
    }
    this.allFieldsValid = valid;
  }

  checkInput(): void {
    this.checkShopName();
    this.checkEmail();
    this.checkPhone();
    this.checkOpeningHours();
    this.checkStreet();
    this.checkStreetNr();
    this.checkZip();
    this.checkCity();
  }

  reloadData() : void{
    this.resetError();

    this.shopsProvider.getShop(this.shopID)
      .subscribe(
        (shop) => {
          this.shop = {
            name: shop.name,
            phone: shop.tel,
            email: shop.kontaktEmail,
            openingHours: shop.oeffnungszeiten,
            address: {
              city: shop.adresse.stadt,
              zip: shop.adresse.plz,
              streetNr: shop.adresse.hausNummer,
              street: shop.adresse.strasse
            }
          };
          this.checkInput();
        },
        (error) => this.registerError("Couldn't fetch data from server.")
      );
  }

  registerError(message: string) : void{
    this.error = true;
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }

  resetError() : void{
    this.error = false;
  }


// ViewController logic (reacting to events)

  save() : void{
    this.resetError();
    this.checkInput();
    if(!this.allFieldsValid) return;

    this.shopsProvider.editShop(this.shopID, this.shop)
      .subscribe(
        () => {
          this.navCtrl.pop();
        },
        (error) => {
          this.registerError("Couldn't edit this shop.")
        }
      )
  }

  proceed() : void{
    this.resetError();
    this.checkInput();
    if(!this.allFieldsValid) return;

    this.shopsProvider.createShop(this.shop)
      .subscribe(
        (shop) => {
          this.navCtrl.push(ServiceInfoPage, {newShop: true, shopID: shop.id, newService: true});
        },
        (error) => {
          this.registerError("Couldn't create a new shop.")
        }
      )
  }

}
