import { Component } from '@angular/core';
import {NavController, ModalController} from 'ionic-angular';
import {AuthenticationProvider} from "../../providers/authentication";
import {UsersProvider} from "../../providers/users";

/*
  Generated class for the ProfileInfo page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-profile-info',
  templateUrl: 'profile-info.html'
})
export class ProfileInfoPage {

  profile: {username?: string, email?: string, oldPassword?: string, newPassword?: string, confirmPassword?: string,
  street?: string, streetNr?: string, zip?: string, city?: string} = {};

  incorrectUsername = false;
  incorrectEmail = false;
  incorrectOldPassword = false;
  newPasswordMismatch = false;

  constructor(public navCtrl: NavController, private auth: AuthenticationProvider, private users: UsersProvider, private modalCtrl: ModalController) {}

  ionViewDidLoad() {
    this.users.getMe().then(
      (me) => {
        this.profile = {
          username: me.nutzerName || "",
          email: me.nutzerEmail || "",
          street: me.adresse.strasse || "",
          streetNr: me.adresse.hausNummer || "",
          zip: me.adresse.plz || "",
          city: me.adresse.stadt || ""
        }
      }
    )
  }

  changeData(){
    let data = { username: this.profile.username, email: this.profile.email,
    addresse: {
      stadt: this.profile.city,
      strasse: this.profile.street,
      hausNummer: this.profile.streetNr,
      plz: this.profile.zip
    }};
    this.users.patchUser(this.auth.getUserId(), data).then(
      () => {

      }
    )
  }

  changePassword(oldPassword: String, newPassword: String, confirmPassword: String){
    this.resetErrorInfo();
    if(newPassword !== confirmPassword){
      this.newPasswordMismatch = true;
      return;
    }
    this.users.patchUser(this.auth.getUserId(), newPassword).then(
      () => {

      }
    )
  }

  resetErrorInfo(){
    this.incorrectUsername = false;
    this.incorrectEmail = false;
    this.incorrectOldPassword = false;
    this.newPasswordMismatch = false;
  }


}
