import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ModalController } from 'ionic-angular';
import { AlertController } from 'ionic-angular';
import { SignupPage } from '../../pages/signup/signup';

/*
  Generated class for the Login page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-login',
  templateUrl: 'login.html',
  providers: [ValidatorProvider],
  entryComponents: [SignupPage]
})
export class LoginPage {

  // variables for data binding with the template
  username: string;
  password: string;
  error = false;
  errorMessage = "";

  constructor(public navCtrl: NavController, public auth: AuthenticationProvider, private validator: ValidatorProvider,
  private modalCtrl: ModalController, private alertCtrl: AlertController) {}

  ionViewDidLoad() {
  }

  login(){
    this.error = false;
    this.errorMessage = "";

    if(this.validator.empty(this.username, this.password)){
      this.error = true;
      this.errorMessage = "Please fill in all required values.";
      return;
    }

    this.auth.login(this.username, this.password)
      .then(
        () => this.navCtrl.pop(),
        (error) => {
          this.error = true;
          this.errorMessage = error.message || "Wrong data";
        }
      )
  }

  skip(){
    let confirm = this.alertCtrl.create({
      title: 'Skip Log In?',
      message: 'Users that are not logged in can not reserve a place in a queue.',
      buttons: [
        {
          text: 'Cancel',
          handler: () => {}
        },
        {
          text: 'OK',
          handler: () => {
            this.navCtrl.pop();
          }
        }
      ]
    });
    confirm.present();
  }

  showSignupPage(){
    this.navCtrl.push(SignupPage);
  }

}
