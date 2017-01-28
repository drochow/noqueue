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
  username: string = "";
  password: string = "";
  error: boolean = false;
  errorMessage: string = "";

  constructor(public navCtrl: NavController, public auth: AuthenticationProvider, private validator: ValidatorProvider,
  private modalCtrl: ModalController, private alertCtrl: AlertController) {}

  ionViewDidLoad() : void{
  }

  login() : void{
    this.error = false;
    this.errorMessage = "";

    if(this.validator.empty(this.username, this.password)) return;

    this.auth.login(this.username, this.password)
      .then(
        () => this.navCtrl.pop(),
        (error) => {
          this.error = true;
          console.log("Error: ", error);
          let jsonError = JSON.parse(error._body);
          if(jsonError.code != 400){
            this.errorMessage = "Couldn't log in. Please try again later."
          } else {
            this.errorMessage = "Wrong username or password.";
          }
        }
      )
  }

  skip() : void{
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

  showSignupPage() : void{
    this.navCtrl.push(SignupPage);
  }

}
