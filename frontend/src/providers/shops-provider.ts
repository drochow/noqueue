import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs/Observable';
import { HttpProvider } from '../providers/http-provider';
import { AuthenticationProvider } from '../providers/authentication-provider';

/*
  Generated class for the ShopsProvider provider.

  See https://angular.io/docs/ts/latest/guide/dependency-injection.html
  for more info on providers and Angular 2 DI.
*/
@Injectable()
export class ShopsProvider {

  constructor(public http: Http, private httpProvider: HttpProvider, private auth: AuthenticationProvider) {
  }

  // @TODO - see getNearbyShops
  getAllShops() : Observable<any>{
    return this.httpProvider.get(this.httpProvider.ROUTES.shops);
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

  getManagers(shopID) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/leiter";
    return this.httpProvider.get(route);
  }

  hireEmployee(userID, shopID, anwesend) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/mitarbeiter";
    return this.httpProvider.post(route, {anwenderId: userID, betriebId: shopID, anwsend: anwesend || false});
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
    this.fireEmployee(userID, shopID).
        subscribe(
        () => this.hireManager(userId, shopID)
    );
  }

  demoteManager(userID, shopID){
    this.fireManager(userID, shopID).
        subscribe(
        () => this.hireEmployee(userID, shopID)
    );
  }

  private mapToExpectedJson(shop: any){
    return {
      name: shop.name,
      adresse: {
        strasse: shop.street,
        hausNummer: shop.streetNr,
        plz: shop.zip,
        stadt: shop.city
      },
      tel: shop.phone,
      kontaktEmail: shop.email,
      oeffnungszeiten: shop.openingHours
    }
  }

}
