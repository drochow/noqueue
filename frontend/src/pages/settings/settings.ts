import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { UserConfigurationProvider } from '../../providers/user-configuration-provider';
import { LoginPage } from '../../pages/login/login';
import { SignupPage } from '../../pages/signup/signup';
import { EditProfilePage } from '../../pages/edit-profile/edit-profile';
/*
  Generated class for the Settings page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-settings',
  templateUrl: 'settings.html',
  entryComponents: [LoginPage, SignupPage, EditProfilePage]
})
export class SettingsPage {

  theme: string;
  notificationSettings: string;
  isLoggedIn: boolean;

  constructor(public navCtrl: NavController, public auth: AuthenticationProvider, public userConfig: UserConfigurationProvider) {
    this.isLoggedIn = this.auth.isLoggedIn();
    console.log("constructor: ", this.auth.isLoggedIn());
  }

  ionViewDidLoad() {
    this.isLoggedIn = this.auth.isLoggedIn();
    console.log("constructor: ", this.auth.isLoggedIn());
  }

  themeSelection(event){
    this.userConfig.selectTheme(this.theme);
  }

  notificationSettingsSelection(event){
    this.userConfig.selectNotificationSettings(this.notificationSettings);
  }

  showEditProfilePage(){
    this.navCtrl.push(EditProfilePage);
  }

  showEditPasswordPage(){
    // this.navCtrl.push(EditPasswordPage);
  }

  logout(){
    this.auth.logout();
    this.navCtrl.popToRoot();
  }

  // @TODO
  deleteAccount(){
    this.auth.logout();
    this.navCtrl.popToRoot();
  }

  showLoginPage(){
    this.navCtrl.push(LoginPage);
  }

  showSignupPage(){
    this.navCtrl.push(SignupPage);
  }

  showReportProblemPage(){
    // this.navCtrl.push(ReportProblemPage);
  }

  showFAQPage(){
    // this.navCtrl.push(FAQPage);
  }

  showPrivacyPolicyPage(){
    // this.navCtrl.push(PrivacyPolicyPage);
  }

  showAboutNoQueuePage(){
    // this.navCtrl.push(AboutNoQueuePage);
  }

}
