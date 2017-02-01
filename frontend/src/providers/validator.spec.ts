/**
 * Created by Sean on 01.02.2017.
 */
import {TestBed, async, inject} from '@angular/core/testing';
import {ValidatorProvider} from './validator-provider';

/**
 * A test class for the validator provider
 */

describe("Testing Validator Provider", () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        ValidatorProvider
      ],
      imports: [

      ]
    });
  }));

  // Username Tests

  it('Username too short', inject([ValidatorProvider], (validProvider) => {
    let username = "abcde";
    expect(validProvider.username(username).toBeFalsy());
  }));

  it('Username long enough', inject([ValidatorProvider], (validProvider) => {
    let username = "abcdef";
    expect(validProvider.username(username).toBeTruthy());
  }));

  it('Username too long', inject([ValidatorProvider], (validProvider) => {
    let username = "abcdefabcdefabcdefabcdefabcdefabcdef";
    expect(validProvider.username(username).toBeFalsy());
  }));



  // Password Tests

  it('Password matches Regex', inject([ValidatorProvider], (validProvider) => {
    let pw = "Ab1abc2";
    expect(validProvider.password(pw).toBeTruthy());
  }));

  it('Password does not match Regex', inject([ValidatorProvider], (validProvider) => {
    let pw = "Abc2";
    expect(validProvider.password(pw).toBeFalsy());
  }));

  it('Passwords match', inject([ValidatorProvider], (validProvider) => {
    let pw = "Ab1abc2";
    expect(validProvider.passwordsMatching(pw, pw).toBeTruthy());
  }));

  it('Passwords do not match', inject([ValidatorProvider], (validProvider) => {
    let pw = "Ab1abc2";
    let pw2 = "AB12abc"
    expect(validProvider.passwordsMatching(pw, pw2).toBeFalsy());
  }));


  // Email Tests

  it('Valid Email', inject([ValidatorProvider], (validProvider) => {
    let mail = "sean@google.com";
    expect(validProvider.email(mail).toBeTruthy());
  }));

  it('Invalid Email 1', inject([ValidatorProvider], (validProvider) => {
    let mail = "seangoogle.com";
    expect(validProvider.email(mail).toBeFalsy());
  }));

  it('Invalid Email 2', inject([ValidatorProvider], (validProvider) => {
    let mail = "sean@@google.com";
    expect(validProvider.email(mail).toBeFalsy());
  }));


  // Search Term Tests

  it('Search Term too long', inject([ValidatorProvider], (validProvider) => {
    let term = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod" +
      "tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. ";
    expect(validProvider.searchTerm(term).toBeFalsy());
  }));

  it('Search Term has correct length', inject([ValidatorProvider], (validProvider) => {
    let term = "Lorem ipsum dolor sit amet";
    expect(validProvider.searchTerm(term).toBeTruthy());
  }));


  // Search Name Tests

  it('Search Name too long', inject([ValidatorProvider], (validProvider) => {
    let name = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod" +
      "tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. ";
    expect(validProvider.searchName(name).toBeFalsy());
  }));

  it('Search Name has correct length', inject([ValidatorProvider], (validProvider) => {
    let name = "Lorem ipsum dolor sit amet";
    expect(validProvider.searchTerm(name).toBeTruthy());
  }));



  // Street Tests

  it('Street is valid 1', inject([ValidatorProvider], (validProvider) => {
    let street = "Poststraße";
    expect(validProvider.street(street).toBeTruthy());
  }));

  it('Street is valid 2', inject([ValidatorProvider], (validProvider) => {
    let street = "Heinrich-Heine-Straße";
    expect(validProvider.street(street).toBeTruthy());
  }));

  it('Street is valid 3', inject([ValidatorProvider], (validProvider) => {
    let street = "Am Berg";
    expect(validProvider.street(street).toBeTruthy());
  }));

  it('Street is valid 4', inject([ValidatorProvider], (validProvider) => {
    let street = "Rüdögä-Weg";
    expect(validProvider.street(street).toBeTruthy());
  }));

  it('Street is too long', inject([ValidatorProvider], (validProvider) => {
    let street = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod" +
      "tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. ";
    expect(validProvider.street(street).toBeFalsy());
  }));


  // ZIP Tests

  it('ZIP is valid 1', inject([ValidatorProvider], (validProvider) => {
    let zip = "12345";
    expect(validProvider.zip(zip).toBeTruthy());
  }));

  it('Zip is too short', inject([ValidatorProvider], (validProvider) => {
    let zip = "1234";
    expect(validProvider.zip(zip).toBeFalsy());
  }));

  it('Zip is too long', inject([ValidatorProvider], (validProvider) => {
    let zip = "123456";
    expect(validProvider.zip(zip).toBeFalsy());
  }));

  it('Zip contains letters', inject([ValidatorProvider], (validProvider) => {
    let zip = "1234a";
    expect(validProvider.zip(zip).toBeFalsy());
  }));


  // Street Number Tests

  it('Street Number is valid 1', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "1";
    expect(validProvider.streetNumber(streetNumber).toBeTruthy());
  }));

  it('Street Number is valid 2', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "12";
    expect(validProvider.streetNumber(streetNumber).toBeTruthy());
  }));

  it('Street Number is valid 3', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "12a";
    expect(validProvider.streetNumber(streetNumber).toBeTruthy());
  }));

  it('Street Number is valid 4', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "123";
    expect(validProvider.streetNumber(streetNumber).toBeTruthy());
  }));

  it('Street Number is valid 5', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "1234";
    expect(validProvider.streetNumber(streetNumber).toBeTruthy());
  }));
  it('Street Number is valid 6', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "12345";
    expect(validProvider.streetNumber(streetNumber).toBeTruthy());
  }));

  it('Street Number is valid 7', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "12345a";
    expect(validProvider.streetNumber(streetNumber).toBeTruthy());
  }));

  it('Street Number does not contain Number', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "a";
    expect(validProvider.streetNumber(streetNumber).toBeFalsy());
  }));

  it('Street Number contains 2 letters', inject([ValidatorProvider], (validProvider) => {
    let streetNumber = "12ab";
    expect(validProvider.streetNumber(streetNumber).toBeFalsy());
  }));


  // city Tests

  it('City is valid 1', inject([ValidatorProvider], (validProvider) => {
    let city = "Berlin";
    expect(validProvider.city(city).toBeTruthy());
  }));

  it('City is valid 2', inject([ValidatorProvider], (validProvider) => {
    let city = "München";
    expect(validProvider.city(city).toBeTruthy());
  }));

  it('City is valid 2', inject([ValidatorProvider], (validProvider) => {
    let city = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod";
    expect(validProvider.city(city).toBeFalsyy());
  }));

  it('City name is too short', inject([ValidatorProvider], (validProvider) => {
    let city = "M";
    expect(validProvider.city(city).toBeFalsyy());
  }));

  // shopName Tests

  it('Shop name is valid 1', inject([ValidatorProvider], (validProvider) => {
    let shop = "Haarwerkstatt";
    expect(validProvider.shopName(shop).toBeTruthy());
  }));

  it('Shop name is valid 2', inject([ValidatorProvider], (validProvider) => {
    let shop = "Salon Eisfeld";
    expect(validProvider.shopName(shop).toBeTruthy());
  }));

  it('Shop name is too long', inject([ValidatorProvider], (validProvider) => {
    let shop = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod";
    expect(validProvider.shopName(shop).toBeFalsy());
  }));


  // phone Tests

  it('Phone number is valid 1', inject([ValidatorProvider], (validProvider) => {
    let phone = "+4930 1234";
    expect(validProvider.phone(phone).toBeTruthy());
  }));

  it('Phone number is valid 2', inject([ValidatorProvider], (validProvider) => {
    let phone = "030 1234";
    expect(validProvider.phone(phone).toBeTruthy());
  }));

  it('Phone number is valid 3', inject([ValidatorProvider], (validProvider) => {
    let phone = "+49301234";
    expect(validProvider.phone(phone).toBeTruthy());
  }));

  it('Phone number is valid 4', inject([ValidatorProvider], (validProvider) => {
    let phone = "0301234";
    expect(validProvider.phone(phone).toBeTruthy());
  }));

  it('Phone number contains letter', inject([ValidatorProvider], (validProvider) => {
    let phone = "0301234a";
    expect(validProvider.phone(phone).toBeFalsy());
  }));


  // opening hours Tests

  it('Opening hours are valid 1', inject([ValidatorProvider], (validProvider) => {
    let hours = "10-18 Uhr";
    expect(validProvider.phone(hours).toBeTruthy());
  }));

  it('Opening hours are valid 2', inject([ValidatorProvider], (validProvider) => {
    let hours = "10:30-18 Uhr";
    expect(validProvider.phone(hours).toBeTruthy());
  }));

  it('Opening hours are valid 3', inject([ValidatorProvider], (validProvider) => {
    let hours = "10:15-20:45 Uhr";
    expect(validProvider.phone(hours).toBeTruthy());
  }));

  it('Opening hours are too', inject([ValidatorProvider], (validProvider) => {
    let hours = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod";
    expect(validProvider.phone(hours).toBeFalsy());
  }));



  // Service description Tests

  it('Service description is valid', inject([ValidatorProvider], (validProvider) => {
    let service = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod";
    expect(validProvider.serviceDescription(service).toBeTruthy());
  }));

  it('Service description is too long', inject([ValidatorProvider], (validProvider) => {
    let service = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod " +
      "tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam " +
      "et justo duo dolores et ea rebu";
    expect(validProvider.serviceDescription(service).toBeFalsy());
  }));


  // Service type Tests

  it('Service tpye is valid 1', inject([ValidatorProvider], (validProvider) => {
    let type = "Haarschneiden";
    expect(validProvider.serviceType(type).toBeTruthy());
  }));

  it('Service tpye is valid 2', inject([ValidatorProvider], (validProvider) => {
    let type = "Nasenhaarverlängerung";
    expect(validProvider.serviceType(type).toBeTruthy());
  }));

  it('Service tpye is too long', inject([ValidatorProvider], (validProvider) => {
    let type = "HaarschneidenHaarschneidenHaarschneidenHaarschneidenHaarschneiden";
    expect(validProvider.serviceType(type).toBeFalsy());
  }));


});
