import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { UsersProvider } from '../../providers/users-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';

/*
  Generated class for the Coworkers page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-coworkers',
  templateUrl: 'coworkers.html',
  providers: [ ShopsProvider, UsersProvider, ValidatorProvider, ConnectivityProvider ]
})
export class CoworkersPage {

// declare variables used by the HTML template (ViewModel)

  error: boolean = false;
  errorMessage: string  = "";
  searchName: string = "";
  users: any = [];
  newShop: boolean = false;
  shopID: number = 0;
  managers: any = [];
  employees: any = [];


// constructor and lifecycle-events

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public usersProvider: UsersProvider,
  public validator: ValidatorProvider, public connectivity: ConnectivityProvider) {
    this.shopID = navParams.get('shopID');
    this.newShop = navParams.get('newShop');
  }

  ionViewDidLoad() : void{
  }
  
  ionViewWillEnter() : void{
    this.connectivity.checkNetworkConnection();
  }


// ViewModel logic (working with the data)

  resetError() : void{
    this.error = false;
    this.errorMessage = "";
  }

  registerError(message: string) : void{
    this.error = true;
    this.errorMessage = message;
  }

  search(event: any) : void{
    this.resetError();
    if(!this.validator.searchName(this.searchName)){
      this.registerError("Search name not valid");
      this.users = [];
      return;
    }
    this.usersProvider.getUsersWithName(this.searchName)
      .subscribe(
        (users) => {
          console.log("GET users with name: ", users);
          this.users = users;
          this.reloadCoworkers();
        },
        (error) => this.registerError(error.message || "Couldn't get users from server")
      )
  }

  reloadCoworkers() : void{
    this.shopsProvider.getEmployees(this.shopID)
      .subscribe(
        (employees) => {
          console.log("Employees for this shop: ", employees);
          this.employees = employees;
          // this users - for each:
          //     this.employees - for each:
          //       if user.id === employee.anwender.id -> user.employee = true
          this.users.forEach(u => {
            employees.forEach(e => {
              if(u.id === e.anwender.id){
                u.employee = true;
              }
            });
          });
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while hiring employee: ", jsonError);
        }
      );

    this.shopsProvider.getManagers(this.shopID)
      .subscribe(
        (managers) => {
          console.log("Managers for this shop: ", managers);
          this.managers = managers;
          // this users - for each:
          //     this.managers - for each:
          //       if user.id === manager.anwender.id -> user.manager = true
          this.users.forEach(u => {
            managers.forEach(m => {
              if(u.id === m.anwender.id){
                u.manager = true;
              }
            });
          });
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while hiring employee: ", jsonError);
        }
      )
  }


// ViewController logic (reacting to events)

  hireEmployee(slidingItem: any, id: number) : void{
    slidingItem.close();
    this.shopsProvider.hireEmployee(id, this.shopID, true)
      .subscribe(
        () => {
          this.users.forEach(u => {
            if(u.id === id){
              u.employee = true;
            }
          });
          this.reloadCoworkers();
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while hiring employee: ", jsonError);
          this.registerError(jsonError.message);
        }
      );
  }

  hireManager(slidingItem: any, id: number) : void{
    slidingItem.close();
    this.shopsProvider.hireManager(id, this.shopID, false)
      .subscribe(
        () => {
          this.users.forEach(u => {
            if(u.id === id){
              u.manager = true;
            }
          });
          this.reloadCoworkers();
        },
        (error) => this.registerError(error.message || "Couldn't hire manager")
      )
  }

  fireEmployee(slidingItem: any, id: number) : void{
    slidingItem.close();
    this.shopsProvider.fireEmployee(id, this.shopID)
      .subscribe(
        () => {
          this.users.forEach(u => {
            if(u.id === id){
              u.employee = false;
            }
          });
          this.reloadCoworkers();
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while firing employee: ", jsonError);
          this.registerError(jsonError.message);
        }
      );
  }

  fireManager(slidingItem: any, id: number) :void{
    slidingItem.close();
    this.shopsProvider.fireManager(id, this.shopID)
      .subscribe(
        () => {
          this.users.forEach(u => {
            if(u.id === id){
              u.manager = false;
            }
          });
          this.reloadCoworkers();
        },
        (error) => {
          let jsonError = JSON.parse(error._body);
          console.log("Error while firing manager: ", jsonError);
          this.registerError(jsonError.message);
        }
      );
  }

  save() : void{
    if(!this.newShop){
      this.navCtrl.pop();
    } else {
      this.navCtrl.popToRoot();
    }
  }

}
