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

  myShops: any = [];
  hasShops: boolean = false;
  error: boolean = false;
  errorMessage: string = "";

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider) {}

  ionViewDidLoad() : void{
    this.reloadData();
  }

  ionViewWillEnter() : void{
    this.reloadData();
  }

  refresh(refresher: any) : void{
    this.reloadData();

    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

  reloadData() : void{
    this.error = false;
    this.errorMessage = "";

    this.shopsProvider.getMyShops()
      .subscribe(
        (shops) => {
          this.myShops = shops;
          this.myShops.sort(shop => shop.isLeiter ? -1 : 0);
          this.hasShops = this.myShops.length > 0;
        },
        (error) => {
          this.error = true;
          this.errorMessage = error.message || "Couldn't get shops from server";
        }
      )
  }

  showMyShopSinglePage(shopID: number, isLeiter: boolean, isAnwesend: boolean) : void{
    this.navCtrl.push(MyShopSinglePage, {shopID: shopID, isLeiter: isLeiter, isAnwesend: isAnwesend});
  }

  createNewShop() : void{
    this.navCtrl.push(ShopInfoPage,{newShop: true});
  }

}
