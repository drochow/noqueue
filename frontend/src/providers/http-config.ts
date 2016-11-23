import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions, RequestMethod, Request } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';


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
    users: "/anwender"
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

  public handleError(error: Response | any){
    const message = error.json();
    console.log("HTTP Error: ", message);
    return Observable.throw(message);
  }

}
