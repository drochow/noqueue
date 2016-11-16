import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';

/*
  Generated class for the Login provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/

@Injectable()
export class Login {

  users: User[];

  constructor(public http: Http) {
    this.users = new Array();
    this.createUsers();
  }

  private createUsers(){
    this.users.push(new User("johndoe","johndoe@example.com","johndoe"));
  }

  public login(name: string, password: string){
    this.users.forEach(function(u){
      if(u.getUsername() === name){
        if(u.getPassword() === password){
          u.logged(true);
          return true;
        }
      }
    });
  }

  public signup(name: string, email: string, password: string){
    this.users.push(new User(name,email,password));
  }

  public isLogged(){
    this.users.forEach(function(u){
      if(u.getLoggedIn()) return true;
    });
    return false;
  }

  public loggedUser(){
    this.users.forEach(function(u){
      if(u.getLoggedIn()) return u;
    });
  }

}

export class User{
  username: string;
  email: string;
  password: string;
  loggedIn: boolean;

  constructor(username: string, email:string, password:string){
    this.username = username;
    this.email = email;
    this.password = password;
    this.loggedIn = false;
  }

  public getUsername(){
    return this.username;
  }

  public getEmail(){
    return this.email;
  }

  public getPassword(){
    return this.password;
  }

  public getLoggedIn(){
    return this.loggedIn;
  }

  public logged(state: boolean){
    this.loggedIn = state;
  }
}
