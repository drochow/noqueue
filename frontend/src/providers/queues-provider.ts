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
/**
 * Prepares HTTP Request for the Queue functionality
 */
@Injectable()
export class QueuesProvider {

  /**
   * Dependency injection
   * @param http - angular2 http module
   * @param httpProvider - the HttpProvider which sends the requests
   * @param auth - the AuthenticationProvider
     */
  constructor(public http: Http, private httpProvider: HttpProvider, public auth: AuthenticationProvider) {
  }

  /**
   * Tries to get the current queue position of the user
   * @returns {Observable<any>} - the response of the server
     */
  getMyQueuePosition() : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/wsp";
    return this.httpProvider.get(route);
  }

  /**
   * HTTP Get request for a given queue
   * @param queueID - the id of the queue
   * @returns {Observable<any>} - the response from the server
     */
  getQueue(queueID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/queues/" + queueID;
    return this.httpProvider.get(route);
  }

  /**
   * Changes the attendance of an employee on his work
   * @param shopID - the shop where the user is working
   * @param isThere - boolean value for his attendance
   * @returns {Observable<any>} - the response from the server
     */
  changeAttendance(shopID: number, isThere: boolean) : Observable<any>{
    return this.httpProvider.put(this.httpProvider.ROUTES.shops + "/" + shopID +"/mitarbeiter", { anwesend: isThere});
  }

  /**
   * Lines up the user in a queue for a given employee
   * @param serviceID - the service
   * @param employeeID - the employee
   * @returns {Observable<any>} - the response from the server
     */
  lineup(serviceID: number, employeeID: number) : Observable<any>{
    return this.httpProvider.post(this.httpProvider.ROUTES.users + "/wsp", this.mapToExpectedJson(serviceID, employeeID));
  }

  /**
   * Leaves up a queue position
   * @returns {Observable<any>} - the response from the server
     */
  leave() : Observable<any>{
    return this.httpProvider.delete(this.httpProvider.ROUTES.users + "/wsp");
  }

  /**
   * An employee starts his work with a client
   * @param shopId - the shop where the user is working
   * @param wspId - the queue position
   * @returns {Observable<any>} - the response from the server
     */
  startWorkOn(shopId: number, wspId: number) : Observable<any>{
    return this.httpProvider.put(this.httpProvider.ROUTES.shops + '/' + shopId +  '/wsp/' + wspId, {})
  }

  /**
   * An employee stops his work with a client
   * @param shopId - the shop where the user is working
   * @param wspId - the queue position
   * @returns {Observable<any>} - the response from the server
     */
  finishWorkOn(shopId: number, wspId: number) : Observable<any>{
    return this.httpProvider.delete(this.httpProvider.ROUTES.shops + '/' + shopId +  '/wsp/' + wspId)
  }

  /**
   * Maps data that will be sent in the Body to the expected format
   * @param serviceID - the id of a service
   * @param employeeID - the user id of an employee
   * @returns {{dienstleistung: Number, mitarbeiter: Number}} - formatted json
     */
  private mapToExpectedJson(serviceID: number, employeeID: number){
    return {
      dienstleistung: Number(serviceID),
      mitarbeiter: Number(employeeID)
    };
  }

}
