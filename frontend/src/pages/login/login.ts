import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ModalController } from 'ionic-angular';
import { AlertController } from 'ionic-angular';
import { SignupPage } from '../../pages/signup/signup';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { ToastController } from 'ionic-angular';


/*
  Generated class for the Login page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-login',
  templateUrl: 'login.html',
  providers: [ValidatorProvider, ConnectivityProvider],
  entryComponents: [SignupPage]
})
export class LoginPage {

// variables for data binding with the template

  username: string = "";
  password: string = "";
  error: boolean = false;
  validationRules: any;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public auth: AuthenticationProvider, private validator: ValidatorProvider,
  private modalCtrl: ModalController, private alertCtrl: AlertController, public connectivity: ConnectivityProvider,
  public toast: ToastController) {
    this.validationRules = {
      username: this.validator.rules.username,
      email: this.validator.rules.email,
      password: this.validator.rules.password
    }
  }

  ionViewDidLoad() : void{
  }

  ionViewWillEnter() : void {
    this.connectivity.checkNetworkConnection();
  }

// ViewController logic (reacting to events)

    login() : void{
    this.error = false;

    if(this.validator.empty(this.username, this.password)) return;

    this.auth.login(this.username, this.password)
      .then(
        () => this.navCtrl.pop(),
        (error) => {
          this.error = true;
          console.log("Error: ", error);
          let jsonError = JSON.parse(error._body);
          if(jsonError.code != 400){
            this.registerError("Couldn't log in. Please try again later.");
          } else {
            this.registerError("Wrong username or password.");
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

  registerError(message: string) : void{
    this.error = true;
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }

  showSignupPage() : void{
    this.navCtrl.push(SignupPage);
  }

}
