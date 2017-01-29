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
  
  getMyQueuePosition() : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/wsp";
    return this.httpProvider.get(route);
  }

  getQueue(queueID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/queues/" + queueID;
    return this.httpProvider.get(route);
  }

  changeAttendance(shopID: number, isThere: boolean) : Observable<any>{
    return this.httpProvider.put(this.httpProvider.ROUTES.shops + "/" + shopID +"/mitarbeiter", { anwesend: isThere});
  }

  lineup(serviceID: number, employeeID: number) : Observable<any>{
    return this.httpProvider.post(this.httpProvider.ROUTES.users + "/wsp", this.mapToExpectedJson(serviceID, employeeID));
  }

  leave() : Observable<any>{
    return this.httpProvider.delete(this.httpProvider.ROUTES.users + "/wsp");
  }

  startWorkOn(shopId: number, wspId: number) : Observable<any>{
    return this.httpProvider.put(this.httpProvider.ROUTES.shops + '/' + shopId +  '/wsp/' + wspId, {})
  }

  finishWorkOn(shopId: number, wspId: number) : Observable<any>{
    return this.httpProvider.delete(this.httpProvider.ROUTES.shops + '/' + shopId +  '/wsp/' + wspId)
  }

  private mapToExpectedJson(serviceID: number, employeeID: number){
    return {
      dienstleistung: Number(serviceID),
      mitarbeiter: Number(employeeID)
    };
  }

}
