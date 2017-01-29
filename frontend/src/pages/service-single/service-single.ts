import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { QueuesProvider } from '../../providers/queues-provider';
import { ServicesProvider } from '../../providers/services-provider';
import { MyQueuePositionPage } from '../my-queue-position/my-queue-position';

/*
  Generated class for the ServiceSingle page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-service-single',
  templateUrl: 'service-single.html',
  providers: [ ShopsProvider, QueuesProvider, ServicesProvider ],
  entryComponents: [ MyQueuePositionPage ]
})
export class ServiceSinglePage {

// declare variables used by the HTML template (ViewModel)

  employees = [];
  selectedEmployee: number = 0;
  service = {
    type: "",
    description: "",
    duration: 0
  };
  shopID: any;
  serviceID: number;
  error: boolean = false;
  errorMessage: string = "";
  queueActive: boolean = false;

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public queuesProvider: QueuesProvider,
  public servicesProvider: ServicesProvider) {
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

// ViewModel logic (working with the data)

  refresh(refresher) : void{
    this.reloadData();

    // @TODO - return a promise in reloadData() and complete the refresher when resolved
    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

  reloadData() : void{
    this.error = false;
    this.errorMessage = "";

    console.log("from service page > shopID : serviceID = " + this.shopID + " : " + this.serviceID);

    // this.servicesProvider.getService(this.serviceID, this.shopID)
    //   .subscribe(
    //     (service) => {
    //       console.log("service from server: ", service);
    //       this.service = {
    //         type: service.name,
    //         duration: service.dauer,
    //         description: service.kommentar
    //       }
    //     },
    //     (error) => console.log(error)
    //   );

    this.shopsProvider.getEmployees(this.shopID)
      .subscribe(
        (employees) => {
          console.log("Employees: ", employees);
          this.employees = employees;
          this.queueActive = this.employees.length > 0;
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error ", jsonError);
        }
      );

    // this.shopsProvider.getNextAvailableSlots(this.shopID)
    //   .subscribe(
    //     (employees) => {
    //       console.log("Employees: ", employees);
    //       this.employees = employees;
    //       this.queueActive = this.employees.length > 0;
    //     }
    //   );
  }

// ViewController logic (reacting to events)

  // @TODO
  employeeSelection() : void{
    console.log(this.selectedEmployee);
    // get the next available time slot for this employee
  }

  lineUp() : void{
    console.log("data at this point: ", this.shopID, this.serviceID, this.selectedEmployee);
    this.queuesProvider.lineup(this.serviceID, this.selectedEmployee)
      .subscribe(
        () => {
          console.log('lined up');
          this.navCtrl.push(MyQueuePositionPage);
        },
        (error) => {
          console.log("error when lining up: ", error);
          this.error = true;
          this.errorMessage = error.message || "Couldn't line up in the queue."
        }
      )
  }


}
