import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ElementRef, ViewChild } from '@angular/core';
import { QueuesProvider } from '../../providers/queues-provider';
import { ShopsProvider } from '../../providers/shops-provider';
import { GoogleMapsProvider } from '../../providers/google-maps-provider';

/*
  Generated class for the MyQueuePosition page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-queue-position',
  templateUrl: 'my-queue-position.html',
  providers: [QueuesProvider, ShopsProvider, GoogleMapsProvider]
})
export class MyQueuePositionPage {

  @ViewChild('map') mapElement: ElementRef;

// declare variables used by the HTML template (ViewModel)

  shop = {};
  queuePosition = {
    id: 0,
    mitarbeiter: "",
    betrieb: "",
    dlId: 0,
    dlName: "",
    schaetzZeitpunkt: 0,
  };

// constructor and lifecycle-events (chronological order)

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public queuesProvider: QueuesProvider,
  public maps: GoogleMapsProvider) {}

  ionViewDidLoad() : void{
    this.reloadData();
  }

// ViewModel logic (working with the data)

  refresh(refresher) : void{
    this.reloadData();

    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

  reloadData() : void{
    this.queuesProvider.getMyQueuePosition()
      .subscribe(
        (position) => {
          console.log(position);
          this.queuePosition = position;
          if(position.shopID){
            this.shopsProvider.getShop(position.shopID)
              .subscribe(
                (shop) => this.shop = shop
              );
          }
          let mapLoaded = this.maps.init(this.mapElement.nativeElement, 52.545433, 13.354636);
          console.log(mapLoaded);
        },
        (error) => {
          console.log(error);
        }
      );
  }

// ViewController logic (reacting to events)

  leave() : void{
    this.queuesProvider.leave()
      .subscribe(
        () => this.navCtrl.popToRoot(),
        (error) => console.log(error)
      )
  }

}
