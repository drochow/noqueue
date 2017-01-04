import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { HttpProvider } from '../providers/http-provider';
import { AuthenticationProvider } from '../providers/authentication-provider';


/*
  Generated class for the QueuesProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class QueuesProvider {

  constructor(public http: Http, private httpProvider: HttpProvider, public auth: AuthenticationProvider) {
  }

  getMyQueues(){
    let route = this.httpProvider.ROUTES.users + "/" + this.auth.getUserId() + "/queues";
    return this.httpProvider.get(route);
  }

  getMyQueuePosition(){
    let route = this.httpProvider.ROUTES.users + "/" + this.auth.getUserId() + "/queueposition";
    return this.httpProvider.get(route);
  }

  getQueue(queueID){
    let route = this.httpProvider.ROUTES.users + "/" + this.auth.getUserId() + "/queues/" + queueID;
    return this.httpProvider.get(route);
  }

  lineup(queuePosition){
    let body = this.mapToExpectedJson(queuePosition);
    return this.httpProvider.post(this.httpProvider.ROUTES.queues, body);
  }

  leave(queuePositionID){
    return this.httpProvider.delete(this.httpProvider.ROUTES.queues + "/" + queuePositionID);
  }

  private mapToExpectedJson(queuePosition){
    return {
      nutzerId: this.auth.getUserId(),
      betriebId: queuePosition.shopID,
      dlId: queuePosition.serviceID,
      mitarbeiterId: queuePosition.employeeID
    }
  }

}
