import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions, RequestMethod, Request } from '@angular/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs/Observable';
import { HttpConfig } from './http-config';
import { AuthenticationProvider } from "./authentication";

/*
  Generated class for the UsersProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class UsersProvider {

  constructor(public http: Http, private httpConfig: HttpConfig, private authProvider: AuthenticationProvider) {
  }

  private getRequest(id: Number): Observable<any>{
    let config = this.httpConfig;
    return this.http.get(config.currentDB + config.ROUTES.users + "/" + id, this.authProvider.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  getUser(id: Number): Promise<any>{
    var self = this;
    return new Promise(function(succeed, fail){
      self.getRequest(id).subscribe(
        (user) => succeed(user)
      )
    });
  }

  private putRequest(id: Number, user: any): Observable<any>{
    let config = this.httpConfig;
    let body = JSON.stringify({nutzerEmail: user.email, nutzerName: user.name, password: user.password});
    return this.http.put(config.currentDB + config.ROUTES.users + "/" + id, body, this.authProvider.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  putUser(id: Number, user: any): Promise<any>{
    var self = this;
    return new Promise(function(succeed, fail){
      self.putRequest(id, user).subscribe(
        () => succeed("updated")
      )
    });
  }

  private patchRequest(id: Number, user: any): Observable<any>{
    let config = this.httpConfig;
    let body = JSON.stringify({nutzerEmail: user.email, nutzerName: user.name, password: user.password});
    return this.http.patch(config.currentDB + config.ROUTES.users + "/" + id, body, this.authProvider.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  patchUser(id: Number, user: any): Promise<any>{
    var self = this;
    return new Promise(function(succeed, fail){
      self.patchRequest(id, user).subscribe(
        () => succeed("updated")
      )
    });
  }

  private deleteRequest(id: Number): Observable<any>{
    let config = this.httpConfig;
    return this.http.delete(config.currentDB + config.ROUTES.users + "/" + id, this.authProvider.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  deleteUser(id: Number): Promise<any>{
    var self = this;
    return new Promise(function(succeed, fail){
      self.deleteRequest(id).subscribe(
        () => succeed("deleted")
      )
    });
  }
}
