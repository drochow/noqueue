import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { UsersProvider } from '../../providers/users-provider';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ValidatorProvider } from '../../providers/validator-provider';

/*
  Generated class for the EditProfile page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-edit-profile',
  templateUrl: 'edit-profile.html',
  providers: [ValidatorProvider, UsersProvider]
})
export class EditProfilePage {

// declare variables used by the HTML template (ViewModel):

  error: boolean = false;
  errorMessage: string = "";
  username: string;
  email: string;
  street: string;
  streetNr: string;
  zip: string;
  city: string;
  validationRules: any;
  isValid = {
    username: true,
    email: true,
    street: true,
    streetNr: true,
    zip: true,
    city: true,
    address: true
  };
  allFieldsValid: boolean = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, private users: UsersProvider, private auth: AuthenticationProvider,
  private validator: ValidatorProvider) {
    this.validationRules = {
      username: this.validator.rules.username,
      email: this.validator.rules.email,
      street: this.validator.rules.street,
      streetNr: this.validator.rules.streetNumber,
      zip: this.validator.rules.zip,
      city: this.validator.rules.city
    };
    this.fetchData();
  }

  ionViewWillEnter() : void{
    this.fetchData();
  }

// ViewModel logic (working with the data)

  checkUsername() : void{
    this.isValid.username = this.validator.username(this.username);
    this.checkAllFields();
  }

  checkEmail() : void{
    this.isValid.email = this.validator.email(this.email);
    this.checkAllFields();
  }

  checkStreet() : void{
    this.isValid.street = this.validator.street(this.street);
    this.checkAddress();
    this.checkAllFields();
  }

  checkStreetNr() : void{
    this.isValid.streetNr = this.validator.streetNumber(this.streetNr);
    this.checkAddress();
    this.checkAllFields();
  }

  checkZip() : void{
    this.isValid.zip = this.validator.zip(this.zip);
    console.log("Testing the validator - zip 12345a, 123456: " + this.validator.zip("12345a") + " " + this.validator.zip("123456"));
    this.checkAddress();
    this.checkAllFields();
  }

  checkCity() : void{
    this.isValid.city = this.validator.city(this.city);
    this.checkAddress();
    this.checkAllFields();
  }

  checkAddress() : void{
    var allEmpty = this.validator.allEmpty(this.street, this.streetNr, this.zip, this.city);
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

  checkInput() : void{
    this.checkUsername();
    this.checkEmail();
    this.checkStreet();
    this.checkStreetNr();
    this.checkZip();
    this.checkCity();
  }

  fetchData() : void{
    this.error = false;
    this.errorMessage = "";
    this.users.getMe()
      .subscribe(
        (me) => {
            this.username = me.nutzerName || "";
            this.email = me.nutzerEmail || "";
            this.street = me.adresse.straße || "";
            this.streetNr = me.adresse.hausNummer || "";
            this.zip = me.adresse.plz || "";
            this.city = me.adresse.stadt || "";
            this.checkInput();
        },
        (error) => {
          this.registerError("Couldn't get data from server. Please try again later.")
        }
      )
  }

  changeData() : void{
    this.error = false;
    this.errorMessage = "";

    this.checkInput();
    if(!this.allFieldsValid) return;

    let data = {
      username: this.username,
      email: this.email,
      street: this.street,
      streetNr: this.streetNr,
      zip: this.zip,
      city: this.city
    };
    this.users.changeProfileInfo(data)
      .subscribe(
        () => {
          console.log("Changed data");
          this.navCtrl.pop();
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          if(jsonError.code == 404){
            this.registerError("This address doesn't exist.")
          } else {
            this.registerError("Couldn't update the information. Please try again later.");
          }
        }
      )
  }

  registerError(message: string) : void{
    this.error = true;
    this.errorMessage = message;
  }

}
