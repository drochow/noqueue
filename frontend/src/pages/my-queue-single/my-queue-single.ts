import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { QueuesProvider } from '../../providers/queues-provider';

/*
  Generated class for the MyQueueSingle page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-my-queue-single',
  templateUrl: 'my-queue-single.html',
  providers: [QueuesProvider]
})
export class MyQueueSinglePage {

  users = [];
  queue = {};
  open = false;
  preventJoining = false;
  queueID: number;
  shopName = "";
  firstClient = "";
  firstStarted = false;
  error = false;
  errorMessage = "";

  constructor(public navCtrl: NavController, public navParams: NavParams, public queuesProvider: QueuesProvider) {
    this.queueID = navParams.get('queueID');
    this.shopName = navParams.get('shopName');
    this.reloadData();
  }

  ionViewDidLoad() {
  }

  reloadData(){
    this.error = false;
    this.errorMessage = "";

    this.queuesProvider.getQueue(this.queueID)
      .subscribe(
        (queue) => {
          console.log("queue: ", queue);
          this.queue = queue;
          this.users = queue.kunden;
          if(this.users.length > 0){
            this.firstClient = this.users[0].nutzerName;
          }
        },
        (error) => {
          this.error = true;
          this.errorMessage = error.message || "Couldn't get data from server";
        }
      );
  }

  start(){
    this.firstStarted = true;
  }

  end(){
    this.firstStarted = false;
    this.reloadData();
  }

}
