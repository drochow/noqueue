import { Injectable } from '@angular/core';
import { Http, RequestOptions, Headers, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import { Storage } from '@ionic/storage';

/*
  Generated class for the HttpProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class HttpProvider {

  // Server configuration
  private localServer = "http://localhost:9000";
  private fakeServer = "http://localhost:3000";
  public workingServer = this.fakeServer;

  public ROUTES = {
    authentication: "/auth",
    users: "/anwender",
    shops: "/betrieb",
    services: "/dlts",
    queues: "/queues"
  };

  private token: string;

  constructor(public http: Http, private storage: Storage) {
    this.readToken();
  }

  readToken(){
    if(this.storage){
      this.storage.get('token').then(
        (token) => this.token = token
      );
    }
  }

  setToken(token){
    this.token = token;
  }

  requestOptions(): RequestOptions{
    let headers = new Headers({"Content-Type" : "application/json"});
    if (this.token && this.token != ""){
      headers.append("X-Auth-Token", this.token);
    }
    return new RequestOptions({headers});
  }

  get(route: string, searchOptions?: any): Observable<any>{
    let options = this.requestOptions();
    let parameters = new URLSearchParams();
    for(var attribute in searchOptions){
      parameters.set(attribute, searchOptions[attribute]);
    }
    options.search = parameters;
    console.log("GET to " + this.workingServer + route);

    return this.http.get(this.workingServer + route, options)
      .map(response => {
        return response.json();
      })
  }

  post(route: string, body: any): Observable<any>{
    let jsonBody = JSON.stringify(body);
    console.log("Sending POST request to " + route + " with body: ", jsonBody);
    return this.http.post(this.workingServer + route, jsonBody, this.requestOptions())
      .map(response => {
        try {
          return response.json();
        } catch(err) {
          return { status: response.status };
        }
      })
  }

  patch(route: string, body: any): Observable<any>{
    return this.http.patch(this.workingServer + route, JSON.stringify(body), this.requestOptions())
      .map(response => response.json())
  }

  put(route: string, body: any): Observable<any>{
    return this.http.put(this.workingServer + route, JSON.stringify(body), this.requestOptions())
      .map(response => {
        try {
          return response.json();
        } catch(err) {
          return { status: response.status };
        }
      })
  }

  delete(route: string): Observable<any>{
    return this.http.delete(this.workingServer + route, this.requestOptions())
      .map(response => {
        try {
          return response.json();
        } catch(err) {
          return { status: response.status };
        }
      })
  }
}
