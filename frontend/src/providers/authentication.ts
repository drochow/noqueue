import { Injectable } from '@angular/core';
import { Http, RequestOptions } from '@angular/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs/Observable';
import { HttpConfig } from './http-config';
import { Storage } from '@ionic/storage';

/*
  Generated class for the Authentication provider.
  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class AuthenticationProvider {

  private token: string;
  userId: number;

  constructor(public http: Http, private httpConfig: HttpConfig, private storage: Storage) {
    if(storage !== undefined){
      storage.get('token').then(
        (token) => {
          console.log('Reading token from local storage: ', token);
          this.token = token;
        }
      )
    }
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

  // @TODO - false request?
  signIn(username: String, password: String): Promise<any>{
    var auth = this;
    return new Promise(function(succeed, fail){
      auth.signInRequest(username, password).subscribe(
        (token) => {
          auth.storage.set('token', token);
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
        (data) => {
          //@TODO - discuss with backend team if signUp should return a token
          // auth.storage.set('token', token);
          //auth.token = token;
          console.log("Response from server on signUp:", data);
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
    console.log("Logged in? - token : " + this.getToken());
    return this.getToken() !== undefined && this.getToken() !== "" && this.getToken() !== null
  }

  getToken() : String {
    return this.token;
  }

  getUserId() : number{
    return this.userId || 0;
  }

  private resetToken(){
    this.token = "";
    this.storage.remove('token');
  }

}
