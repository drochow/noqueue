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

// declare variables used by the HTML template (ViewModel)

  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
  error: boolean = false;
  errorMessage: string = "";
  email: string = "";
  username: string = "";
  validationRules: any;
  isValid = {
    oldPassword: boolean = true,
    newPassword: boolean = true,
    confirmPassword: boolean = true,
    passwordsMatching: boolean = true,
    differentPasswords: boolean = true
  };
  allFieldsValid: boolean = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public users: UsersProvider, public validator: ValidatorProvider) {
    this.validationRules = {
      emptyPassword: "Please fill in this field.",
      newPassword: "Must be at least 8 characters.",
      samePassword: "New password equals old password.",
      passwordsMatching: "Passwords have to match."
    }
  }

  ionViewDidLoad() : void{
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

  ionViewWillEnter() : void{
  }

// ViewModel logic (working with the data)

  checkOldPassword() : void{
    this.isValid.oldPassword = !this.validator.empty(this.oldPassword);
    this.checkDifferentPasswords();
    this.checkAllFields();
  }

  checkNewPassword() : void{
    this.isValid.newPassword = this.validator.password(this.newPassword);
    this.checkDifferentPasswords();
    this.checkPasswordsMatching();
  }

  checkConfirmPassword() : void{
    this.isValid.confirmPassword = this.validator.password(this.confirmPassword);
    this.checkPasswordsMatching();
  }

  checkPasswordsMatching() : void{
    this.isValid.passwordsMatching = this.validator.passwordsMatching(this.newPassword, this.confirmPassword);
    this.checkAllFields();
  }

  checkDifferentPasswords() : void{
    this.isValid.differentPasswords = !this.validator.passwordsMatching(this.oldPassword, this.newPassword);
  }

  checkAllFields() : void{
    var valid = true;
    if(this.validator.empty(this.oldPassword, this.newPassword, this.confirmPassword)){
      valid = false;
    } else {
      for(let attr in this.isValid){
        if(this.isValid[attr] == false) valid = false;
      }
    }
    this.allFieldsValid = valid;
  }

  checkInput() : void{
    this.checkOldPassword();
    this.checkNewPassword();
    this.checkConfirmPassword();
  }

  changePassword() : void{
    this.resetError();
    this.checkInput();
    if(!this.allFieldsValid) return;

    this.users.changePassword({username: this.username, email: this.email, oldPassword: this.oldPassword, newPassword: this.newPassword})
      .subscribe(
        () => this.navCtrl.pop(),
        (error) => {
          let jsonError = JSON.parse(error._body);
          if(jsonError.code == 500){
            this.registerError("False old password.");
          } else {
            this.registerError("Couldn't change the server. Please try again later.")
          }
        }
      );
    //..
  }

  registerError(message: string) : void{
    this.error = true;
    this.errorMessage = message;
  }

  resetError() : void{
    this.error = false;
    this.errorMessage = "";
  }
}
