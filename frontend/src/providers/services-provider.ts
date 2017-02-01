import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { HttpProvider } from '../providers/http-provider';

/*
  Generated class for the ServicesProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
/**
 * Prepares HTTP Request for the Services functionality
 */
export class ServicesProvider {

  /**
   * Dependency injection
   * @param http - angular2 standard http module
   * @param httpProvider - the HttpProvider that actually sends the requests
     */
  constructor(public http: Http, private httpProvider: HttpProvider) {
  }

  /**
   * Gets the services provided by a given shop
   * @param shopID - the shop
   * @returns {Observable<any>} - the response from the server
     */
  getServicesFor(shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung";
    return this.httpProvider.get(route);
  }

  /**
   * Gets all possible service types
   * @returns {Observable<any>} - the response from the server
     */
  getAllServiceTypes() : Observable<any>{
    return this.httpProvider.get(this.httpProvider.ROUTES.services);
  }

  /**
   * Creates a service for a given shop
   * @param shopID - the shop
   * @param service - the service data
   * @returns {Observable<any>} - the response from the server
     */
  createService(shopID: number, service: any) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung";
    service.dauer = service.dauer*60; //backend is saving in seconds
    let body = this.mapToExpectedJson(service);
    return this.httpProvider.post(route, body);
  }

  /**
   * Edits a service for a given shop
   * @param shopID - the shop
   * @param serviceID - the service id
   * @param service - updated service data
   * @returns {Observable<any>} - the response from the server
     */
  editService(shopID: number, serviceID: number, service: any) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung/" + serviceID;
    let body = this.mapToExpectedJson(service);
    service.dauer = service/60; //backend is saving in seconds
    return this.httpProvider.put(route, body);
  }

  /**
   * Deletes a service
   * @param shopID - the shop
   * @param serviceID - the service id
   * @returns {Observable<any>} - the response from the server
     */
  deleteService(shopID: number, serviceID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung/" + serviceID;
    return this.httpProvider.delete(route);
  }

  /**
   * Maps data that will be sent in the requests Body to the expected format
   * @param service - the service data to be sent
   * @returns {{dauer: any, name: any, kommentar: any}} - formatted json
     */
  private mapToExpectedJson(service: any){
    return {
      dauer: service.duration,
      name: service.type,
      kommentar: service.description
    }
  }
}
