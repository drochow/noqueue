import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { ToastController } from 'ionic-angular';
// custom providers
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { LocationsProvider } from '../../providers/locations-provider';
import { ShopsProvider } from '../../providers/shops-provider';
import { QueuesProvider } from '../../providers/queues-provider';
// custom pages
import { LoginPage } from '../login/login';
import { SignupPage } from '../signup/signup';
import { SettingsPage } from '../settings/settings';
import { ShopsPage } from '../shops/shops';
import { ShopSinglePage } from '../shop-single/shop-single';
import { MyShopsPage } from '../my-shops/my-shops';
import { ShopInfoPage } from '../shop-info/shop-info';
import { MyQueuePositionPage } from '../my-queue-position/my-queue-position';
import { MyShopSinglePage } from '../my-shop-single/my-shop-single';

/*
  Generated class for the Dashboard page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-dashboard',
  templateUrl: 'dashboard.html',
  providers: [ShopsProvider, QueuesProvider, ConnectivityProvider, LocationsProvider],
  entryComponents: [LoginPage, SignupPage, SettingsPage, ShopsPage, ShopSinglePage, MyShopsPage, ShopInfoPage, MyQueuePositionPage, MyShopSinglePage]
})
export class DashboardPage {

// variables for data-binding with the template

  isLoggedIn: boolean = false;
  managerCount: number = 0;
  employeeCount: number = 0;
  myQueuePosition: any = {};
  isInQueue: boolean = false;
  shopsNearby: any = [];
  hasShopsNearby: boolean = false;
  myShops: any = [];
  hasShops: boolean = false;
  myQueues: any = [];
  hasQueues: boolean = false;
  searchTerm: string = "";
  radius: number = 0;


// constructor and lifecycle-events

  constructor(public navCtrl: NavController, private loadingCtrl: LoadingController, public auth: AuthenticationProvider, private shops: ShopsProvider, private queues: QueuesProvider,
  public connectivity: ConnectivityProvider, public locations: LocationsProvider, public toast: ToastController) {
  }

  ionViewWillEnter() : void{
    this.connectivity.checkNetworkConnection();

    let loading = this.loadingCtrl.create({
      content: 'Fetching data ...'
    });

    loading.present();
    this.reloadData();
    setTimeout(() => {
      loading.dismiss()
    },1000);
  }

// ViewModel logic (working with the data)

  refresh(refresher) : void{
    this.reloadData();
    if(refresher)
      setTimeout(() => {
        refresher.complete();
      }, 1000);
  }

  resetData() : void{
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

  registerError(message: string) : void{
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }

  reloadData() : void{
    this.resetData();
    this.locations.getUserLocation()
      .then(
        (location) => {
          let lat = location.latitude;
          let long = location.longitude;
          this.shops.getShops(3,0,"",100000000,lat,long)
            .subscribe(
              (shops) => {
                this.shopsNearby = shops;
                this.hasShopsNearby = true;
              },
              (error) => {
                this.registerError("Error while fetching shops.");
              }
            )
        }
      );

    this.isLoggedIn = this.auth.isLoggedIn();

    if(this.isLoggedIn) {
      this.shops.getMyShops()
        .subscribe(
          (shops) => {
            this.myShops = shops;
            this.managerCount = this.myShops.filter(function(s) { return s.isLeiter}).length;
            this.employeeCount = this.myShops.filter(function(s) { return !s.isLeiter}).length;
            this.hasShops = true;
          },
          (error) => {
          }
        );

      this.queues.getMyQueuePosition()
        .subscribe(
          (queuePosition) => {
            this.myQueuePosition = queuePosition;
            this.isInQueue = true;
          },
          (error) => {}
        );
    }
  }

// ViewController logic (reacting to events)

  searchShops() : void{
    this.navCtrl.push(ShopsPage, {preparedSearch: true, searchTerm: this.searchTerm, radius: this.radius});
  }
  
  showMyQueuePositionPage() : void{
    this.navCtrl.push(MyQueuePositionPage);
  }

  showSettingsPage() : void{
    this.navCtrl.push(SettingsPage);
  }

  showLoginPage() : void{
    this.navCtrl.push(LoginPage);
  }

  showSignupPage() : void{
    this.navCtrl.push(SignupPage);
  }

  showShopsPage() : void{
    this.navCtrl.push(ShopsPage);
  }

  showShopSinglePage(shopID: number) : void{
    this.navCtrl.push(ShopSinglePage, {shopID: shopID});
  }

  showMyShopsPage() : void{
    this.navCtrl.push(MyShopsPage);
  }

  showCreateShopPage() : void{
    this.navCtrl.push(ShopInfoPage, {newShop: true});
  }

}
