import {TestBed, getTestBed, async, inject} from '@angular/core/testing';
import {Headers, ResponseOptions, RequestOptions, Response, HttpModule, Http, XHRBackend, RequestMethod} from '@angular/http';
import {AuthenticationProvider} from './authentication';
import {HttpConfig} from './http-config';

describe("Testing Authentication Provider", () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthenticationProvider,
        HttpConfig
      ],
      imports: [
        HttpModule
      ]
    });
  }));

  it('Sign In with correct data', async(inject([AuthenticationProvider], (authProvider) => {
    let username = "john";
    let password = "doe";
    authProvider.signIn(username, password).then( () => {
      expect(authProvider.isLoggedIn()).toBeTruthy();
      expect(authProvider.getToken()).toBeDefined();
      expect(authProvider.getToken()).not.toEqual("");
      authProvider.logOut();
    });
  })));

  it('Sign In with incorrect data', async(inject([AuthenticationProvider], (authProvider) => {
    let username = "john123";
    let password = "doe456";
    authProvider.signIn(username, password).then( () => {
      expect(authProvider.isLoggedIn()).not.toBeTruthy();
      expect(authProvider.getToken()).toBeUndefined();
      expect(authProvider.getToken()).toEqual("");
      authProvider.logOut();
    });
  })));

  it('Log Out should delete the Token', async(inject([AuthenticationProvider], (authProvider) => {
    let username = "john";
    let password = "doe";
    authProvider.signIn(username, password).then( () => {
      expect(authProvider.isLoggedIn()).toBeTruthy();
      expect(authProvider.getToken()).not.toEqual("");
      authProvider.logOut();
      expect(authProvider.isLoggedIn()).not.toBeTruthy();
      expect(authProvider.getToken()).toEqual("");
    });
  })));

  it('Each Sign In should return a new Token', async(inject([AuthenticationProvider], (authProvider) => {
    let username = "john";
    let password = "doe";
    var firstToken: String;
    var secondToken: String;
    authProvider.signIn(username, password).then( () => {
      firstToken = authProvider.getToken();
      authProvider.signIn(username, password).then( () => {
        secondToken = authProvider.getToken();
        expect(firstToken).not.toEqual(secondToken);
      });
    });
  })));

  it('Sign Up with valid data', async(inject([AuthenticationProvider], (authProvider) => {
    let username = "john";
    let password = "doe";
    let email = "johndoe@test.org";
    authProvider.signUp(username, password, email).then( (info) => {
      expect(info).toEqual("signed up");
    });
  })));

  //@TODO - inform backend that "Action not found" is being returned in html form
  it('Sign Up with invalid data', async(inject([AuthenticationProvider], (authProvider) => {
    let username = "";
    let password = "";
    let email = "";
    authProvider.signUp(username, password, email).then( (info) => {
      expect(info).not.toEqual("signed up");
    });
  })));

  it('Sign Up should also receive a token', async(inject([AuthenticationProvider], (authProvider) => {
    let username = "john";
    let password = "doe";
    let email = "johndoe@test.org";
    authProvider.signUp(username, password, email).then( (info) => {
      expect(authProvider.getToken).not.toEqual("");
    });
  })));

  it('Token should be valid', async(inject([AuthenticationProvider], (authProvider) => {
    let username = "john123";
    let password = "doe456";
    authProvider.signIn(username, password).then( () => {
      authProvider.checkToken().then( (info) => {
        expect(info).toBe("logged in");
      });
    });
  })));

});
