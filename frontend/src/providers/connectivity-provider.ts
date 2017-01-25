import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Network } from 'ionic-native';
import { Platform } from 'ionic-angular';

/*
  Generated class for the ConnectivityProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/

declare var Connection;
declare var navigator: any;

@Injectable()
export class ConnectivityProvider {

  onDevice: boolean;

  constructor(public http: Http, public platform: Platform) {
    this.onDevice = this.platform.is('cordova');
  }

  isOnline() : boolean {
    console.log(Network.downlinkMax);
    if(this.onDevice && Network.type){
      return Network.type.toLowerCase() !== "none";
    } else {
      return navigator.onLine;
    }
  }

}
