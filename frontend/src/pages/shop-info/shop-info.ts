import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ServiceInfoPage } from '../service-info/service-info';

/*
  Generated class for the ShopInfo page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-shop-info',
  templateUrl: 'shop-info.html',
  providers: [ShopsProvider],
  entryComponents: [ ServiceInfoPage ]
})
export class ShopInfoPage {

// declare variables used by the HTML template (ViewModel)

  error = false;
  errorMessage = "";
  shop = {
    name: "",
    phone: "",
    email: "",
    openingHours: "",
    address: {
      city: "",
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
    zip: true,
    city: true,
    address: true
  };
  allFieldsValid = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider,
  public validator: ValidatorProvider) {
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

  ionViewDidLoad() {
  }

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


// ViewModel logic (working with the data)

  // call only if editing existing shop
  reloadData(){
    this.resetError();

    this.shopsProvider.getShop(this.shopID)
      .subscribe(
        (shop) => {
          console.log("Get shop: ", shop);
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
        (error) => this.registerError(error.message || "Couldn't retrieve data from server")
      );
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }

  resetError(){
    this.error = false;
    this.errorMessage = "";
  }


// ViewController logic (reacting to events)

  save(){
    this.resetError();
    this.checkInput();
    if(!this.allFieldsValid) return;

    this.shopsProvider.editShop(this.shopID, this.shop)
      .subscribe(
        () => {
          this.navCtrl.pop();
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while saving shop: ", jsonError);
          this.registerError(jsonError.message);
        }
      )
  }

  proceed(){
    this.resetError();
    this.checkInput();
    if(!this.allFieldsValid) return;

    this.shopsProvider.createShop(this.shop)
      .subscribe(
        (shop) => {
          console.log("will push to service info page with new shop ID: ", shop.id);
          this.navCtrl.push(ServiceInfoPage, {newShop: true, shopID: shop.id, newService: true});
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while saving new shop: ", jsonError);
          this.registerError(jsonError.message);
        }
      )
  }

}
