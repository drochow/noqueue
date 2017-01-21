import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { UsersProvider } from '../../providers/users-provider';
import { ValidatorProvider } from '../../providers/validator-provider';

/*
  Generated class for the EditPassword page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-edit-password',
  templateUrl: 'edit-password.html',
  providers: [ ValidatorProvider, UsersProvider ]
})
export class EditPasswordPage {

  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
  error = false;
  errorMessage = "";
  email = "";
  username = "";

  constructor(public navCtrl: NavController, public users: UsersProvider, public validator: ValidatorProvider) {}

  ionViewDidLoad() {
    this.resetError();

    this.users.getMe()
      .subscribe(
        (user) => {
            this.email = user.nutzerEmail;
            this.username = user.nutzerName;
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error ", jsonError);
          this.registerError(jsonError.message);
        }
      )
  }

  changePassword(){
    this.resetError();
    if(!this.validator.passwordsMatching(this.confirmPassword, this.newPassword)){
      this.registerError("New password and confirm password not matching");
    }
    if(!this.validator.password(this.newPassword)){
      this.registerError("New Password not valid");
    }
    if(this.validator.passwordsMatching(this.oldPassword, this.newPassword)){
      this.registerError("New password matches old password!")
    }
    if(this.error) return;

    this.users.changePassword({username: this.username, email: this.email, oldPassword: this.oldPassword, newPassword: this.newPassword})
      .subscribe(
        () => this.navCtrl.pop(),
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error ", jsonError);
          this.registerError(jsonError.message);
        }
      );
    //..
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }

  resetError(){
    this.error = false;
    this.errorMessage = "";
  }
}
