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

  constructor(public navCtrl: NavController, public users: UsersProvider, public validator: ValidatorProvider) {}

  ionViewDidLoad() {
  }

  changePassword(){
    //..
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }
}
