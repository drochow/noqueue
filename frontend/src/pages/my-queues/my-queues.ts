import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { QueuesProvider } from '../../providers/queues-provider';
import { MyQueueSinglePage } from '../my-queue-single/my-queue-single';


/*
  Generated class for the MyQueues page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-queues',
  templateUrl: 'my-queues.html',
  providers: [QueuesProvider],
  entryComponents: [MyQueueSinglePage]
})
export class MyQueuesPage {

  queues = [];
  error: boolean = false;
  errorMessage: string = "";

  constructor(public navCtrl: NavController, public navParams: NavParams, public queuesProvider: QueuesProvider) {
  }

  ionViewDidLoad() : void{
    this.reloadData();
  }

  reloadData() : void{
    this.error = false;
    this.errorMessage = "";

    // @TODO - use getMyShops() instead and make extra request for the name of the shop
    // this.queuesProvider.getMyQueues()
    //   .subscribe(
    //     (queues) => this.queues = queues,
    //     (error) => {
    //       this.error = true;
    //       this.errorMessage = error.message || "Couldn't retrieve queues from server";
    //     }
    //   );
  }

  showQueue(id: number, shopName: string) : void{
    this.navCtrl.push(MyQueueSinglePage, {queueID: id, shopName: shopName});
  }

}
