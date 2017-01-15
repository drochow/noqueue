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

  // @TODO - use getMyShops() instead and check if the user is 'isAnwesend'
  getMyQueues() : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/" + this.auth.getUserId() + "/queues";
    return this.httpProvider.get(route);
  }

  getMyQueuePosition() : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/queueposition";
    return this.httpProvider.get(route);
  }

  getQueue(queueID) : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/queues/" + queueID;
    return this.httpProvider.get(route);
  }

  // @TODO
  changeAttendance(shopID, userID, isThere: boolean){
    // ...
  }

  // @TODO
  closeQueue(shopID, userID){
    // ...
  }

  lineup(shopID, serviceID, employeeID) : Observable<any>{
    console.log(shopID, serviceID, employeeID);
    let body = this.mapToExpectedJson(shopID, serviceID, employeeID);
    console.log("body: ", body);
    return this.httpProvider.post(this.httpProvider.ROUTES.queues, body);
  }

  leave(queuePositionID) : Observable<any>{
    return this.httpProvider.delete(this.httpProvider.ROUTES.queues + "/" + queuePositionID);
  }


  // @TODO remove the default userid  value; it's only used for testing purposes
  // as the token from the fake server doesn't contain userID
  private mapToExpectedJson(shopID, serviceID, employeeName){
    let body = {
      nutzerId: this.auth.getUserId() || 1,
      betriebId: shopID,
      dlId: serviceID,
      mitarbeiterName: employeeName
    };
    return body;
  }

}
