import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ToastController } from 'ionic-angular';
// custom providers
import { ShopsProvider } from '../../providers/shops-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { MyShopSinglePage } from '../my-shop-single/my-shop-single';
// custom pages
import { ShopInfoPage } from '../shop-info/shop-info';

/*
  Generated class for the MyShops page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-shops',
  templateUrl: 'my-shops.html',
  providers: [ShopsProvider, ConnectivityProvider],
  entryComponents: [ MyShopSinglePage, ShopInfoPage ]
})
export class MyShopsPage {

  // declare variables used by the HTML template (ViewModel)

  myShops: any = [];
  hasShops: boolean = false;
  error: boolean = false;

  // constructor and lifecycle-events

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider,
  public connectivity: ConnectivityProvider, public toast : ToastController) {}

  ionViewDidLoad() : void{
    this.reloadData();
  }

  ionViewWillEnter() : void{
    this.connectivity.checkNetworkConnection();
    this.reloadData();
  }

  // ViewModel logic - working with the data

  refresh(refresher: any) : void{
    this.reloadData();

    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

  reloadData() : void{
    this.error = false;

    this.shopsProvider.getMyShops()
      .subscribe(
        (shops) => {
          this.myShops = shops;
          this.myShops.sort(shop => shop.isLeiter ? -1 : 0);
          this.hasShops = this.myShops.length > 0;
        },
        (error) => {
          this.registerError("Couldn't fetch data from server.")
        }
      )
  }

  registerError(message: string) : void{
    this.error = true;
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }

// ViewController logic (reacting to events)

  showMyShopSinglePage(shopID: number, isLeiter: boolean, isAnwesend: boolean) : void{
    this.navCtrl.push(MyShopSinglePage, {shopID: shopID, isLeiter: isLeiter, isAnwesend: isAnwesend});
  }

  createNewShop() : void{
    this.navCtrl.push(ShopInfoPage,{newShop: true});
  }

}
