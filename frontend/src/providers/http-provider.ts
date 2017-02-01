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
/**
 * Sends HTTP Provider to a server
 */
@Injectable()
export class HttpProvider {

  // Server configuration
  private localServer = "http://localhost:9000";
  public workingServer = this.localServer;
  public ROUTES = {
    authentication: "/auth",
    users: "/anwender",
    shops: "/betrieb",
    services: "/dlt",
    queues: "/queues"
  };

  // the authentication token
  // needed for the Requests Headers
  private token: string;

  /**
   * Dependency injection:
   * @param http - Angular2 Http module
   * @param storage - Local Storage
     */
  constructor(public http: Http, private storage: Storage) {
    this.readToken();
  }

  /**
   * Tries to read the token from the local storage
   */
  readToken() : void{
    if(this.storage){
      this.storage.get('token').then(
        (token) => this.token = token
      );
    }
  }

  /**
   * An interface for setting the token directly from other classes
   * needed if there are problems while reading the token from local storage
   * @param token
     */
  setToken(token) : void{
    this.token = token;
  }

  /**
   * Prepares the request options for a HTTP request
   * @returns {RequestOptions}
     */
  requestOptions() : RequestOptions{
    let headers = new Headers({"Content-Type" : "application/json"});
    if (this.token && this.token != ""){
      headers.append("X-Auth-Token", this.token);
    }
    return new RequestOptions({headers});
  }

  /**
   * Sends a HTTP GET request
   * @param route
   * @param searchOptions
   * @returns {Observable<R>} - the response from the server
     */
  get(route: string, searchOptions?: any) : Observable<any>{
    let options = this.requestOptions();
    let parameters = new URLSearchParams();
    for(var attribute in searchOptions){
      parameters.set(attribute, searchOptions[attribute]);
    }
    options.search = parameters;
    return this.http.get(this.workingServer + route, options)
      .map(response => this.responseToJson(response));
  }

  /**
   * Sends a HTTP POST request
   * @param route
   * @param body
   * @returns {Observable<R>} - the response from the server
     */
  post(route: string, body: any) : Observable<any>{
    let jsonBody = JSON.stringify(body);
    return this.http.post(this.workingServer + route, jsonBody, this.requestOptions())
      .map(response => this.responseToJson(response));
  }

  /**
   * Sends a HTTP PATCH request
   * @param route
   * @param body
   * @returns {Observable<R>} - the response from the server
     */
  patch(route: string, body: any) : Observable<any>{
    return this.http.patch(this.workingServer + route, JSON.stringify(body), this.requestOptions())
      .map(response => this.responseToJson(response));
  }

  /**
   * Sends a HTTP PUT request
   * @param route
   * @param body
   * @returns {Observable<R>} - the response from the server
     */
  put(route: string, body: any) : Observable<any>{
    return this.http.put(this.workingServer + route, JSON.stringify(body), this.requestOptions())
      .map(response => this.responseToJson(response));
  }

  /**
   * Sends a HTTP Delete request
   * @param route
   * @returns {Observable<R>} - the response from the server
     */
  delete(route: string) : Observable<any>{
    return this.http.delete(this.workingServer + route, this.requestOptions())
      .map(response => this.responseToJson(response));
  }

  /**
   * Transfers the Response from the server to JSON
   * @param response
   * @returns {any} - JSON response
     */
  responseToJson(response) : any {
    try {
      return response.json();
    } catch(err) {
      return { status: response.status };
    }
  }


}
