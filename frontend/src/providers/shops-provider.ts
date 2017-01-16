import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { HttpProvider } from '../providers/http-provider';
import { AuthenticationProvider } from '../providers/authentication-provider';
import { LocationsProvider } from '../providers/locations-provider';

/*
  Generated class for the ShopsProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class ShopsProvider {

  constructor(public http: Http, private httpProvider: HttpProvider, private auth: AuthenticationProvider, public locations: LocationsProvider) {
  }

  // @TODO - see getNearbyShops
  getAllShops() : Observable<any>{
    return this.httpProvider.get(this.httpProvider.ROUTES.shops);
  }

  // getShops(limit: number, page: number, filter: string, radius: number): Promise<any>{
  //   var self = this;
  //   return new Promise(function(resolve, reject){
  //     self.locations.getUserLocation()
  //       .then(
  //         (location) => {
  //           let lat = location.latitude;
  //           let long = location.longitude;
  //           var searchOptions = {size: limit, page: page, q: filter, radius: radius, lat: lat, long: long};
  //           if(!limit) delete searchOptions.size;
  //           if(!page) delete searchOptions.page;
  //           if(!filter) delete searchOptions.q;
  //           if(!radius) delete searchOptions.radius;
  //           console.log("GET shops with searchOptions: ", searchOptions);
  //           resolve(self.httpProvider.get(self.httpProvider.ROUTES.shops, searchOptions));
  //         },
  //         (error) => {
  //           console.log("Couldn't read user Location: ", error);
  //           reject(error);
  //         }
  //       );
  //   });
  // }

  getShops(limit: number, page: number, filter: string, radius: number, lat: number, long: number): Observable<any>{
    let searchOptions = {size: limit, page: page, q: filter, radius: radius, lat: lat, long: long};
    console.log("SEARCH OPTIONS: ", searchOptions);
    return this.httpProvider.get(this.httpProvider.ROUTES.shops, searchOptions);
  }

  // @TODO - merge this method and the above one into one (getShops, with get params: size,page,q,radius [see rest-api.md])
  getNearbyShops(limit: number, page: number, filter: string) : Observable<any>{
    let searchOptions = { size: limit, page, q: filter };
    return this.httpProvider.get(this.httpProvider.ROUTES.shops, searchOptions);
  }

  getMyShops() : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/betrieb";
    return this.httpProvider.get(route);
  }

  getShop(id: any) : Observable<any>{
    return this.httpProvider.get(this.httpProvider.ROUTES.shops + "/" + id);
  }

  createShop(shop: any) : Observable<any>{
    let body = this.mapToExpectedJson(shop);
    return this.httpProvider.post(this.httpProvider.ROUTES.shops, body);
  }

  editShop(shopID: any, shop: any) : Observable<any>{
    let body = this.mapToExpectedJson(shop);
    return this.httpProvider.put(this.httpProvider.ROUTES.shops + "/" + shopID, body);
  }

  getEmployees(shopID) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/mitarbeiter";
    return this.httpProvider.get(route);
  }

  getNextAvailableSlots(shopID) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung/mitarbeiter";
    return this.httpProvider.get(route);
  }

  getManagers(shopID) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/leiter";
    return this.httpProvider.get(route);
  }

  hireEmployee(userID, shopID, anwesend) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/mitarbeiter";
    return this.httpProvider.post(route, {anwenderId: userID, betriebId: shopID, anwesend: anwesend || false});
  }

  hireManager(userID, shopID, anwesend) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/leiter";
    return this.httpProvider.post(route, {anwenderId: userID, betriebId: shopID, anwsend: anwesend || false});
  }

  fireEmployee(userID, shopID) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/mitarbeiter/" + userID;
    return this.httpProvider.delete(route);
  }

  fireManager(userID, shopID) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/leiter/" + userID;
    return this.httpProvider.delete(route);
  }

  promoteEmployee(userID, shopID){
    let self = this;
    return new Promise(function(resolve, reject){
      self.fireEmployee(userID, shopID)
        .subscribe(
          () => self.hireManager(userID, shopID, false)
            .subscribe(
              () => resolve(),
              (error) => reject(error)
            ),
          (error) => reject(error)
        )
    });
    //
    //
    // this.fireEmployee(userID, shopID).
    //     subscribe(
    //     () => {
    //       return this.hireManager(userID, shopID, false);
    //     }
    // );
  }

  demoteManager(userID, shopID){
    let self = this;
    return new Promise(function(resolve, reject){
      self.fireManager(userID, shopID)
        .subscribe(
          () => self.hireEmployee(userID, shopID, false)
            .subscribe(
              () => resolve(),
              (error) => reject(error)
            ),
          (error) => reject(error)
        )
    });
  }

  private mapToExpectedJson(shop: any){
    return {
      name: shop.name,
      adresse: {
        strasse: shop.address.street,
        hausNummer: shop.address.streetNr,
        plz: shop.address.zip,
        stadt: shop.address.city
      },
      tel: shop.phone,
      kontaktEmail: shop.email,
      oeffnungszeiten: shop.openingHours
    }
  }

}
