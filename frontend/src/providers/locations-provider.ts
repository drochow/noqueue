import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Geolocation } from 'ionic-native';

/*
  Generated class for the LocationsProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class LocationsProvider {
  
  constructor(public http: Http) {
  }

  getUserLocation(): Promise<any>{
    let latitude: number;
    let longitude: number;

    return new Promise(function(resolve, reject){
      Geolocation.getCurrentPosition()
        .then(
          (position) => {
            latitude = position.coords.latitude;
            longitude = position.coords.longitude;
            resolve({latitude: latitude, longitude: longitude});
          },
          (error) => {
            latitude = 52.545433;
            longitude = 13.354636;
            resolve({latitude: latitude, longitude: longitude});
          }
        )
    });
  }

}
