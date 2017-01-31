import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ShopSinglePage } from '../shop-single/shop-single';
import { LocationsProvider } from '../../providers/locations-provider';

/*
  Generated class for the Shops page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-shops',
  templateUrl: 'shops.html',
  providers: [ShopsProvider, LocationsProvider],
  entryComponents: [ ShopSinglePage ]
})
export class ShopsPage {

// declare variables used by the HTML template (ViewModel)

  searchTerm: string = "";
  radius: number = 0;
  shops: any = [];
  shouldShowShops: boolean = true;
  error: boolean = false;
  errorMessage: string = "";
  allShopsFetched: boolean = false;
  noShops: boolean = false;
  location: any;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public validator: ValidatorProvider,
  public locations: LocationsProvider) {
    if(this.navParams.get('preparedSearch')){
      this.searchTerm = this.navParams.get('searchTerm');
      this.radius = this.navParams.get('radius');
    }
  }

  ionViewWillEnter() : void{
    this.locations.getUserLocation()
      .then(
        (location) => {
          this.location = location;
          if(this.searchTerm.length > 0) this.search(undefined);
        },
        (error) => console.log(error)
      )
  }

// ViewController logic (reacting to events)

  search(event: any) : void{
    this.error = false;
    this.errorMessage = "";


    if(!this.validator.searchTerm(this.searchTerm)){
      this.error = true;
      this.errorMessage = "Search Term not valid";
      console.log("Search term not valid");
      return;
    }

    console.log("searching for: " + this.searchTerm + " with radius: " + this.radius);

    this.shops = [];
    this.loadShops()
      .then(
        () => console.log("shops loaded"),
        (error) => {
          console.log("Error 1");
          let jsonError = JSON.parse(error._body);
          console.log("Error while fetching shops: ", jsonError);
          console.log("Error while fetching shops: ", error);
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
              console.log("GET Shops in shops.ts :", shops);
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
              console.log("Should show shops 2: " + self.shouldShowShops);
              resolve();
            },
              (error) => {
                console.log("Error 2");
                self.shouldShowShops = false;
                self.error = true;
                self.errorMessage = error.message || "Something went wrong";
                console.log("error: ", JSON.parse(error._body));
                reject(error);
              }
        );
    });
  }

  infiniteScroll(scroll: any) : void{
    console.log("to infinity and beyond");
    this.loadShops()
      .then(
        () => {
          scroll.complete();
          console.log("whats up");
        },
        (error) => {
          console.log("Error 3");
          this.error = true;
          this.errorMessage = error.message || "something went wrong";
          scroll.complete();
        }
      )
  }

  showShopSinglePage(id: number) : void{
    console.log("shop id: ", id);
    this.navCtrl.push(ShopSinglePage, {shopID: id});
  }

}
