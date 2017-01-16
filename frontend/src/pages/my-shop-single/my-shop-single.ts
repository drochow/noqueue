import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { ServicesProvider } from '../../providers/services-provider';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ShopInfoPage } from '../shop-info/shop-info';
import { ServiceInfoPage } from '../service-info/service-info';
import { CoworkersPage } from '../coworkers/coworkers';

/*
  Generated class for the MyShopSingle page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-shop-single',
  templateUrl: 'my-shop-single.html',
  providers: [ShopsProvider, ServicesProvider],
  entryComponents: [ ShopInfoPage, ServiceInfoPage, CoworkersPage ]
})
export class MyShopSinglePage {

  shopID: number;
  shop = {};
  managers = [];
  employees = [];
  currentManagerWorking = false;
  hasOwnQueueToggle = false;
  services = [];
  error = false;
  errorMessage = "";

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public servicesProvider: ServicesProvider,
  public auth: AuthenticationProvider) {
    this.shopID = this.navParams.get('shopID');
  }

  ionViewDidLoad() {
    this.reloadData();
  }

  ionViewWillLeave(){
    // send changes of the toggle only when leaving the page
    if(this.hasOwnQueueToggle !== this.currentManagerWorking){
      // this.shopsProvider.managerWorking(userID, shopID, bool)..
    }
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }

  reloadData(){
    this.error = false;
    this.errorMessage = "";

    this.shopsProvider.getShop(this.shopID)
      .subscribe(
        (shop) => {
          this.shop = {
            name: shop.name,
            phone: shop.tel,
            email: shop.kontaktEmail,
            openingHours: shop.oeffnungszeiten,
            address: shop.adresse.strasse + " " + shop.adresse.hausNummer + ", " + shop.adresse.plz + shop.adresse.stadt
          }
        },
        (error) => this.registerError(error.message || "Coulnd't get this shop from the server")
      );

    this.shopsProvider.getEmployees(this.shopID)
      .subscribe(
        (employees) => this.employees = employees,
        (error) => this.registerError(error.message || "Something went wrong")
      );

    this.shopsProvider.getManagers(this.shopID)
      .subscribe(
        (managers) => {
          this.managers = managers;
          this.currentManagerWorking = this.managers.filter(m => m.anwenderId && m.anwenderId === this.auth.getUserId())[0].anwesend;
        },
        (error) => this.registerError(error.message || "Something went wrong")
      );

    this.servicesProvider.getServicesFor(this.shopID)
      .subscribe(
        (services) => this.services = services,
        (error) => this.registerError(error.message || "Something went wrong")
      )
  }

  demoteManager(slidingItem, userID){
    slidingItem.close();
    this.shopsProvider.demoteManager(userID, this.shopID)
      .then(
        () => {},
        (error) => this.registerError(error || "Couldn't promote employee")
      );
  }

  promoteEmployee(slidingItem, userID){
    slidingItem.close();
    this.shopsProvider.promoteEmployee(userID, this.shopID)
      .then(
        () => {},
        (error) => this.registerError(error || "Couldn't promote employee")
      );
  }

  fireManager(slidingItem, userID){
    slidingItem.close();
    this.shopsProvider.fireManager(userID, this.shopID)
      .subscribe(
        () => this.reloadData(),
        (error) => this.registerError("Couldn't fire manager")
      )
  }

  fireEmployee(slidingItem, userID){
    slidingItem.close();
    this.shopsProvider.fireEmployee(userID, this.shopID)
      .subscribe(
        () => this.reloadData(),
        (error) => this.registerError("Couldn't fire employee")
      )
  }

  editShopInfo(){
    this.navCtrl.push(ShopInfoPage, {newShop: false, shopID: this.shopID});
  }

  showService(serviceID){
    console.log("will show: " + serviceID);
    this.navCtrl.push(ServiceInfoPage, {newShop: false, shopID: this.shopID, newService: false, serviceID: serviceID});
  }

  deleteService(serviceID){
    console.log("will delete: " + serviceID);
    //
  }

  createService(){
    this.navCtrl.push(ServiceInfoPage, {newShop: false, shopID: this.shopID, newService: true});
  }

  addCoworkers(){
    this.navCtrl.push(CoworkersPage, {newShop: false, shopID: this.shopID});
  }

}
