import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ValidatorProvider } from '../../providers/validator-provider';

/*
  Generated class for the Signup page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-signup',
  templateUrl: 'signup.html',
  providers: [AuthenticationProvider, ValidatorProvider]
})
export class SignupPage {

  // variables for data binding with the template
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  error = false;
  errorMessage = "";

  constructor(public navCtrl: NavController, public auth: AuthenticationProvider, private validator: ValidatorProvider) {}

  ionViewDidLoad() {
  }

  signup(){
    this.error = false;
    this.errorMessage = "";

    if(!this.validator.username(this.username)){
      this.registerError("Username not valid");
    }
    if(!this.validator.email(this.email)){
      this.registerError("Email not valid");
    }
    if(!this.validator.password(this.password)){
      this.registerError("Password not valid");
    }
    if(!this.validator.passwordMatching(this.password, this.confirmPassword)){
      this.registerError("Password not matching");
    }
    if(this.error) return;

    this.auth.signup(this.username, this.email, this.password)
      .then(
        () => console.log("Signed up"), // this.navCtrl.push(DashboardPage)
        (error) => {
          this.error = true;
          this.errorMessage = error.message || "Something went wrong";
        }
      )
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }

}
