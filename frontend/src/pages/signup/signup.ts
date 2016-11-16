import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { HttpService } from '../../providers/http-service';

/*
  Generated class for the Signup page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-signup',
  templateUrl: 'signup.html',
  providers: [HttpService]
})
export class SignUpPage {

  username: any;
  email: any;
  password: any;
  confirmedPassword: any;

  constructor(public navCtrl: NavController, public httpService: HttpService) {}

  ionViewDidLoad() {
    console.log('Hello Signup Page');
  }

  signUp(username: string, email: string, password: string, confirmedPassword: string){
    if(password === confirmedPassword){
      console.log("YES");
      this.httpService.addNewUser(username, password, email);
    }
    this.navCtrl.pop();
  }

}
