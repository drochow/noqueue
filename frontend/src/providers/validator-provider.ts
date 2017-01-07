import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';

/*
  Generated class for the ValidatorProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class ValidatorProvider {

  constructor(public http: Http) {
  }

  // 6 to 30 alphanumeric + special characters - _ .
  username = string => /^[a-zA-Z\d\.\-\_]{6,30}$/.test(string);

  //@TODO find a better expression for password
  // 6 to 30 alphanumeric
  password = string => /^[a-zA-Z\d\.]{6,30}$/.test(string);

  passwordMatching = (string1, string2) => string1 === string2;

  // @TODO check if you can use the HTML5 email validation
  // simple email xxx@yyy.zzz
  email = string => /.+\@.+\..+/.test(string);

  // 0 to 50 characters
  searchTerm = string => /.{1,50}/.test(string);

  // 2-50 alphanumeric + german symbols
  street = string => /[a-zA-ZäöüÄÖÜß\d \.]{2,50}/.test(string);

  // only german ZIP codes (nnnnn format)
  zip = string => /[0-9]{5}/.test(string);

  // 1-5 numeric, followed by 0 or 1 letter
  streetNumber = string => /[0-9]{1,5}[a-zA-Z]?/.test(string);

  // 2-40 letters (including german symbols)
  city = string => /[a-zA-ZüäöÄÖÜß]{2,40}/.test(string);

  // 0-250 from any kind
  serviceDescription = string => /.{1,200}/.test(string);

  // 2-40 alphanumeric (including german symbols)
  serviceType = string => /[a-zA-ZüäöÄÖÜß\d]{2,40}/.test(string);

  empty = (...strings) => {
    for(let value of strings){
      if (!value || value === ""){
        return true;
      }
    }
    return false;
  }

}
