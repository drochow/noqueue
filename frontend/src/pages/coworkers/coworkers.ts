import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { ShopsProvider } from '../../providers/shops-provider';
import { UsersProvider } from '../../providers/users-provider';
import { ValidatorProvider } from '../../providers/validator-provider';

/*
  Generated class for the Coworkers page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-coworkers',
  templateUrl: 'coworkers.html',
  providers: [ ShopsProvider, UsersProvider, ValidatorProvider ]
})
export class CoworkersPage {

  error = false;
  errorMessage = "";
  searchName = "";
  users = [];
  newShop = false;
  shopID = 0;

  constructor(public navCtrl: NavController, public navParams: NavParams, public shopsProvider: ShopsProvider, public usersProvider: UsersProvider,
  public validator: ValidatorProvider) {
    this.shopID = navParams.get('shopID');
    this.newShop = navParams.get('newShop');
  }

  ionViewDidLoad() {
  }

  resetError(){
    this.error = false;
    this.errorMessage = "";
  }

  registerError(message){
    this.error = true;
    this.errorMessage = message;
  }

  search(){
    if(!this.validator.searchName(this.searchName)){
      this.registerError("Search name not valid");
      this.users = [];
      return;
    }
    this.usersProvider.getUsersWithName(this.searchName)
      .subscribe(
        (users) => this.users = users,
        (error) => this.registerError(error.message || "Couldn't get users from server")
      )
  }

  hireEmployee(slidingItem, id){
    slidingItem.close();
    this.shopsProvider.hireEmployee(id, this.shopID, true)
      .subscribe(
        () => {
          this.users.forEach(u => {
            if(u.id === id){
              u.employee = true;
            }
          })
        },
        (error) => this.registerError(error.message || "Couldn't hire employee")
      );
  }

  hireManager(slidingItem, id){
    slidingItem.close();
    this.shopsProvider.hireManager(id, this.shopID, false)
      .subscribe(
        () => {
          this.users.forEach(u => {
            if(u.id === id){
              u.manager = true;
            }
          })
        },
        (error) => this.registerError(error.message || "Couldn't hire manager")
      )
  }

  save(){
    if(!this.newShop){
      this.navCtrl.pop();
    } else {
      this.navCtrl.popToRoot();
    }
  }

}
