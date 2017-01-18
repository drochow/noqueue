import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { MyShopSinglePage } from '../my-shop-single/my-shop-single';
import { ShopInfoPage } from '../shop-info/shop-info';

/*
  Generated class for the MyShops page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-shops',
  templateUrl: 'my-shops.html',
  providers: [ShopsProvider],
  entryComponents: [ MyShopSinglePage, ShopInfoPage ]
})
export class MyShopsPage {

  myShops = [];
  hasShops = false;
  error = false;
  errorMessage = "";

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider) {}

  ionViewDidLoad() {
    this.reloadData();
  }

  ionViewWillEnter(){
    this.reloadData();
  }

  refresh(refresher){
    this.reloadData();

    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

  reloadData(){
    this.error = false;
    this.errorMessage = "";

    this.shopsProvider.getMyShops()
      .subscribe(
        (shops) => {
          this.myShops = shops;
          this.hasShops = this.myShops.length > 0;
        },
        (error) => {
          this.error = true;
          this.errorMessage = error.message || "Couldn't get shops from server";
        }
      )
  }

  showMyShopSinglePage(shopID, isLeiter, isAnwesend){
    this.navCtrl.push(MyShopSinglePage, {shopID: shopID, isLeiter: isLeiter, isAnwesend: isAnwesend});
  }

  createNewShop(){
    this.navCtrl.push(ShopInfoPage,{newShop: true});
  }

}
