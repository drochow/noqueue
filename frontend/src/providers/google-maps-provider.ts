import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { ConnectivityProvider } from '../providers/connectivity-provider';

/*
  Generated class for the GoogleMapsProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/

/**
 * variables from third-party-libraries
 * must be declared with the 'declare' keyword
 */
declare var google;

/**
 * Gets access to Google Maps
 */
@Injectable()
export class GoogleMapsProvider {

  mapElement: any;
  map: any;
  mapInitialised: boolean = false;
  markers: any = [];
  latitude: number;
  longitude: number;

  /**
   * Dependency injection:
   * @param http - Angular2 HTTP Module
   * @param connectivity - ConnectivityProvider
     */
  constructor(public http: Http, public connectivity: ConnectivityProvider) {
  }

  /**
   * Prepares a request to Google Maps
   * @param mapElement - the DOM element that will be updated
   * @param latitude - the needed latitute
   * @param longitude - the needed longitude
   * @returns {Promise<any>} - resends the Promise answer of the called method
     */
  init(mapElement: any, latitude: number, longitude: number) : Promise<any>{
    this.mapElement = mapElement;
    this.latitude = latitude;
    this.longitude = longitude;

    return this.loadGoogleMaps();
  }

  /**
   * Connects to Google Maps SDK
   * @returns {Promise<T>}
     */
  loadGoogleMaps() : Promise<any>{
    return new Promise((resolve) => {
      if(typeof google == "undefined" || typeof google.maps == "undefined"){
        // google maps SDK must be loaded

        if(this.connectivity.isOnline()){
          window['mapInit'] = () => {
            this.initMap();
          }

          let script = document.createElement("script");
          script.id = "googleMaps";
          script.src = "http://maps.google.com/maps/api/js?callback=mapInit";

          document.body.appendChild(script);
        }
      } else {
        if (this.connectivity.isOnline()) {
          this.initMap();
        }
      }
    })
  }

  /**
   * Initializes a map
   */
  initMap() : void{
    this.mapInitialised = true;
    let latLng = new google.maps.LatLng(this.latitude, this.longitude);
    let mapOptions = {
      center: latLng,
      zoom: 15,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    this.map = new google.maps.Map(this.mapElement, mapOptions);
    this.addMarker();
  }

  /**
   * Adds a marker to the map at the given coordinates
   */
  addMarker() : void{
    let latLng = new google.maps.LatLng(this.latitude, this.longitude);
    let marker = new google.maps.Marker({
      map: this.map,
      animation: google.maps.Animation.DROP,
      position: latLng
    });
    this.markers.push(marker);
  }

}
