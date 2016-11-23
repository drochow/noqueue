import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions, RequestMethod, Request } from '@angular/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs/Observable';
import { HttpConfig } from './http-config';

/*
  Generated class for the Authentication provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class AuthenticationProvider {

  private token: string;

  constructor(public http: Http, private httpConfig: HttpConfig) {
  }

  requestOptionsWithToken(): RequestOptions{
    var requestOptions = this.httpConfig.requestOptions();
    requestOptions.headers.append('X-Auth-Token', this.token);
    return requestOptions;
  }

  private signInRequest(username: String, password: String): Observable<any>{
    let config = this.httpConfig;
    let body = JSON.stringify({nutzerName: username, password: password});
    // return this.http.get(config.currentDB + config.ROUTES.signin)
    return this.http.post(config.currentDB + config.ROUTES.signin, body, config.requestOptions())
      .map(config.handleData)
      .catch(config.handleError);
  }

  signIn(username: String, password: String): Promise<any>{
    var auth = this;
    return new Promise(function(succeed, fail){
      auth.signInRequest(username, password).subscribe(
        (token) => {
          auth.token = token;
          succeed();
        }
      );
    });
  }

  private signUpRequest(username: String, password: String, email: String): Observable<any>{
    let body = JSON.stringify({nutzerName: username, password: password, nutzerEmail: email});
    let config = this.httpConfig;

    return this.http.post(config.currentDB + config.ROUTES.signup, body, config.requestOptions())
      .map(config.handleData)
      .catch(config.handleError);
  }

  signUp(username: String, password: String, email: String): Promise<any>{
    var auth = this;
    return new Promise(function(succeed, fail){
      //@TODO - validate user input
      auth.signUpRequest(username, password, email).subscribe(
        (token) => {
          auth.token = token;
          succeed("signed up");
        }
      )
    });
  }

  private checkTokenRequest(): Observable<any>{
    let config = this.httpConfig;

    return this.http.get(config.currentDB + config.ROUTES.token, this.requestOptionsWithToken())
      .map(config.handleData)
      .catch(config.handleError);
  }

  checkToken(): Promise<any>{
    var auth = this;
    return new Promise(function(succeed, fail){
      auth.checkTokenRequest().subscribe(
        (info) => succeed("logged in")
      )
    });
  }

  logOut(){
    this.resetToken();
  }

  isLoggedIn() : boolean {
    return this.getToken() !== undefined && this.getToken() !== ""
  }

  getToken() : String {
    return this.token;
  }

  private resetToken(){
    this.token = "";
  }

}
