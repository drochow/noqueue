# NoQueue - Frontend To-Do List

## Fix-Up

### Providers

#### AuthenticationProvider

##### userID -- #1000

The functionality for `userID` is obsolete, as the server endpoints don't require it for GET/UPDATE ME requests.  

#### QueuesProvider

##### getMyQueues -- #1010

Obsolete (use `getMyShops()` and check if the user is `leiter`/`mitarbeiter`.)  
Check if someone is calling this function.  

#### ServicesProvider

##### getQueueFor(shopID) -- #1020 

Move to `ShopsProvider` and update all calling files.

##### getService(serviceID) -- #1021

Obsolete and not supported by the backend - find it's usage in the pages (probably only ```service-single.ts```) and replace it by sending the service in the `navParams` when switching from `Shop-Single` to `Service-Single`.  
This should be actually already done, but my IDE says that some page is using the `getService` method - so please check.

#### ShopsProvider

##### Some obsolete methods -- #1030

Delete `getAllShops()`, `getNearbyShos()` and the commented `getShops()` method.  
The same applies to `promote()` and `demote()`. There seem the be some classes using them, though, so update them also (probably `coworkers.ts` and `my-shop-single.ts`). 

#### ValidatorProvider -- #1040

Maybe reduce the difficulty of the `password` RegEx?


### Pages

#### Dashboard

##### asyncSetup (authProvider) --- (later!)  -- #1100

Each time the `DashboardPage` is being prepared to be shown, we are calling an async setup of the `AuthenticationProvider` (reading the Token from the `Storage` and stuff). Maybe that's not the best place to make it, as the navigator visits this page quite often.  
Better idea - move this to `app/app.component.ts`.  

Update - There is a big chance that there will be a `SplashScreenPage` that will call this setup and jump to `DashboardPage` after finished, so don't move this method yet.

#### Coworkers

##### map users[]   -- #1110

After reloading the Coworkers modify the users array by setting a `manager` or `employee` flag on the users whose `id` matches the `manager.anwender.id` / `employee.anwender.id` (no guarantee for the right names of the properties).

##### fireManager/fireEmployee  -- #1111

The server endpoints probably expect the managerID (not the userID). The HTML template is now giving the userID to the fireManager/Employee function?  
Please check the server's expectations for the request.

#### MyQueues and MyQueueSingle

##### Delete these pages  -- #1120

This pages are no longer needed, as we are going to instead show their information in `My-Shops` and `My-Shop-Single`.  
In order to delete them properly, you have to go to `app/app.module.ts` and remove them from:

- the `import` lines
- the `declarations[]` inside `NgModule`
- the `entryComponents[]` inside `NgModule`

And to remove them from their calling stations (```dashboard.ts```):

- `import` 
- `entryComponents` inside of the `@Component` declaration
- delete the methods that are telling the `NavCtrl` to jump to these pages

#### ServiceInfo 

##### Automatic Select of custom type  -- #1130

When Creating a Custom Type, automatically select it in the `ion-select` element.  
[This may help you](http://ionicframework.com/docs/v2/api/components/select/Select/)

#### ServiceSingle

##### Employees not loading  -- #1140

I don't know if this is a problem on the Backend, but at our last meeting I had to comment the `getNextAvailableSlots` in `reloadData()` (which should return an array of employees and their next slots) and used the `getEmployees()` instead (which returns only the employees).  

#### Settings

##### Theme Selection  -- #1150

There seems to be a problem with the Theme Selection (dark or light), as the Ionic `.scss` files are being prerendered and cannot be updated while the application is running.  
Please remove this functionality from `settings.html` and `setting.ts`, as well from `providers/user-configuration-provider.ts`.

##### Notification --- (later!)   -- #1151

If we decide to use Push Notifications, we have to update the Settings correspondingly. 

#### Empty Pages

Think out some appropriate content for the following pages:

- About NoQueue (```pages/about-no-queue```)   **-- #1160**
- FAQ (```pages/f-a-q```) **-- #1161**
- Privacy Policy (```pages/privacy-policy```) **-- #1162**
- Report A Problem (```pages/report-problem```) **-- #1163**
 

## Edge Cases

### User that has a Queue Position wants to line up for a second service -- #2000

1. Check if users has a queue position:
	- by sending a GET My Queue Position request
	- or by keeping this info in the Storage:

	```js
	import { Storage } from '@ionic/storage'
	...
	constructor(public storage: Storage){
		// async operation with Promise return 
		this.storage.get('isLinedUp')
			.then(
				(isLinedUp) => ...,
				(couldntFind) => ...
			)
	}
	```
2. Disable "Line Up" button in ```pages/service-single.ts```
3. Inform the user that he can not be lined up in two queues simultaneously / offer him to visit the ```MyQueuePositionPage``` (in order to cancel his first booking, if needed)

### Line Up - only when Logged In -- #2001

Disable the "Line Up" button in ```pages/service-single.ts``` if the user isn't logged in:

```js
import { AuthenticationProvider } from ...
...
isLoggedIn: bool;

constructor(public auth: AuthProvider){
}

ionViewWillEnter(){
	this.isLoggedIn = this.auth.isLoggedIn();
}
```

### No Network Connection -- #2002

Before showing a given page (```ionViewWillEnter()```) / before sending a request / do you have a better idea when this should be done? :  

Check if the device is connected to the internet (```providers/connectivity-provider.ts```)

If not, show a Toast / modify the page.

### Lining-Up after several minutes on the page -- #2003

During this time, the next available time slots could be changed on the server:

- reload every N seconds, or:
- send new GET Time Slots request right before Lining Up and check if the result matches the stored info; if not - inform the user that changes have been made & reload the Page / ?

### Managing Queue without refreshing the page -- #2004

- Reload the data every N seconds? (```pages/my-shop-single.ts```), and/or:
- Before Starting/Stopping the treatment of a client, check if he is still there (by reloading the data)

## UX

### Server Errors -- #3000

Translate their meaning to the user.

### Validation Errors -- #3001

Better User Feedback if he enters non-valid parameters.  
Idea: for each method in `ValidatorProvider`, make a string property, which describes the Regex-rule (minimize redundancy).

### OnBoarding -- #3002

Create On-Boarding Slides that are shown the first time a user opens the application.

### SplashScreen -- #3003

Create a SplashScreen with the NoQueue Logo, which is shown until the `AuthenticationProvider` is done with it's async setup.

## Design

### Logo and AppIcon -- #4000

Coming soon.

### Consistent Design on the Pages -- #4001

Also coming soon.

## Features

### Push Notifications -- #5000

Check how the Cordova plugin for push notifications works (ionic-docs/native).  
We could inform the users N Minutes before their appointment. 
Implementation (idea):

1. A new ```push-notifications-provider.ts``` that can send push notifications with ```message: string```
2. Get the appointment's start (GET My Queue Position)
3. An Event Listener / some sort of function / ? - that checks if time < N minutes
4. Disable this function after sending the Notification


## Testing

I made some tests last year, but deleted them after Rewriting the Frontend, because they didn't match the new architecture.  
However, you can use them as a basis for your tests: [Link to old tests](https://github.com/dkaatz/noqueue/blob/7e290f019d167be82a92c55bf504fdba720081b4/frontend/src/providers/authentication.spec.ts).  
You just have to create a file ending on `.spec.ts` and it will be recognized as a test file by Karma.

### Validator -- #6000

Make sure that the RegEx are working properly. 

### Http-Requests -- #6001

If you wish, you can also test the functionality of the different http-providers (`ShopsProvider`,`AuthenticationProvider` ...).  

## Style

### Clean-Up -- #7000

After implementing the application:

 - remove all `console.log`
 - remove non-used code

### Consistency

#### Providers -- #7010

Some of the Providers have non-consistent parameter names right now. For example:

```js
// ShopsProvider
getShop(id: any)
editShop(shopID: any, ...)
```

#### Pages -- #7011

Guidelines for consitent formatting of the Components (TypeScript classes):

```js
import ionic/angular stuff;
import providers;
import other pages;
...
class {
	// declare variables used by the HTML template (ViewModel):
	shop: any;
	employees = [];
	...
	
	// constructor and lifecycle-events (chronological order):
	constructor(){}
	ionViewWillEnter(){}
	ionViewDidEnter(){}
	ionViewWillLeave(){}
	
	// ViewModel logic (working with the data)
	reloadData(){}
	registerError(){}
	checkInput(){}
	...
	
	// ViewController logic (reacting to events)
	editShop()
	showService()
	jumpToOtherPage()
}
```

### Type Declarations -- #7012

Wherever possible, declare the types of the parameters and the return types of the functions.


## Documentation -- #8000

Document all files with ```JSDoc``` (we could split this task an the end).

## Deployment -- #9000

If the backend is deployed on the real university server, we can also upload NoQueue to the ```Ionic View App```. This way, we will be able to test the application on our real devices.