import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
// import { Store } from '../providers/store';
// import { User } from '../providers/user';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';

@Injectable()
export class HttpService {

  private servicesDB = "https://noqueue-dummy.firebaseio.com/";
  private usersDB = "https://noqueue-dummy-users.firebaseio.com/";
  private testDB = "http://localhost:9000";
  private token: string;

  constructor(public http: Http) {
  }

  // Auth
  signIn(user: any, route: String){
    let headers = new Headers({"Content-Type" : "application/json"});
    let options = new RequestOptions({headers: headers});
    let body = JSON.stringify(user);

    let action = function(): Observable<any>{
      return this.http.post(this.testDB + route, body, options)
        .map(this.extractJson)
        .catch(this.handleError);
    }

    action().subscribe(
      (token) => this.token = token
    )
  }

  getToken(){
    return this.token
  }

  // old requests
  fetchToken(): Observable<any>{
    return this.http.get(this.testDB + "/signin")
      .map(this.extractJson)
      .catch(this.handleError);
  }

  // getToken(){
  //   this.fetchToken().subscribe((token)=>{
  //     this.token = token
  //   });
  //   return this.token;
  // }

  testSignIn(){
    let header = new Headers();
    header.append('Accept', 'application/json');
    header.append('X-Auth-Token', this.token || "");
    let res = this.http.get(this.testDB + "/testSignedIn", { headers: header})
      .map(this.extractJson)
      .catch(this.handleError)
    res.subscribe(
      (info) => console.log(info)
    )
  }



  getAllServices(): Observable<any> {
    return this.http.get(this.servicesDB + ".json")
      .map(this.extractJson)
      .catch(this.handleError);
  }

  getServiceWithId(id: number): Observable<any>{
    return this.http.get(this.servicesDB + (id-1) + ".json")
      .map(this.extractJson)
      .catch(this.handleError);
  }

  getAllUsers(): Observable<any> {
    return this.http.get(this.usersDB + ".json")
      .map(this.extractJson)
      .catch(this.handleError);
  }

  addNewUser(username: string, password: string, email: string): Observable<any>{
    let headers = new Headers({"Content-Type" : "application/json"});
    let options = new RequestOptions({headers: headers});
    let user = {
      username: username,
      password: password,
      email: email
    };

    // var requestoptions = new RequestOptions({
    //   method: RequestMethod.Post,
    //   url: this.usersDB,
    //   headers: headers,
    //   body: JSON.stringify(user)
    // });

    console.log("test");
    return this.http.post(this.usersDB, JSON.stringify(user), options)
      .catch(this.handleError);

    // return this.http.request(new Request(requestoptions))
    //   .map(this.extractJson)
    //   .catch(this.handleError);
  }

  private extractJson(res: Response){
    return res.json();
  }

  private handleError(error: Response | any){
    const message = error.json();
    console.log(message);
    return Observable.throw(message);
  }

}
