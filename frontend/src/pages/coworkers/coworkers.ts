import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { UsersProvider } from '../../providers/users-provider';
import { ValidatorProvider } from '../../providers/validator-provider';
import { ConnectivityProvider } from '../../providers/connectivity-provider';
import { ToastController } from 'ionic-angular';

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
  searchName: string = "";
  users: any = [];
  newShop: boolean = false;
  shopID: number = 0;
  managers: any = [];
  employees: any = [];


// constructor and lifecycle-events

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public usersProvider: UsersProvider,
  public validator: ValidatorProvider, public connectivity: ConnectivityProvider, public toast: ToastController) {
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
  }

  registerError(message: string) : void{
    this.error = true;
    let toast = this.toast.create({
      message: message,
      duration: 3000
    });
    toast.present();
  }

  search(event: any) : void{
    this.resetError();
    if(!this.validator.searchName(this.searchName)){
      this.registerError("Search name not valid.");
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
        (error) => this.registerError("Couldn't get users from server.")
      )
  }

  reloadCoworkers() : void{
    this.shopsProvider.getEmployees(this.shopID)
      .subscribe(
        (employees) => {
          console.log("Employees for this shop: ", employees);
          this.employees = employees;
          this.users.forEach(u => {
            employees.forEach(e => {
              if(u.id === e.anwender.id){
                u.employee = true;
              }
            });
          });
        },
        (error) => {
          this.registerError("Error while fetching employees.");
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
          this.registerError("Error while fetching managers.");
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
          this.registerError("Error while hiring employee.");
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
        (error) => this.registerError("Error while hiring manager.")
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
          this.registerError("Error while firing employee.");
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
          this.registerError("Error while firing manager.");
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
