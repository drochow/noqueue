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
export class ServicesProvider {

  constructor(public http: Http, private httpProvider: HttpProvider) {
  }

  getQueueFor(shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/ws";
    return this.httpProvider.get(route);
  }

  getServicesFor(shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung";
    return this.httpProvider.get(route);
  }

  // @TODO obsolete method
  getService(serviceID: number, shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung/" + serviceID;
    return this.httpProvider.get(route);
  }

  // @TODO
  getNextTimeSlots(serviceID: number, shopID: number){
    // ...
  }

  getAllServiceTypes() : Observable<any>{
    return this.httpProvider.get(this.httpProvider.ROUTES.services);
  }

  createService(shopID: number, service: any) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung";
    service.dauer = service.dauer*60; //backend is saving in seconds
    let body = this.mapToExpectedJson(service);
    return this.httpProvider.post(route, body);
  }

  editService(shopID: number, serviceID: number, service: any) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung/" + serviceID;
    let body = this.mapToExpectedJson(service);
    service.dauer = service/60; //backend is saving in seconds
    return this.httpProvider.put(route, body);
  }

  private mapToExpectedJson(service: any){
    return {
      dauer: service.duration,
      name: service.type,
      kommentar: service.description
    }
  }
}
