import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { LoadingController } from 'ionic-angular';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ShopsProvider } from '../../providers/shops-provider';
import { QueuesProvider } from '../../providers/queues-provider';
import { LoginPage } from '../login/login';
import { SignupPage } from '../signup/signup';
import { SettingsPage } from '../settings/settings';
import { ShopsPage } from '../shops/shops';
import { ShopSinglePage } from '../shop-single/shop-single';
import { MyShopsPage } from '../my-shops/my-shops';
import { MyQueuesPage } from '../my-queues/my-queues';
import { MyQueueSinglePage } from '../my-queue-single/my-queue-single';
import { ShopInfoPage } from '../shop-info/shop-info';
import { MyQueuePositionPage } from '../my-queue-position/my-queue-position';
import { MyShopSinglePage } from '../my-shop-single/my-shop-single';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { LocationsProvider } from '../../providers/locations-provider';

/*
  Generated class for the Dashboard page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-dashboard',
  templateUrl: 'dashboard.html',
  providers: [ShopsProvider, QueuesProvider, ConnectivityProvider, LocationsProvider],
  entryComponents: [LoginPage, SignupPage, SettingsPage, ShopsPage, ShopSinglePage, MyShopsPage, MyQueuesPage, MyQueueSinglePage, ShopInfoPage, MyQueuePositionPage, MyShopSinglePage]
})
export class DashboardPage {

  // variables for data-binding with the template
  isLoggedIn: boolean = false;
  managerCount: number = 0;
  employeeCount: number = 0;
  myQueuePosition = {};
  isInQueue: boolean = false;
  shopsNearby = [];
  hasShopsNearby: boolean = false;
  myShops = [];
  hasShops: boolean = false;
  myQueues = [];
  hasQueues: boolean = false;
  searchTerm: string = "";
  radius: number = 0;

  constructor(public navCtrl: NavController, private loadingCtrl: LoadingController, public auth: AuthenticationProvider, private shops: ShopsProvider, private queues: QueuesProvider,
  public connectivity: ConnectivityProvider, public locations: LocationsProvider) {
  }

  ionViewWillEnter() : void{
    let loading = this.loadingCtrl.create({
      content: 'Fetching data ...'
    });

    loading.present();
    if(!this.auth.getToken()) {
      this.auth.asyncSetup().then(
        () => {
          console.log("Auth AsynSetup...")
          this.reloadData();
          loading.dismiss();
        },
        (error) => {
          console.log(error);
        }
      );
    } else {
      this.reloadData();
      loading.dismiss()
    }
  }

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

  reloadData() : void{
    console.log("token: ", this.auth.getToken());
    console.log("Internet connection: ", this.connectivity.isOnline());

    this.resetData();

    this.locations.getUserLocation()
      .then(
        (location) => {
          console.log("Got User Location:", location)
          let lat = location.latitude;
          let long = location.longitude;
          this.shops.getShops(3,0,"",100000000,lat,long)
            .subscribe(
              (shops) => {
                console.log("nearby shops: ", shops);
                this.shopsNearby = shops;
                this.hasShopsNearby = true;
              },
              (error) => {
                let jsonError = JSON.parse(error._body);
                console.log("Error while fetching shops: ", jsonError);
              }
            )
        }
      );

    this.isLoggedIn = this.auth.isLoggedIn();

    if(this.isLoggedIn) {
      this.shops.getMyShops()
        .subscribe(
          (shops) => {
            console.log("my shops: ", shops);
            this.myShops = shops;
            this.managerCount = this.myShops.filter(function(s) { return s.isLeiter}).length
            this.employeeCount = this.myShops.filter(function(s) { return !s.isLeiter}).length
            this.hasShops = true;
          }
        );
      //@TODO - use getMyShops() instead
      // this.queues.getMyQueues()
      //   .subscribe(
      //     (queues) => {
      //       console.log("my queues: ", queues);
      //       this.myQueues = queues;
      //       this.hasQueues = true;
      //     }
      // );
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
    console.log("Why are you not working...");
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

  showMyQueuesPage() : void{
    this.navCtrl.push(MyQueuesPage);
  }

  showMyQueueSinglePage(queueID: number, shopName: string) : void{
    this.navCtrl.push(MyQueueSinglePage, {queueID: queueID, shopName: shopName});
  }

  showCreateShopPage() : void{
    this.navCtrl.push(ShopInfoPage, {newShop: true});
  }


  leave() : void{
    this.queues.leave()
      .subscribe(
        () => this.refresh(undefined),
        (error) => console.log(error)
      )
  }

}
