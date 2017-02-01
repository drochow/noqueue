import { Component } from '@angular/core';
import { ElementRef, ViewChild } from '@angular/core';
import { NavController, NavParams, Platform } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { QueuesProvider } from '../../providers/queues-provider';
import { ServicesProvider } from '../../providers/services-provider';
import { ServiceSinglePage } from '../service-single/service-single';
import { GoogleMapsProvider } from '../../providers/google-maps-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { ToastController } from 'ionic-angular';

/*
  Generated class for the ShopSingle page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-shop-single',
  templateUrl: 'shop-single.html',
  providers: [ QueuesProvider, ShopsProvider, ServicesProvider, GoogleMapsProvider, ConnectivityProvider ],
  entryComponents: [ ServiceSinglePage ]
})
export class ShopSinglePage {

// declare variables used by the HTML template (ViewModel)

  @ViewChild('map') mapElement: ElementRef;

  employees: any = [];
  shop = {
    name: "",
    addressString: "",
    phone: "",
    email: "",
    openingHours: ""
  };
  services: any = [];
  shopID: any;
  error: boolean = false;
  shopActive: boolean = true;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public queuesProvider: QueuesProvider,
  public servicesProvider: ServicesProvider, public platform: Platform, public maps: GoogleMapsProvider, public connectivity: ConnectivityProvider,
  public toast: ToastController) {
    this.shopID = this.navParams.get('shopID');
  }

  ionViewDidLoad() : void{
    this.reloadData();
  }

  ionViewWillEnter() : void {
    this.connectivity.checkNetworkConnection();
  }

// ViewModel logic (working with the data)

  refresh() : void{
    this.reloadData();
  }

  reloadData() : void{
    this.error = false;

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
          this.maps.init(this.mapElement.nativeElement, shop.adresse.latitude, shop.adresse.longitude);
        },
        (error) => {
          this.registerError("Couldn't fetch data from server.")
        }
      );

    this.servicesProvider.getServicesFor(this.shopID)
      .subscribe(
        (services) => {
          this.services = services;
        },
            (error) => {
              this.registerError("Couldn't fetch data from server.");
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
        },
        (error) => this.registerError("Couldn't fetch data from server.")
      );
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

  showService(id: number) : void{
    let service = this.services.filter(s => s.id == id)[0];
    this.navCtrl.push(ServiceSinglePage, {shopID: this.shopID, serviceID: id, service: service});
  }


}
