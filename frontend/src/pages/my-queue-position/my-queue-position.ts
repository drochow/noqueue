import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { QueuesProvider } from '../../providers/queues-provider';
import { ShopsProvider } from '../../providers/shops-provider';

/*
  Generated class for the MyQueuePosition page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-queue-position',
  templateUrl: 'my-queue-position.html',
  providers: [QueuesProvider, ShopsProvider]
})
export class MyQueuePositionPage {

  shop = {};
  queuePosition = {
    id: 0,
    uhrzeit: "",
    mitarbeiter: "",
    adresse: "",
    distanz: "",
    platzNummer: "",
    tel: "",
    betriebName: ""
  };

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public queuesProvider: QueuesProvider) {}

  ionViewDidLoad() {
    this.reloadData();
  }

  refresh(refresher){
    this.reloadData();

    setTimeout(() => {
      refresher.complete();
    }, 1000);
  }

  reloadData(){
    this.queuesProvider.getMyQueuePosition()
      .subscribe(
        (position) => {
          this.queuePosition = position;
          if(position.shopID){
            this.shopsProvider.getShop(position.shopID)
              .subscribe(
                (shop) => this.shop = shop
              );
          }
        },
        (error) => {
          console.log(error);
        }
      );
  }

  leave(){
    this.queuesProvider.leave(this.queuePosition.id)
      .subscribe(
        () => this.navCtrl.popToRoot(),
        (error) => console.log(error)
      )
  }

}
