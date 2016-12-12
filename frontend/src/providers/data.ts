import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
// import { Store } from '../providers/store';
import { HttpService } from '../providers/http-service';

/*
  Generated class for the Data provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/

@Injectable()
export class ServicesData {

  services = new Array();

  constructor(public http: Http, public httpService: HttpService) {

  }

  public getAllServices(){
    this.httpService.getAllServices().subscribe(
      (services) => this.services = services);
    console.log("---" + this.services);
    return this.services;
  }

  public getService(id: number){
    this.services.forEach(function(s){
      if(s.getId() === id) return s;
    })
  };

}


