import { Component } from '@angular/core';
import { ElementRef, ViewChild } from '@angular/core';
import { NavController, NavParams, Platform } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { QueuesProvider } from '../../providers/queues-provider';
import { ServicesProvider } from '../../providers/services-provider';
import { ServiceSinglePage } from '../service-single/service-single';
import { GoogleMapsProvider } from '../../providers/google-maps-provider';

/*
  Generated class for the ShopSingle page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-shop-single',
  templateUrl: 'shop-single.html',
  providers: [ QueuesProvider, ShopsProvider, ServicesProvider, GoogleMapsProvider ],
  entryComponents: [ ServiceSinglePage ]
})
export class ShopSinglePage {

  @ViewChild('map') mapElement: ElementRef;

  employees = [];
  shop = {};
  services = [];
  shopID: any;
  error = false;
  errorMessage = "";
  shopActive = true;

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public queuesProvider: QueuesProvider,
  public servicesProvider: ServicesProvider, public platform: Platform, public maps: GoogleMapsProvider) {
    this.shopID = this.navParams.get('shopID');
  }

  ionViewDidLoad() {
    this.reloadData();
  }

  refresh(){
    this.reloadData();
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
            addressString: shop.adresse.strasse + " " + shop.adresse.hausNummer + ", " + shop.adresse.plz + shop.adresse.stadt
          };
          let mapLoaded = this.maps.init(this.mapElement.nativeElement, shop.adresse.latitude, shop.adresse.longitude);
          console.log("loaded map: ", mapLoaded);
          console.log("map object: ", this.mapElement);
        },
        (error) => {
          this.error = true;
          this.errorMessage = "Can't find shop with this ID"
        }
      );

    this.servicesProvider.getServicesFor(this.shopID)
      .subscribe(
        (services) => {
          console.log("services from server ", services);
          this.services = services;
        },
            (error) => {
              this.error = true;
              this.errorMessage = "Cannot find the services for this shop"
            }
      );

    this.shopsProvider.getNextAvailableSlots(this.shopID)
      .subscribe(
        (employees) => {
          let activeEmployees = employees.filter(e => e.anwesend);
          this.employees = activeEmployees;
          // this.employees.push({nutzerName: "any"});
          if(this.employees.length === 0){
            this.shopActive = false;
          }
        }
      );
  }

  showService(id){
    console.log("From shop-single - service id = " + id);
    let service = this.services.filter(s => s.id == id)[0];
    this.navCtrl.push(ServiceSinglePage, {shopID: this.shopID, serviceID: id, service: service});
  }

  // employeeSelection(event, serviceID, employeeName){
  //   this.selectedEmployees.set(serviceID, employeeName);
  // }
  //
  // lineUp(serviceID){
  //   let employeeName = this.selectedEmployees.get(serviceID) || "Any";
  //   this.queuesProvider.lineup({serviceID, shopID: this.shopID, employeeName})
  //     .subscribe(
  //       () =>  console.log("Lined up!"), // this.navCtrl.push(MyQueuePositionPage),
  //       (error) => {
  //         console.log("error");
  //       }
  //     )
  // }

}
