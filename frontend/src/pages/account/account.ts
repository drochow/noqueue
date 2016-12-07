import { Component } from '@angular/core';
import { Tabs, NavController } from 'ionic-angular';
import { ModalController } from 'ionic-angular';
import { LoginPage } from '../login/login';
import { ProfileInfoPage } from '../profile-info/profile-info';
import { AuthenticationProvider } from '../../providers/authentication';
import { UsersProvider } from '../../providers/users';
import { MainPage } from '../main/main';

/*
  Generated class for the Account page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-account',
  templateUrl: 'account.html'
})
export class Account {

  loggedIn: boolean;

  constructor(public navCtrl: NavController, private auth: AuthenticationProvider, private modalCtrl: ModalController, private users: UsersProvider) {
    this.loggedIn = this.auth.isLoggedIn();
    // for testing purposes:
    this.loggedIn = true;
  }

  ionViewDidLoad() {
    console.log('Hello Account Page');
    // this.loggedIn = this.auth.isLoggedIn();
  }

  logOut(){
    this.auth.logOut();
    let t: Tabs = this.navCtrl.parent;
    t.select(0);
  }

  logIn(){
    let loginModal = this.modalCtrl.create(LoginPage);
    loginModal.present();
  }

  editInformation(){
    let infoModal = this.modalCtrl.create(ProfileInfoPage);
    infoModal.present();
  }

  deleteAccount(){
    this.users.deleteUser(this.auth.getUserId()).then(
      () => {
        this.auth.logOut()
      }
    )

  }



}
