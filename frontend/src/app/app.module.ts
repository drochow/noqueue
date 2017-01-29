import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { IonicApp, IonicModule } from 'ionic-angular';
import { MyApp } from './app.component';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/filter';
import { AuthHttp, AuthConfig } from 'angular2-jwt';
import { Http } from '@angular/http';
import { Storage } from '@ionic/storage';
import { JwtHelper } from "angular2-jwt";
// custom providers:
import { HttpProvider } from '../providers/http-provider';
import { AuthenticationProvider } from '../providers/authentication-provider';
import { ShopsProvider } from '../providers/shops-provider';
import { ServicesProvider } from '../providers/services-provider';
import { QueuesProvider } from '../providers/queues-provider';
import { UsersProvider } from '../providers/users-provider';
import { UserConfigurationProvider } from '../providers/user-configuration-provider';
import { ValidatorProvider } from '../providers/validator-provider';
import { ConnectivityProvider } from '../providers/connectivity-provider';
import { GoogleMapsProvider } from '../providers/google-maps-provider';
import { LocationsProvider } from '../providers/locations-provider';
// custom pages:
import { LoginPage } from '../pages/login/login';
import { SignupPage } from '../pages/signup/signup';
import { DashboardPage} from '../pages/dashboard/dashboard';
import { SettingsPage } from '../pages/settings/settings';
import { EditProfilePage } from '../pages/edit-profile/edit-profile';
import { EditPasswordPage } from '../pages/edit-password/edit-password';
import { ReportProblemPage } from '../pages/report-problem/report-problem';
import { FAQPage } from '../pages/f-a-q/f-a-q';
import { PrivacyPolicyPage } from '../pages/privacy-policy/privacy-policy';
import { AboutNoQueuePage } from '../pages/about-no-queue/about-no-queue';
import { ShopsPage } from '../pages/shops/shops';
import { ShopSinglePage } from '../pages/shop-single/shop-single';
import { ServiceSinglePage } from '../pages/service-single/service-single';
import { MyQueuePositionPage } from '../pages/my-queue-position/my-queue-position';
import { MyShopsPage } from '../pages/my-shops/my-shops';
import { MyShopSinglePage } from '../pages/my-shop-single/my-shop-single';
import { ShopInfoPage } from '../pages/shop-info/shop-info';
import { ServiceInfoPage } from '../pages/service-info/service-info';
import { CoworkersPage } from '../pages/coworkers/coworkers';

let storage = new Storage();

export function getAuthHttp(http) {
  return new AuthHttp(new AuthConfig({
    headerPrefix: "X-Auth-Token",
    noJwtError: true,
    globalHeaders: [{'Accept': 'application/json'}],
    tokenGetter: (() => storage.get('id_token')),
  }), http);
}

@NgModule({
  declarations: [
    MyApp,
    LoginPage,
    SignupPage,
    DashboardPage,
    SettingsPage,
    EditProfilePage,
    EditPasswordPage,
    ReportProblemPage,
    FAQPage,
    PrivacyPolicyPage,
    AboutNoQueuePage,
    ShopsPage,
    ShopSinglePage,
    ServiceSinglePage,
    MyQueuePositionPage,
    MyShopsPage,
    MyShopSinglePage,
    ShopInfoPage,
    ServiceInfoPage,
    CoworkersPage
  ],
  imports: [
    HttpModule,
    IonicModule.forRoot(MyApp)
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    LoginPage,
    SignupPage,
    DashboardPage,
    SettingsPage,
    EditProfilePage,
    EditPasswordPage,
    ReportProblemPage,
    FAQPage,
    PrivacyPolicyPage,
    AboutNoQueuePage,
    ShopsPage,
    ShopSinglePage,
    ServiceSinglePage,
    MyQueuePositionPage,
    MyShopsPage,
    MyShopSinglePage,
    ShopInfoPage,
    ServiceInfoPage,
    CoworkersPage
  ],
  providers: [Storage, JwtHelper, HttpProvider, AuthenticationProvider, ShopsProvider, ServicesProvider, QueuesProvider, UsersProvider,
    UserConfigurationProvider, ValidatorProvider, ConnectivityProvider, GoogleMapsProvider, LocationsProvider, {
    provide: AuthHttp,
    useFactory: getAuthHttp,
    deps: [Http]
  }]
})
export class AppModule {}
