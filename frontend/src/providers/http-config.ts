import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions} from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';
import {ErrorObservable} from 'rxjs/observable/ErrorObservable';


@Injectable()
export class HttpConfig {

  public servicesDB = "https://noqueue-dummy.firebaseio.com/";
  public usersDB = "https://noqueue-dummy-users.firebaseio.com/";
  public localDB2 = "/api";
  public localDB = "http://localhost:9000";
  public currentDB = this.localDB;

  public ROUTES = {
    signin: "/signin",
    signup: "/signup",
    token: "/testSignedIn",
    users: "/anwender",
    services: "/dlts"
  };

  constructor(public http: Http) {
  }

  public requestOptions(): RequestOptions{
    let headers = new Headers({"Content-Type" : "application/json"});
    return new RequestOptions({headers: headers});
  }

  public handleData(res: Response){
    return res.json();
  }

  public handleError(error: Response | any): ErrorObservable{
    const message = error.json();
    console.log("HTTP Error: ", message);
    return Observable.throw(message);
  }

}
