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

  // 8 to 30 alphanumeric, must contain at least 1 lower case letter, 1 upper case letter, 1 digit(number) and
  // 1 special character
  password = string => /(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/.test(string);

  passwordMatching = (string1, string2) => string1 === string2;

  email = string => /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
      .test(string);

  // 1 to 50 characters
  searchTerm = string => /.{0,50}/.test(string);

  // 1 to 30 characters
  searchName = string => /^[a-zA-Z\d\.\-\_]{1,30}$/.test(string);

  // 2-50 alphanumeric + german symbols
  street = string => /[a-zA-ZäöüÄÖÜß\d \.]{2,50}/.test(string);

  // only german ZIP codes (nnnnn format)
  zip = string => /[0-9]{5}/.test(string);

  // 1-5 numeric, followed by 0 or 1 letter
  streetNumber = string => /[0-9]{1,5}[a-zA-Z]?/.test(string);

  // 2-40 letters (including german symbols)
  city = string => /[a-zA-ZüäöÄÖÜß]{2,40}/.test(string);

  // 2-30 alphanumeric, . - _ space
  shopName = string => /^[a-zA-Z\d\.\-\_\'\"\s]{2,30}$/.test(string);

  //includes +4930 1234, 030 1234, +4938293 1234, 038293 1234, 0173 12345678, +49173 12345678
  // +49301234, 0301234, +49382931234, 0382931234, 017312345678, +4917312345678
  phone = string => /^((^\(\+?\d+[\ ]*\d*\)|^\(\d+\)|^\+?\d+|^\d+)+([\-\/\ ])*(\d)+)*$/.test(string);

  // 1-50 chars from any kind
  openingHours = string => /.{1,50}/.test(string);

  // 0-250 from any kind
  serviceDescription = string => /.{1,200}/.test(string);

  // 2-40 alphanumeric (including german symbols)
  serviceType = string => /[a-zA-ZüäöÄÖÜß\d\s]{2,40}/.test(string);

  empty = (...strings) => {
    for(let value of strings){
      if (!value || value === ""){
        return true;
      }
    }
    return false;
  }

}
