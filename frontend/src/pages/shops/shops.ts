import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { ValidatorProvider } from '../../providers/validator-provider';

/*
  Generated class for the Shops page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-shops',
  templateUrl: 'shops.html',
  providers: [ShopsProvider]
})
export class ShopsPage {

  // variables used for binding with template
  searchTerm = "";
  radius = 0;
  shops = [];
  shouldShowShops = true;
  error = false;
  errorMessage = "";
  allShopsFetched = false;

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public validator: ValidatorProvider) {}

  ionViewDidLoad() {
  }

  search(){
    this.error = false;
    this.errorMessage = "";
    this.shouldShowShops = true;

    console.log("searching for: " + this.searchTerm + " with radius: " + this.radius);

    if(!this.validator.searchTerm(this.searchTerm)){
      this.error = true;
      this.errorMessage = "Search Term not valid";
      console.log("not valid");
      return;
    }

    this.shops = [];
    this.loadShops()
      .then(
        () => console.log("loaded"),
        () => console.log("error")
      )
  }

  loadShops(){
    let size = 10;
    let self = this;
    return new Promise(function(resolve, reject){
      self.shopsProvider.getNearbyShops(size, parseInt("" + (self.shops.length/size)) + 1, self.searchTerm)
        .subscribe(
          (data) => {
            console.log("data:", data);
            if(data.length < size){
              self.allShopsFetched = true;
            }
            for(var item of data){
              if(self.shops)
              self.shops.push(item);
            }
            if (self.shops.length === 0) {
              self.error = true;
              self.errorMessage = "No shops found";
              self.shouldShowShops = false;
            }
            resolve();
          },
          (error) => {
            self.shouldShowShops = false;
            self.error = true;
            self.errorMessage = error.message || "Something went wrong";
            reject();
          }
        )
    });
  }

  infiniteScroll(scroll){
    console.log("to infinity and beyond");
    this.loadShops()
      .then(
        () => {
          scroll.complete();
          console.log("whats up");
        },
        (error) => {
          this.error = true;
          this.errorMessage = error.message || "something went wrong";
          scroll.complete();
        }
      )
  }

  showShopSinglePage(id){
    console.log("shop id: ", id);
    // this.navCtrl.push(SingleShopPage, {shopID: id});
  }

}
