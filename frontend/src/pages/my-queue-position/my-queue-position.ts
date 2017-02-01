import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ElementRef, ViewChild } from '@angular/core';
import { QueuesProvider } from '../../providers/queues-provider';
import { ShopsProvider } from '../../providers/shops-provider';
import { GoogleMapsProvider } from '../../providers/google-maps-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { ToastController } from 'ionic-angular';

/*
  Generated class for the MyQueuePosition page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-queue-position',
  templateUrl: 'my-queue-position.html',
  providers: [QueuesProvider, ShopsProvider, GoogleMapsProvider, ConnectivityProvider]
})
export class MyQueuePositionPage {

  // declare variables used by the HTML template (ViewModel)

  @ViewChild('map') mapElement: ElementRef;

  shop: any = {};
  queuePosition = {
    id: 0,
    mitarbeiter: "",
    betrieb: "",
    dlId: 0,
    dlName: "",
    schaetzZeitpunkt:  0,
  };

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public queuesProvider: QueuesProvider,
  public maps: GoogleMapsProvider, public connectivity: ConnectivityProvider, public toast: ToastController) {}

  ionViewDidLoad() : void{
    this.reloadData();
  }

  ionViewWillEnter() : void{
    this.connectivity.checkNetworkConnection();
  }

// ViewModel logic (working with the data)

  refresh(refresher: any) : void{
    this.reloadData();

    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

  registerError(message: string) : void{
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }

  reloadData() : void{
    this.queuesProvider.getMyQueuePosition()
      .subscribe(
        (position) => {
          this.queuePosition = position;
          if(position.shopID){
            this.shopsProvider.getShop(position.shopID)
              .subscribe(
                (shop) => this.shop = shop,
                (error) => this.registerError("Error while fetching data from the server.")
              );
          }
          this.maps.init(this.mapElement.nativeElement, 52.545433, 13.354636);
        },
        (error) => {
          this.registerError("Error while fetching data from the server.");
        }
      );
  }

// ViewController logic (reacting to events)

  leave() : void{
    this.queuesProvider.leave()
      .subscribe(
        () => this.navCtrl.popToRoot(),
        (error) => this.registerError("Error while leaving the queue.")
      )
  }

}
