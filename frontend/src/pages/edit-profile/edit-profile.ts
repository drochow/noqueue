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
  templateUrl: 'edit-profile.html'
})
export class EditProfilePage {

  error = false;
  errorMessage = "";
  username = "";
  email = "";
  street = "";
  streetNr = "";
  zip = "";
  city = "";


  constructor(public navCtrl: NavController, private users: UsersProvider, private auth: AuthenticationProvider,
  private validator: ValidatorProvider) {
    this.fetchData();
  }

  ionViewWillEnter() {
    this.fetchData();
  }

  fetchData(){
    this.error = false;
    this.errorMessage = "";
    this.users.getMe()
      .subscribe(
        (me) => {
            this.username = me.nutzerName || "",
            this.email = me.nutzerEmail || "",
            this.street = me.adresse.strasse || "",
            this.streetNr = me.adresse.hausNummer || "",
            this.zip = me.adresse.plz || "",
            this.city = me.adresse.stadt || ""
        },
        (error) => {
          this.registerError(error.message || "Couldn't get data from server")
        }
      )
  }

  changeData(){
    this.error = false;
    this.errorMessage = "";

    if(!this.validator.username(this.username)){
      this.registerError("Username not valid");
    }
    if(this.email.length > 0 && !this.validator.email(this.email)){
      this.registerError("Email not valid");
    }
    if(this.street.length > 0 && !this.validator.street(this.street)){
      this.registerError("Street not valid");
    }
    if(this.zip.length > 0 && !this.validator.zip(this.zip)){
      this.registerError("Street Number not valid");
    }
    if(this.city.length > 0 && !this.validator.city(this.city)){
      this.registerError("Street Number not valid");
    }
    if(this.error) return;

    // @TODO - dont send empty properties?
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
        () => this.navCtrl.pop()
      )
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }

}
