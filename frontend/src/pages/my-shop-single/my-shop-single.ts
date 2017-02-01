import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { ServicesProvider } from '../../providers/services-provider';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ShopInfoPage } from '../shop-info/shop-info';
import { ServiceInfoPage } from '../service-info/service-info';
import { CoworkersPage } from '../coworkers/coworkers';
import {QueuesProvider} from "../../providers/queues-provider";
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { ToastController } from 'ionic-angular';

/*
  Generated class for the MyShopSingle page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-shop-single',
  templateUrl: 'my-shop-single.html',
  providers: [ShopsProvider, ServicesProvider, QueuesProvider, ConnectivityProvider],
  entryComponents: [ ShopInfoPage, ServiceInfoPage, CoworkersPage ]
})
export class MyShopSinglePage {

// declare variables used by the HTML template (ViewModel)

  isAnwesend: boolean = false;
  shopID: number;
  isLeiter: boolean = false;
  shop = {
    name: "",
    address: "",
    phone: "",
    email: "",
    openingHours: ""
  };
  managers: any = [];
  employees: any = [];
  currentManagerWorking: boolean = false;
  hasOwnQueueToggle: boolean = false;
  services: any = [];
  error: boolean = false;
  queue: any = [];
  clients: any = [];
  firstClient: string = "";
  firstStarted: boolean = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public servicesProvider: ServicesProvider,
  public auth: AuthenticationProvider, public queuesProvider: QueuesProvider, public connectivity: ConnectivityProvider,
  public toast: ToastController) {
    this.shopID = this.navParams.get('shopID');
    this.isLeiter = this.navParams.get('isLeiter');
    this.isAnwesend = this.navParams.get('isAnwesend');
  }

  ionViewDidLoad() : void{
  }

  ionViewWillEnter(): void {
    this.connectivity.checkNetworkConnection();
    this.reloadData();
  }

  ionViewWillLeave() : void{
    // send changes of the toggle only when leaving the page
    if(this.hasOwnQueueToggle !== this.currentManagerWorking){
      // this.shopsProvider.managerWorking(userID, shopID, bool)..
    }
  }

// ViewModel logic (working with the data)

  registerError(message: string) : void{
    this.error = true;
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }

  reloadData() : void{
    this.error = false;

    this.shopsProvider.getShop(this.shopID)
      .subscribe(
        (shop) => {
          console.log("GET Shop: ", shop);
          this.shop = {
            name: shop.name,
            phone: shop.tel,
            email: shop.kontaktEmail,
            openingHours: shop.oeffnungszeiten,
            address: shop.adresse.strasse + " " + shop.adresse.hausNummer + ", " + shop.adresse.plz + shop.adresse.stadt
          }
        },
        (error) => this.registerError("Error while fetching data from the server.")
      );

    if(this.isLeiter) {
      this.shopsProvider.getEmployees(this.shopID)
        .subscribe(
          (employees) => {
            console.log("GET Employees: ", employees);
            this.employees = employees;
          },
          (error) => this.registerError("Error while fetching data from the server.")
        );

      this.shopsProvider.getManagers(this.shopID)
        .subscribe(
          (managers) => {
            console.log("GET Managers: ", managers);
            this.managers = managers;
          },
          (error) => this.registerError("Error while fetching data from the server.")
        );

      this.servicesProvider.getServicesFor(this.shopID)
        .subscribe(
          (services) => this.services = services,
          (error) => this.registerError("Error while fetching data from the server.")
        )
    } else {
      this.shopsProvider.getQueueFor(this.shopID)
        .subscribe(
          (queue) => {
            console.log("queue: ", queue);
            this.queue = queue;
            this.clients = queue.wsps;
            if(this.clients.length > 0){
              this.firstClient = this.clients[0].anwender.nutzerName;
            }
          },
            (error) => this.registerError("Error while fetching data from the server.")
        )
    }
  }

  // start(){
  //   this.firstStarted = true;
  // }
  //
  // end(){
  //   this.firstStarted = false;
  //   this.reloadData();
  // }

// ViewController logic (reacting to events)

  switchAttendance() : void{
    this.queuesProvider.changeAttendance(this.shopID, !this.isAnwesend).subscribe(
      () => { this.isAnwesend = !this.isAnwesend},
      (error) => this.registerError("Error while switching attendance.")
    )
  }


  fireManager(slidingItem: any, userID: number) : void{
    slidingItem.close();
    this.shopsProvider.fireManager(userID, this.shopID)
      .subscribe(
        () => this.reloadData(),
        (error) => {
          this.registerError("Couldn't fire the manager.")
        }
      )
  }

  fireEmployee(slidingItem: any, userID: number) : void{
    slidingItem.close();
    this.shopsProvider.fireEmployee(userID, this.shopID)
      .subscribe(
        () => this.reloadData(),
        (error) => {
          this.registerError("Couldn't fire the employee.")
        }
      )
  }

  startWorkOn(wspId: number) : void{
    this.queuesProvider.startWorkOn(this.shopID, wspId)
      .subscribe(
        () => this.reloadData(),
        (error) => {
          this.registerError("Couldn't start working on this user.")
        }
      )
  }

  finishWorkOn(wspId: number) : void{
    this.queuesProvider.finishWorkOn(this.shopID, wspId)
      .subscribe(
        () => this.reloadData(),
        (error) => {
          this.registerError("Couldn't finish working with on this user.")
        }
      )
  }

  editShopInfo() : void{
    if(this.isLeiter)
      this.navCtrl.push(ShopInfoPage, {newShop: false, shopID: this.shopID});
  }

  showService(serviceID: number) : void{
    let service = this.services.filter(s => s.id == serviceID)[0];
    console.log("will show: ", service);
    this.navCtrl.push(ServiceInfoPage, {newShop: false, shopID: this.shopID, newService: false, serviceID: serviceID, service: service});
  }

  deleteService(serviceID: number) : void{
    console.log("will delete: " + serviceID);
    //
  }

  createService() : void{
    console.log("will create");
    this.navCtrl.push(ServiceInfoPage, {newShop: false, shopID: this.shopID, newService: true, serviceID: -1, service: undefined});
  }

  addCoworkers() : void{
    this.navCtrl.push(CoworkersPage, {newShop: false, shopID: this.shopID});
  }


}
