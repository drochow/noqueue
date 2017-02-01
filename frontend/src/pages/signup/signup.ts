import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { ToastController } from 'ionic-angular';
// custom providers
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';

/*
  Generated class for the Signup page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-signup',
  templateUrl: 'signup.html',
  providers: [ValidatorProvider, ConnectivityProvider]
})
export class SignupPage {

// declare variables used by the HTML template (ViewModel)

  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  error: boolean = false;
  validationRules: any;
  isValid = {
    username: true,
    email: true,
    password: true,
    confirmPassword: true,
    passwordsMatching: true
  };
  allFieldsValid: boolean = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public auth: AuthenticationProvider, private validator: ValidatorProvider,
  public connectivity: ConnectivityProvider, public toast : ToastController) {
    // later - read these from the validator:
    this.validationRules = {
      username: this.validator.rules.username,
      email: this.validator.rules.email,
      password: this.validator.rules.password,
      passwordsMatching: this.validator.rules.passwordMatching
    }
  }

  ionViewDidLoad() : void{
  }

  ionViewWillEnter() : void {
    this.connectivity.checkNetworkConnection();
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

  checkPassword() : void{
    this.isValid.password = this.validator.password(this.password);
    this.checkPasswordsMatching();
  }

  checkConfirmPassword() : void{
    this.isValid.confirmPassword = this.validator.password(this.confirmPassword);
    this.checkPasswordsMatching();
  }

  checkPasswordsMatching() : void{
    this.isValid.passwordsMatching = this.validator.passwordsMatching(this.password, this.confirmPassword);
    this.checkAllFields();
  }

  checkAllFields() : void{
    var valid = true;
    if(this.validator.empty(this.username, this.email, this.password, this.confirmPassword)){
      valid = false;
    } else {
      for(let attr in this.isValid){
        if(this.isValid[attr] == false) valid = false;
      }
    }
    this.allFieldsValid = valid;
  }

  checkInput() : void{
    this.checkUsername();
    this.checkEmail();
    this.checkPassword();
    this.checkConfirmPassword();
    this.checkPasswordsMatching();
  }

  registerError(message: string) : void{
    this.error = true;
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }

// ViewController logic (reacting to events)

  signup() : void{
    this.error = false;

    this.checkInput();
    if(!this.allFieldsValid) return;

    this.auth.signup(this.username, this.email, this.password)
      .then(
        () => {
          this.navCtrl.popToRoot()
        },
            (error) => {
              this.registerError("Couldn't sign up.");
            }
      );
  }

}
