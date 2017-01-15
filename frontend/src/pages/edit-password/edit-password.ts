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
  providers: [ ValidatorProvider ]
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
    this.users.getMe()
      .subscribe(
        (user) => {
            this.email = user.nutzerEmail;
            this.username = user.nutzerName;
        },
        (error) => {
          this.registerError(error || "Couldnt get user from server");
        }
      )
  }

  changePassword(){
    if(!this.validator.passwordMatching(this.confirmPassword, this.newPassword)){
      this.registerError("Passwords not matching");
    }
    if(!this.validator.password(this.newPassword)){
      this.registerError("New Password not valid");
    }
    if(this.error) return;

    this.users.changePassword({username: this.username, email: this.email, password: this.newPassword})
      .subscribe(
        () => this.navCtrl.pop(),
        (error) => this.registerError(error || "Couldn't save password")
      );
    //..
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }
}
