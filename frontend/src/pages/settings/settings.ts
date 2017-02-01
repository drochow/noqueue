import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
// custom providers
import { AuthenticationProvider } from '../../providers/authentication-provider';
// custom pages
import { LoginPage } from '../../pages/login/login';
import { SignupPage } from '../../pages/signup/signup';
import { EditProfilePage } from '../../pages/edit-profile/edit-profile';
import { EditPasswordPage} from '../../pages/edit-password/edit-password';
import { ReportProblemPage } from '../../pages/report-problem/report-problem';
import { FAQPage } from '../../pages/f-a-q/f-a-q';
import { PrivacyPolicyPage } from '../../pages/privacy-policy/privacy-policy';
import { AboutNoQueuePage } from '../../pages/about-no-queue/about-no-queue';

/*
  Generated class for the Settings page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-settings',
  templateUrl: 'settings.html',
  entryComponents: [LoginPage, SignupPage, EditProfilePage, EditPasswordPage, ReportProblemPage, FAQPage, PrivacyPolicyPage, AboutNoQueuePage]
})
export class SettingsPage {

// declare variables used by the HTML template (ViewModel)

  isLoggedIn: boolean;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public auth: AuthenticationProvider) {
    this.isLoggedIn = this.auth.isLoggedIn();
  }

  ionViewDidLoad() : void{
    this.isLoggedIn = this.auth.isLoggedIn();
  }

// ViewController logic (reacting to events)

  showEditProfilePage() : void{
    this.navCtrl.push(EditProfilePage);
  }

  showEditPasswordPage() : void{
    this.navCtrl.push(EditPasswordPage);
  }

  logout() : void{
    this.auth.logout();
    this.navCtrl.popToRoot();
  }

  showLoginPage() : void{
    this.navCtrl.push(LoginPage);
  }

  showSignupPage() : void{
    this.navCtrl.push(SignupPage);
  }

  showReportProblemPage() : void{
    this.navCtrl.push(ReportProblemPage);
  }

  showFAQPage() : void{
    this.navCtrl.push(FAQPage);
  }

  showPrivacyPolicyPage() : void{
    this.navCtrl.push(PrivacyPolicyPage);
  }

  showAboutNoQueuePage() : void{
    this.navCtrl.push(AboutNoQueuePage);
  }

}
