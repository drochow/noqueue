import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { QueuesProvider } from '../../providers/queues-provider';
import { ServicesProvider } from '../../providers/services-provider';
import { MyQueuePositionPage } from '../my-queue-position/my-queue-position';
import { AuthenticationProvider } from '../../providers/authentication-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { ToastController } from 'ionic-angular';

/*
  Generated class for the ServiceSingle page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-service-single',
  templateUrl: 'service-single.html',
  providers: [ ShopsProvider, QueuesProvider, ServicesProvider, ConnectivityProvider ],
  entryComponents: [ MyQueuePositionPage ]
})
export class ServiceSinglePage {

// declare variables used by the HTML template (ViewModel)

  employees: any  = [];
  selectedEmployee: number = 0;
  service = {
    type: "",
    description: "",
    duration:  0
  };
  shopID: any;
  serviceID: number;
  error: boolean = false;
  errorMessage: string = "";
  queueActive: boolean = false;
  isLoggedIn: boolean = false;
  isLignedUp: boolean = true;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public queuesProvider: QueuesProvider,
  public servicesProvider: ServicesProvider, public auth: AuthenticationProvider, public connectivity: ConnectivityProvider,
  public toast: ToastController) {
    this.shopID = this.navParams.get('shopID');
    this.serviceID = this.navParams.get('serviceID');
    let navService = this.navParams.get('service');
    this.service = {
      type: navService.name,
      duration: navService.dauer,
      description: navService.kommentar
    };
  }

  ionViewDidLoad() : void{
    this.reloadData();
  }

  ionViewWillEnter() : void{
    this.connectivity.checkNetworkConnection();
    this.isLoggedIn = this.auth.isLoggedIn();
    this.queuesProvider.getMyQueuePosition()
      .subscribe(
        (position) => this.isLignedUp = true,
        (error) => this.isLignedUp = false
      )
  }

// ViewModel logic (working with the data)

  refresh(refresher: any) : void{
    this.reloadData();

    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

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
    this.errorMessage = "";
    this.shopsProvider.getNextAvailableSlots(this.shopID)
      .subscribe(
        (employees) => {
          console.log("Employees ns: ", employees);
          this.employees = employees;
          this.queueActive = this.employees.length > 0;
        },
        (error) => this.registerError("Couldn't fetch data from server.")
      );
  }

// ViewController logic (reacting to events)

  lineUp() : void{
    console.log("data at this point: ", this.shopID, this.serviceID, this.selectedEmployee);
    this.queuesProvider.lineup(this.serviceID, this.selectedEmployee)
      .subscribe(
        () => {
          console.log('lined up');
          this.navCtrl.push(MyQueuePositionPage);
        },
        (error) => {
          this.registerError("Couldn't line up for this service.")
        }
      )
  }

}
