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
/**
 * Prepares HTTP Request for the Shops functionality
 */
export class ShopsProvider {

  /**
   * Dependency injection
   * @param http - angular2 http module
   * @param httpProvider - the http provider that actually sends the requests
   * @param auth - authentication provider
   * @param locations - Locations Provider
     */
  constructor(public http: Http, private httpProvider: HttpProvider, private auth: AuthenticationProvider, public locations: LocationsProvider) {
  }

  /**
   * Gets a list of a shop with given:
   * @param limit
   * @param page
   * @param filter
   * @param radius
   * @param lat
   * @param long
     * @returns {Observable<any>} - the response from the server
     */
  getShops(limit: number, page: number, filter: string, radius: any, lat: number, long: number): Observable<any>{
    let searchOptions = {size: limit, page: page, q: filter, radius: radius, lat: lat, long: long};
    if(!radius) delete searchOptions.radius;
    return this.httpProvider.get(this.httpProvider.ROUTES.shops, searchOptions);
  }

  /**
   * Gets the shops of a user
   * @returns {Observable<any>} - the response from the server
     */
  getMyShops() : Observable<any>{
    let route = this.httpProvider.ROUTES.users + "/betrieb";
    return this.httpProvider.get(route);
  }

  /**
   * Gets the information for a single shop
   * @param shopID - the shop id
   * @returns {Observable<any>} - the response from the server
     */
  getShop(shopID: number) : Observable<any>{
    return this.httpProvider.get(this.httpProvider.ROUTES.shops + "/" + shopID);
  }

  /**
   * Creates a new shop
   * @param shop - shop data to be sent
   * @returns {Observable<any>} - the response from the server
     */
  createShop(shop: any) : Observable<any>{
    let body = this.mapToExpectedJson(shop);
    return this.httpProvider.post(this.httpProvider.ROUTES.shops, body);
  }

  /**
   * Edits an existing shop
   * @param shopID - the shop id
   * @param shop - updated shop information
   * @returns {Observable<any>} - the response from the server
     */
  editShop(shopID: number, shop: any) : Observable<any>{
    let body = this.mapToExpectedJson(shop);
    return this.httpProvider.put(this.httpProvider.ROUTES.shops + "/" + shopID, body);
  }

  /**
   * Gets the employees in a given shop
   * @param shopID - the shop id
   * @returns {Observable<any>} - the response from the server
     */
  getEmployees(shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/mitarbeiter";
    return this.httpProvider.get(route);
  }

  /**
   * Gets the next available time slots for a shop
   * @param shopID - the shop
   * @returns {Observable<any>} - the response from the server
     */
  getNextAvailableSlots(shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/dienstleistung/mitarbeiter";
    return this.httpProvider.get(route);
  }

  /**
   * Gets the queue for a given shop
   * @param shopID - the shop
   * @returns {Observable<any>} - the response from the server
     */
  getQueueFor(shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/ws";
    return this.httpProvider.get(route);
  }

  /**
   * Gets the managers for a shop
   * @param shopID - the shop
   * @returns {Observable<any>} - the response from the server
     */
  getManagers(shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/leiter";
    return this.httpProvider.get(route);
  }

  /**
   * Hires an employee to a shop
   * @param userID - the employee
   * @param shopID - the shop
   * @param anwesend - the initial attendance of the employee
   * @returns {Observable<any>} - the response from the server
     */
  hireEmployee(userID: number, shopID: number, anwesend: boolean) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/mitarbeiter";
    return this.httpProvider.post(route, {anwenderId: userID, betriebId: shopID, anwesend: anwesend || false});
  }

  /**
   * Hires a manager to a shop
   * @param userID - the manager
   * @param shopID - the shop
   * @param anwesend - the initial attendance of the manager
   * @returns {Observable<any>} - the response from the server
     */
  hireManager(userID: number, shopID: number, anwesend: boolean) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/leiter";
    return this.httpProvider.post(route, {anwenderId: userID, betriebId: shopID, anwsend: anwesend || false});
  }

  /**
   * Fires an employee from a shop
   * @param userID - the employee
   * @param shopID - the shop
   * @returns {Observable<any>} - the response from the server
     */
  fireEmployee(userID: number, shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/mitarbeiter/" + userID;
    return this.httpProvider.delete(route);
  }

  /**
   * Fires a manager from a shop
   * @param userID - the manager
   * @param shopID - the shop
   * @returns {Observable<any>} - the response from the server
     */
  fireManager(userID: number, shopID: number) : Observable<any>{
    let route = this.httpProvider.ROUTES.shops + "/" + shopID + "/leiter/" + userID;
    return this.httpProvider.delete(route);
  }

  /**
   * Maps the data that will be sent to the expected JSON format
   * @param shop - the data
   * @returns {{name: any, adresse: {strasse: any, hausNummer: any, plz: any, stadt: any}, tel: any, kontaktEmail: any, oeffnungszeiten: any}}
     */
  private mapToExpectedJson(shop: any) {
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
