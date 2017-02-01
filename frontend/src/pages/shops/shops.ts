import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ToastController } from 'ionic-angular';
// custom providers
import { ShopsProvider } from '../../providers/shops-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { LocationsProvider } from '../../providers/locations-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
// custom pages
import { ShopSinglePage } from '../shop-single/shop-single';


/*
  Generated class for the Shops page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-shops',
  templateUrl: 'shops.html',
  providers: [ShopsProvider, LocationsProvider, ConnectivityProvider],
  entryComponents: [ ShopSinglePage ]
})
export class ShopsPage {

// declare variables used by the HTML template (ViewModel)

  searchTerm: string = "";
  radius: number = 0;
  shops: any = [];
  shouldShowShops: boolean = true;
  error: boolean = false;
  allShopsFetched: boolean = false;
  noShops: boolean = false;
  location: any;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public validator: ValidatorProvider,
  public locations: LocationsProvider, public connectivity: ConnectivityProvider, public toast : ToastController) {
    if(this.navParams.get('preparedSearch')){
      this.searchTerm = this.navParams.get('searchTerm');
      this.radius = this.navParams.get('radius');
    }
  }

  ionViewWillEnter() : void{
    this.connectivity.checkNetworkConnection();
    this.locations.getUserLocation()
      .then(
        (location) => {
          this.location = location;
          if(this.searchTerm.length > 0) this.search(undefined);
        },
        (error) => {}
      )
  }

// ViewController logic (reacting to events)

  search(event: any) : void{
    this.error = false;

    if(!this.validator.searchTerm(this.searchTerm)){
      this.registerError("Search term not valid.");
      return;
    }

    this.shops = [];
    this.loadShops()
      .then(
        () => {},
        (error) => {
          this.registerError("Couldn't fetch data from server.")
        }
      )
  }

  loadShops() : Promise<any>{
    this.shouldShowShops = true;
    this.noShops = false;
    let size = 10;
    let self = this;
    return new Promise(function(resolve, reject){
      self.shopsProvider.getShops(size, Number((self.shops.length/size)), self.searchTerm, self.radius > 0 ? self.radius : "", self.location.latitude, self.location.longitude)
        .subscribe(
            (shops) => {
              if(shops.length < size){
                self.allShopsFetched = true;
              }
              for(var item of shops){
                if(self.shops){
                  item.dist = (Number(item.distanz)/1000).toFixed(1);
                  self.shops.push(item);
                }
              }
              if (self.shops.length === 0) {
                self.noShops = true;
                self.shouldShowShops = false;
              }
              resolve();
            },
              (error) => {
                self.shouldShowShops = false;
                self.error = true;
                reject(error);
              }
        );
    });
  }

  infiniteScroll(scroll: any) : void{
    this.loadShops()
      .then(
        () => {
          scroll.complete();
        },
        (error) => {
          this.registerError("Couldn't fetch data from server.");
          scroll.complete();
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

  showShopSinglePage(id: number) : void{
    this.navCtrl.push(ShopSinglePage, {shopID: id});
  }

}
