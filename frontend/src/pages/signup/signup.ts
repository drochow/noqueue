import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { HttpService } from '../../providers/http-service';
import { AuthenticationProvider } from '../../providers/authentication';

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
  correctData: boolean;

  constructor(public navCtrl: NavController, public httpService: HttpService, private auth: AuthenticationProvider) {}

  ionViewDidLoad() {
    console.log('Hello Signup Page');
  }

  signUp(username: string, email: string, password: string, confirmedPassword: string){
    if(password === confirmedPassword){
      console.log("Passwords matching");
      this.auth.signUp(username, password, email).then(
        () => {
          console.log("here");
          this.correctData = true;
          this.navCtrl.pop();
        }
      )
    } else {
      this.correctData = false
    }
  }

}
