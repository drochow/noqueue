import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { ModalController } from 'ionic-angular';
// import {Data} from "../../providers/data";
import { AlertController } from 'ionic-angular';
import { MainPage } from '../../pages/main/main';
import { SignUpPage } from '../../pages/signup/signup';
import {ForgotPassword} from "../forgot-password/forgot-password";
import { HttpService } from '../../providers/http-service';
import { AuthenticationProvider } from '../../providers/authentication';

/*
  Generated class for the Login page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-login',
  templateUrl: 'login.html',
  providers: [HttpService],
  entryComponents:[ MainPage, ForgotPassword, SignUpPage ]
})
export class LoginPage{

  users: any;
  username: any;
  password: any;
  token: any;


  constructor(public navCtrl: NavController, public modalCtrl: ModalController,  public alertCtrl: AlertController, public httpService : HttpService, private auth: AuthenticationProvider) {
  }

  ionViewDidLoad() {
    this.fetchAllUsers();
  }

  private fetchAllUsers(){
    this.httpService.getAllUsers().subscribe(
      (users) => this.users = users);
  }

  signUp(){
    this.navCtrl.push(SignUpPage);
  }

  logIn(username: string, password: string){
    this.auth.signIn(username, password).then(
      () => {
        console.log(this.auth.getToken());
        if(this.auth.isLoggedIn()){
          this.navCtrl.push(MainPage);
        }
      }
    );
  }

  forgotPassword(){
    let modal = this.modalCtrl.create(ForgotPassword);
    modal.present();
  }

  skip(){
    // this.httpService.testSignIn();
    let confirm = this.alertCtrl.create({
      title: 'Skip Log In?',
      message: 'Users that are not logged in can not reserve a place in a queue.',
      buttons: [
        {
          text: 'Cancel',
          handler: () => {

          }
        },
        {
          text: 'OK',
          handler: () => {
              this.navCtrl.push(MainPage);
          }
        }
      ]
    });
    confirm.present();
  }
}
