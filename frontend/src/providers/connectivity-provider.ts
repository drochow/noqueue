import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Network } from 'ionic-native';
import { Platform } from 'ionic-angular';
import { ToastController } from 'ionic-angular';

/*
  Generated class for the ConnectivityProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/

/**
 * Variables from third-party-libraries
 * must be declared with the 'declare' keyword
 */
declare var Connection;
declare var navigator: any;

/**
 * Checks if the user's device has network connectivity
 */
@Injectable()
export class ConnectivityProvider {

  onDevice: boolean;

  /**
   * Dependency injection
   * @param http - Angular2 Http Module
   * @param platform - Cordovas Platform Plugin
   * @param toast - ToastController
     */
  constructor(public http: Http, public platform: Platform, private toast: ToastController) {
    this.onDevice = this.platform.is('cordova');
  }

  /**
   * checks if the user is online
   * @returns {boolean} - user online
     */
  isOnline() : boolean {
    if(this.onDevice && Network.type){
      return Network.type.toLowerCase() !== "none";
    } else {
      return navigator.onLine;
    }
  }

  /**
   * Checks the network connectivity and presents a Toast element
   * informing the user about this problem
   */
  checkNetworkConnection(): void{
    if(!this.isOnline()){
      let toast = this.toast.create({
        message: 'Please check your network connection.',
        duration: 3000
      });
      toast.present();
    }
  }

}
