import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { HttpProvider } from '../providers/http-provider';
import { Storage } from '@ionic/storage';

/*
  Generated class for the AuthenticationProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/

/**
 * This class is responsible for Authentication-based requests to the server
 */
@Injectable()
export class AuthenticationProvider {

  // authentication token
  private token: string;

  /**
   * Dependency injection:
   * @param http - the angular2 http module
   * @param httpProvider - the provider that sends http requests
   * @param storage - Local Storage (where the token is being stored)
     */
  constructor(public http: Http, private httpProvider: HttpProvider, private storage: Storage) {
    if(this.storage){
      this.storage.get('token').then(
        (token) => this.token = token
      );
    }
  }

  /**
   * Reads the token from the Storage
   * @returns {Promise<T>} - a resolved or rejected Promise after trying to read the token
     */
  asyncSetup() : Promise<any>{
    let auth = this;
    return new Promise(function(resolve, reject){
      auth.storage.keys().then(
        (keys) => {
          if(keys.indexOf('token') < 0){
            resolve();
          } else {
            auth.storage.get('token').then(
              (token) => {
                auth.token = token;
                auth.httpProvider.setToken(token);
                resolve()
              },
              () => reject()
            )
          }
        }
      );
    });
  }

  /**
   * Prepares a login request to the server
   * @param username
   * @param password
   * @returns {Promise<T>} - Promise with the answer
     */
  login(username: string, password: string) : Promise<any>{
    let auth = this;
    let body = {nutzerName: username, password: password};
    return new Promise(function(resolve, reject){
      auth.httpProvider.post(auth.httpProvider.ROUTES.authentication, body)
        .subscribe(
          (token) => {
            auth.storage.set('token', token);
            auth.token = token;
            auth.httpProvider.setToken(token);
            resolve("Logged In");
          },
          (error) => reject(error)
        )
    });
  }

  /**
   * Prepares a signup request to the server
   * @param username
   * @param email
   * @param password
   * @returns {Promise<T>} - Promise with the answer
     */
  signup(username: string, email: string, password: string) : Promise<any>{
    let auth = this;
    let body = {nutzerName: username, nutzerEmail: email, password: password};
    return new Promise(function(resolve, reject){
      auth.httpProvider.post(auth.httpProvider.ROUTES.users, body)
        .subscribe(
          (token) => {
            auth.storage.set('token', token);
            auth.token = token;
            auth.httpProvider.setToken(token);
            resolve("Signed Up");
          },
          (error) => reject(error)
        )
    })
  }

  /**
   * Responses to a logout user event
   */
  logout() : void{
    this.resetToken();
  }

  /**
   * Removes the authentication token from the local storage
   */
  resetToken() : void{
    this.token = "";
    this.storage.remove('token');
    this.httpProvider.readToken();
  }

  /**
   * Check if there is a saved token
   * @returns {boolean} - true if token found (user is logged in)
     */
  isLoggedIn() : boolean{
    return this.token !== undefined && this.token !== "";
  }

  /**
   * Returns the authentication token
   * @returns {string} - the authentication token
     */
  getToken() : string{
    return this.token;
  }

}
