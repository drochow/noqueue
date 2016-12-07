import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs/Observable';
import {AuthenticationProvider} from "./authentication";
import {HttpConfig} from "./http-config";

/*
  Generated class for the ServicesProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class ServicesProvider {

  constructor(public http: Http, private httpConfig: HttpConfig, private authProvider: AuthenticationProvider) {
  }

  private getAllRequest(): Observable<any>{
    let config = this.httpConfig;
    return this.http.get(config.currentDB + config.ROUTES.services, this.authProvider.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  getAllServices(): Promise<any>{
    var self = this;
    return new Promise(function(succeed, fail){
      self.getAllRequest().subscribe(
        (services) => succeed(services)
      )
    });
  }

  private getRequest(id: Number): Observable<any>{
    let config = this.httpConfig;
    return this.http.get(config.currentDB + config.ROUTES.services + "/" + id, this.authProvider.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  getService(id: Number): Promise<any>{
    var self = this;
    return new Promise(function(succeed, fail){
      self.getRequest(id).subscribe(
        (service) => succeed(service)
      )
    });
  }



  private putRequest(id: Number, service: any): Observable<any>{
    let config = this.httpConfig;
    let body = JSON.stringify(service);
    return this.http.put(config.currentDB + config.ROUTES.services + "/" + id, body, this.authProvider.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  putService(id: Number, service: any): Promise<any>{
    var self = this;
    return new Promise(function(succeed, fail){
      self.putRequest(id, service).subscribe(
        () => succeed("updated")
      )
    });
  }

  private deleteRequest(id: Number): Observable<any>{
    let config = this.httpConfig;
    return this.http.delete(config.currentDB + config.ROUTES.services + "/" + id, this.authProvider.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  deleteService(id: Number): Promise<any>{
    var self = this;
    return new Promise(function(succeed, fail){
      self.deleteRequest(id).subscribe(
        () => succeed("deleted")
      )
    });
  }

  private postRequest(service: any): Observable<any>{
    let body = JSON.stringify(service);
    let config = this.httpConfig;
    return this.http.post(config.currentDB + config.ROUTES.services, body, config.requestOptions())
      .map(config.handleData)
      .catch(config.handleError);
  }

  postNewService(service: any): Promise<any>{
    var auth = this;
    return new Promise(function(succeed, fail){
      //@TODO - validate user input
      auth.postRequest(service).subscribe(
        (data) => {
          succeed(data)
        }
      )
    });
  }



}
