import { Component } from '@angular/core';
import { NavController, ModalController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ShopsProvider } from '../../providers/shops-provider';
import { QueuesProvider } from '../../providers/queues-provider';
import { LoginPage } from '../login/login';
import { SignupPage } from '../signup/signup';
import { SettingsPage } from '../settings/settings';
import { ShopsPage } from '../shops/shops';

/*
  Generated class for the Dashboard page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-dashboard',
  templateUrl: 'dashboard.html',
  providers: [ShopsProvider, QueuesProvider],
  entryComponents: [LoginPage, SignupPage, SettingsPage, ShopsPage]
})
export class DashboardPage {

  // variables for data-binding with the template
  isLoggedIn = false;
  myQueuePosition = {};
  isInQueue = false;
  shopsNearby = [];
  hasShopsNearby = false;
  myShops = [];
  hasShops = false;
  myQueues = [];
  hasQueues = false;

  constructor(public navCtrl: NavController, private loadingCtrl: LoadingController, public auth: AuthenticationProvider, private shops: ShopsProvider, private queues: QueuesProvider) {
  }

  ionViewWillEnter() {
    let loading = this.loadingCtrl.create({
      content: 'Fetching data ...'
    });

    loading.present();

    this.auth.asyncSetup().then(
      () => {
        this.reloadData();
        loading.dismiss();
      },
      (error) => {
        console.log(error);
      }
    );
    this.reloadData();
  }

  refresh(refresher){
    this.reloadData();

    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

  resetData(){
    this.isLoggedIn = false;
    this.myQueuePosition = {};
    this.isInQueue = false;
    this.shopsNearby = [];
    this.hasShopsNearby = false;
    this.myShops = [];
    this.hasShops = false;
    this.myQueues = [];
    this.hasQueues = false;
  }

  reloadData(){
    console.log("token: ", this.auth.getToken());

    this.resetData();


    this.shops.getNearbyShops(3,1,"")
      .subscribe(
        (shops) => {
          console.log("nearby shops: ", shops);
          this.shopsNearby = shops;
          this.hasShopsNearby = true;
        }
      );

    this.isLoggedIn = this.auth.isLoggedIn();

    if(this.isLoggedIn) {
      this.shops.getMyShops()
        .subscribe(
          (shops) => {
            console.log("my shops: ", shops);
            this.myShops = shops;
            this.hasShops = true;
          }
        );
      this.queues.getMyQueues()
        .subscribe(
          (queues) => {
            console.log("my queues: ", queues);
            this.myQueues = queues;
            this.hasQueues = true;
          }
    );
      this.queues.getMyQueuePosition()
        .subscribe(
          (queuePosition) => {
            console.log("my queue position: ", queuePosition);
            this.myQueuePosition = queuePosition;
            this.isInQueue = true;
          }
        );
    }
  }

  showMyQueuePositionPage(){
    // this.navCtrl.push(MyQueuePositionPage);
  }

  showSettingsPage(){
    this.navCtrl.push(SettingsPage);
  }

  showLoginPage(){
    this.navCtrl.push(LoginPage);
  }

  showSignupPage(){
    this.navCtrl.push(SignupPage);
  }

  showShopsPage(){
    this.navCtrl.push(ShopsPage);
  }

  showShopSinglePage(shopID){
    // this.navCtrl.push(ShopSinglePage, {id: shopID});

  }

  showMyShopsPage(){
    // this.navCtrl.push(MyShopsPage);
  }

  showMyShopSinglePage(shopID){
    // this.navCtrl.push(MyShopSinglePage, {id: shopID});
  }

  showMyQueuesPage(){
    // this.navCtrl.push(MyQueuesPage);
  }

  showMyQueueSinglePage(queueID){
    // this.navCtrl.push(MyQueueSinglePage, {id: queueID});
  }

  showCreateShopPage(){
    // this.navCtrl.push(ShopInfoPage, {newPage: true});
  }

}
