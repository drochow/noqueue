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

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider,
  public validator: ValidatorProvider) {
    this.newShop = navParams.get('newShop');
    if(!this.newShop){
      this.shopID = navParams.get('shopID');
      this.reloadData();
    }
  }

  ionViewDidLoad() {
  }

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
          }
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

  checkInput(){
    if(!this.validator.shopName(this.shop.name)){
      this.registerError("Shop name not valid");
    }
    if(!this.validator.phone(this.shop.phone)){
      this.registerError("Phone number not valid");
    }
    if(!this.validator.email(this.shop.email)){
      this.registerError("Email not valid");
    }
    if(!this.validator.openingHours(this.shop.openingHours)){
      this.registerError("Opening Hours not valid");
    }
    if(!this.validator.city(this.shop.address.city)){
      this.registerError("City not valid");
    }
    if(!this.validator.zip(this.shop.address.zip)){
      this.registerError("ZIP Code not valid");
    }
    if(!this.validator.streetNumber(this.shop.address.streetNr)){
      this.registerError("Street Number not valid");
    }
    if(!this.validator.street(this.shop.address.street)){
      this.registerError("Street not valid");
    }
  }

  save(){
    this.resetError();
    this.checkInput();
    if(this.error) return;

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
    if(this.error) return;

    this.shopsProvider.createShop(this.shop)
      .subscribe(
        (id) => {
          console.log("will push to service info page with new shop ID: ", id);
          this.navCtrl.push(ServiceInfoPage, {newShop: true, shopID: id, newService: true});
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while saving new shop: ", jsonError);
          this.registerError(jsonError.message);
        }
      )
  }

}
