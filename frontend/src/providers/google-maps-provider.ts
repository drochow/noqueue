import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { ConnectivityProvider } from '../providers/connectivity-provider';

/*
  Generated class for the GoogleMapsProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/

declare var google;

@Injectable()
export class GoogleMapsProvider {

  mapElement: any;
  map: any;
  mapInitialised: boolean = false;
  markers: any = [];
  latitude: number;
  longitude: number;

  constructor(public http: Http, public connectivity: ConnectivityProvider) {
  }

  init(mapElement: any, latitude: number, longitude: number) : Promise<any>{
    this.mapElement = mapElement;
    this.latitude = latitude;
    this.longitude = longitude;

    return this.loadGoogleMaps();
  }

  loadGoogleMaps() : Promise<any>{
    return new Promise((resolve) => {
      if(typeof google == "undefined" || typeof google.maps == "undefined"){
        // google maps SDK must be loaded
        this.disableMap();

        if(this.connectivity.isOnline()){
          window['mapInit'] = () => {
            this.initMap();
            this.enableMap();
          }

          let script = document.createElement("script");
          script.id = "googleMaps";
          script.src = "http://maps.google.com/maps/api/js?callback=mapInit";

          document.body.appendChild(script);
        }
      } else {
        if(this.connectivity.isOnline()){
          this.initMap();
          this.enableMap();
        } else {
          this.disableMap();
        }
      }
    })
  }

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
