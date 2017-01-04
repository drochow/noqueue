import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { HttpProvider } from '../providers/http-provider';
import { AuthenticationProvider } from '../providers/authentication-provider';

/*
  Generated class for the UsersProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class UsersProvider {

  constructor(public http: Http, private httpProvider: HttpProvider, private auth: AuthenticationProvider) {
  }

  getUsersWithName(name: string){
    let searchOptions = { q: name };
    return this.httpProvider.get(this.httpProvider.ROUTES.users, searchOptions);
  }

  getMe(){
    return this.httpProvider.get(this.httpProvider.ROUTES.users + "/" + this.auth.getUserId());
  }

  changeProfileInfo(data: any){
    let body = {
      nutzerName: data.username,
      nutzerEmail: data.email,
      adresse: {
        plz: data.zip,
        stadt: data.city,
        strasse: data.street,
        hausNummer: data.streetNr
      }
    };
    return this.httpProvider.patch(this.httpProvider.ROUTES.users + "/" + this.auth.getUserId(), body);
  }

  changePassword(password: string){
    return this.httpProvider.patch(this.httpProvider.ROUTES.users + "/" + this.auth.getUserId(), {password});
  }
}
