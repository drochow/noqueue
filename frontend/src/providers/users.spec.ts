import {TestBed, async, inject} from '@angular/core/testing';
import {HttpModule} from '@angular/http';
import {AuthenticationProvider} from './authentication';
import {UsersProvider} from './users';
import {HttpConfig} from './http-config';

describe("Testing Users Provider", () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        UsersProvider,
        AuthenticationProvider,
        HttpConfig
      ],
      imports: [
        HttpModule
      ]
    });
  }));

  it('Get user', async(inject([UsersProvider], (usersProvider) => {
    let id = 101;
    usersProvider.getUser(id).then( (user) => {
      expect(user).toBeDefined();
      expect(user).not.toBeNull();
    });
  })));

  it('Get non-existing user', async(inject([UsersProvider], (usersProvider) => {
    let id = -1;
    usersProvider.getUser(id).then( (user) => {
      expect(user).toBeUndefined();
      expect(user).toBeNull();
    });
  })));

  it('Patch user with correct data', async(inject([UsersProvider], (usersProvider) => {
    let id = 101;
    let user = {
      email: "jdoe@example.org"
    };
    usersProvider.patchUser(id, user).then( (info) => {
      expect(info).toEqual("updated");
    });
  })));

  it('Patch user with incorrect data', async(inject([UsersProvider], (usersProvider) => {
    let id = 101;
    let user = {
      email: ""
    };
    usersProvider.patchUser(id, user).then( (info) => {
      expect(info).not.toEqual("updated");
    });
  })));

  it('Patch non-existing', async(inject([UsersProvider], (usersProvider) => {
    let id = -1;
    let user = {
      email: "jdoe@example.org"
    };
    usersProvider.patchUser(id, user).then( (info) => {
      expect(info).not.toEqual("updated");
    });
  })));

  it('Put user with correct data', async(inject([UsersProvider], (usersProvider) => {
    let id = 101;
    let user = {
      name: "John",
      email: "jdoe@example.org",
      password: "123456"
    };
    usersProvider.putUser(id, user).then( (info) => {
      expect(info).toEqual("updated");
    });
  })));

  it('Put user with incorrect data', async(inject([UsersProvider], (usersProvider) => {
    let id = 101;
    let user = {
      name: "John",
      email: "jdoe@example.org",
      password: "123456"
    };
    usersProvider.putUser(id, user).then( (info) => {
      expect(info).not.toEqual("updated");
    });
  })));

  it('Put non-existing', async(inject([UsersProvider], (usersProvider) => {
    let id = -1;
    let user = {
      name: "John",
      email: "jdoe@example.org",
      password: "123456"
    };
    usersProvider.putUser(id, user).then( (info) => {
      expect(info).not.toEqual("updated");
    });
  })));

  it('Delete user', async(inject([UsersProvider], (usersProvider) => {
    let id = 101;
    usersProvider.deleteUser(id).then( (info) => {
      expect(info).toEqual("deleted");
    });
  })));

  it('Delete non-existing user', async(inject([UsersProvider], (usersProvider) => {
    let id = -1;
    usersProvider.deleteUser(id).then( (info) => {
      expect(info).not.toEqual("deleted");
    });
  })));

});
