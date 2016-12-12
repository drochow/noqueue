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

  incorrectUsername = false;
  incorrectEmail = false;
  incorrectOldPassword = false;
  newPasswordMismatch = false;

  constructor(public navCtrl: NavController, private auth: AuthenticationProvider, private users: UsersProvider, private modalCtrl: ModalController) {}

  ionViewDidLoad() {
  }

  changeData(username: String, email: String){
    let user = { username: username, email: email };
    this.users.patchUser(this.auth.getUserId(), user).then(
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
