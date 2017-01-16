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
  getMyQueues(shopId) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopId + "/ws";
    return this.httpProvider.get(route);
  }

  getMyQueuePosition() : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/wsp";
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

  lineup(serviceID: number, employeeID: number) : Observable<any>{
    console.log(serviceID, employeeID);
    return this.httpProvider.post(this.httpProvider.ROUTES.users + "/wsp", this.mapToExpectedJson(serviceID, employeeID));
  }

  leave() : Observable<any>{
    return this.httpProvider.delete(this.httpProvider.ROUTES.users + "/wsp");
  }


  // @TODO remove the default userid  value; it's only used for testing purposes
  // as the token from the fake server doesn't contain userID
  private mapToExpectedJson(serviceID: number, employeeID: number){
    return {
      dienstleistung: Number(serviceID),
      mitarbeiter: Number(employeeID)
    };
  }

}
