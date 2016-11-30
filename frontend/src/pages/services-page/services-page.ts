import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
// import { ServicesData } from '../../providers/data';
import { HttpService } from '../../providers/http-service';
import { AuthenticationProvider } from '../../providers/authentication';
// import { Data } from '../../providers/data';
// import { Store } from '../../providers/store';
import {SingleService} from "../../pages/single-service/single-service";

/*
  Generated class for the ServicesPage page.

  See http://ionicframework.com/docs/v2/components/#navigation for more info on
  Ionic pages and navigation.
*/
@Component({
  selector: 'page-services-page',
  templateUrl: 'services-page.html',
  providers: [HttpService],
  entryComponents: [SingleService]
})
export class ServicesPage {

  services: any[];
  loggedIn = false;

  constructor(public navCtrl: NavController, public httpService: HttpService, private auth: AuthenticationProvider) {

  }

  expand(id: number){
    console.log("navigating with id: " + id);
    this.navCtrl.push(SingleService, {id: id});
  }

  ionViewDidLoad() {
    this.fetchAllServices();
    this.loggedIn = this.auth.isLoggedIn();
  }

  private fetchAllServices(){
    this.httpService.getAllServices().subscribe(
      (services) => this.services = services);
  }

}
