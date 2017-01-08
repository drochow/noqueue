# NoQueue Frontend Architecture

*Pseudocode

## Pages

### __ App __

```js
import AuthenticationProvider
import DashboardPage, LoginPage

let rootPage = DashboardPage
// in DashboardPage, check if loggedIn and show Login Modal

/*if auth.isLoggedIn
	rootPage = DashboardPage
else
	rootPage = LoginPage
*/
// navCtrl.setRoot(DashboardPage)
```

### LoginPage

```js
import AuthenticationProvider, ValidatorProvider
import DashboardPage, SignupPage
import AlertController

// template:
inputs:		 username, password
buttons:	 login, signup, skip
infoField:	 *ngIf="error" {{errorMessage}}

// logic:
let error = false
let errorMessage = ""
let username, password: string

login() =>
	!validator.empty(username,password) // guard 
	error = false, errorMessage = ""
	auth.login(username, password).then(
		() => this.navCtrl.push(DashboardPage),
		(error) => this.error = true, errorMessage = error.message || ""
	)

showSignupPage() =>
	this.navCtrl.push(SignupPage)

skip() =>
	let confirm = alertCtrl.create(..)
		(text: "Users that are not logged in can not reserve a place in a queue.")
		.handler('OK') => this.navCtrl.push(DashboardPage) // or .popToRoot()
	confirm.present()
```

### SignupPage

```js
import AuthenticationProvider, ValidatorProvider
import DashboardPage

// template:
inputs:		 username,email,password,confirm
button:		 signup
infoField:	 *ngIf="error" {{errorMessage}}

// logic:
let username,email,password,confirm: string
let error = false
let errorMessage = ""

signup() =>
	error = false, errorMessage = ""
	validator.username && validator.email && validator.password && validator.passwordsMatching
		for-each: if false, error = true, errorMessage
	auth.signup(username,email,password).then(
		() => this.navCtrl.popToRoot(),
		(error) => this.error = true, errorMessage = error.message || "..."
	)
```

### DashboardPage

```js
import AuthenticationProvider, ShopsProvider, QueuesProvider
import SettingsPage, ShopsPage, MyShopsPage, MyQueuesPage, MyQueuePositionPage, LoginPage, SignupPage, ShopSinglePage, MyShopSinglePage, MyQueueSinglePage, ShopInfoPage

// template
ion-refresher
navbutton: settings
loginInfo: *ngIf="loggedIn === false" "Log in or Sign up to explore all features of NoQueue, including lining up"
card: myqueueposition: *ngIf="hasMyQueuePosition"
list shops nearby + button (see all)
list my shops + button (see all)
list my queues + button (see all)

// logic
let loggedIn: boolean
let myQueuePosition, shopsNearby, myShops, myQueues: any

ionViewDidLoad() => // 
	this.reloadData()
	if not logged in show modal(LoginPage)
	
refresh() =>
	this.reloadData()
	
reloadData() =>
	this.loggedIn = this.auth.isLoggedIn()
	
	this.shopsProvider.getNearbyShops(5,1,"")
		.subscribe(
			(data) => this.shopsNearby = data,
			(error) => ?
		)
	
	if(this.loggedIn){
		this.queuesProvider.getMyQueues()
			.subscribe(
				(data) =>	this.myQueues = data,
				(error) => ?
			)
		
		this.shopsProvider.getMyShops()
			.subscribe(
				(data) => this.myShops = data,
				(error) => ?
				}
			)
		
		this.queuesProvider.getMyQueuePosition()
			.subscribe(
				(data) => this.myQueuePosition = data,
				(error) => ?
				}
			)
	}

showMyQueuePositionPage() =>
	navCtrl.push(MyQueuePositionPage)

showSettingsPage() =>
	navCtrl.push(SettingsPage)

showLoginPage() =>
	navCtrl.push(LoginPage)

showSignupPage() =>
	navCtrl.push(SignupPage)

showShopsPage() =>
	navCtrl.push(ShopsPage)

showShopSinglePage(id) =>
	navCtrl.push(ShopSinglePage, {id})

showMyShopsPage() =>
	navCtrl.push(MyShopsPage)

showMyShopSinglePage(id) =>
	navCtrl.push(MyShopSinglePage, {id})

showMyQueuesPage() =>
	navCtrl.push(MyQueuesPage)
	
showMyQueueSinglePage(id) =>
	navCtrl.push(MyQueueSinglePage, {id})

showCreateShopPage() =>
	navCtrl.push(ShopInfoPage, {new: true})
```

### SettingsPage

```js
import AuthenticationProvider, UserConfigurationProvider
import EditProfilPage, EditPasswordPage, ReportProblemPage, FAQPage, PrivacyPolicyPage, AboutNoQueuePage

// template
list with sections:
	account - if loggedIn edit Profile, edit Password, logout, delete account
			  else login, signup
	customization - theme (select); if loggedIn - push notifications(select) // never, always [(ngModel)]="selection" ionChange = selected($event, selection)
	app - report a problem, faq, about, privacy policy

// logic
let theme, notificationSettings

themeSelection(event, theme) =>
	this.userConfigProvider.selectTheme(theme)
	
notificationSettingsSelection(event, settings) =>
	this.userConfigProvider.selectNotificationSettings(settings)

showEditProfilPage() =>
	this.navCtrl(EditProfilPage)

showEditPasswordPage() =>
	this.navCtrl(EditPasswordPage)

showReportProblemPage() =>
	this.navCtrl(ReportProblemPage)

showFAQPage() =>
	this.navCtrl(FAQPage)

showPrivacyPolicyPage() =>
	this.navCtrl(PrivacyPolicyPage)

showAboutNoQueuePage() =>
	this.navCtrl(AboutNoQueuePage)
```

### ShopsPage

```js
import ShopsProvider, ValidatorProvider
import ShopSinglePage

// template
searchbar: text, radius
list of shops [name, opening hours, distance, active queue]
error field - if error, errorMessage
virtual scroll

// logic
let radius, searchTerm
this.shops = []
error = false
errorMessage = ""

search(){
	error = false
	errorMessage = ""
	validate.radius, validate.searchTerm
	this.shopsProvider.getNearbyShops(...)
		.subscribe(
			(data) => {
				this.shops = data // filter queueOpen?
				if shops.length = 0 {
					error = true, message = "No shops found"
				}
			}
		)
}

virtualScroll.onScroll()  =>
	get next page of shops ?

showSingleShopPage(id) =>
	this.navCtrl.push(SingleShopPage, {id});
```

### ShopSinglePage

```js
import ShopsProvider, QueuesProvider, ServicesProvider
import MyQueuePositionPage

// template
mapview?
info for [name,address,opening hours,phone,email]
list [services]
	item - dauer, typ, kommentar, select (mitarbeiter), button - queue // check if wsoffen
refresher

// logic
let employees = []
let selectedEmployees = []
let shop
let services = []
let shopID
let active = true

constructor() =>
	this.shopID = navParams.get('shopID');

ionViewDidShow() =>
	this.reloadData()

refresh() =>
	this.reloadData()

reloadData() =>
	this.shopsProvider.getShop(shopID)
		.subscribe(
			(data) => this.shop = data,
			(error) => ?
		)
	this.servicesProvider.getServicesFor(shopID)
		.subscribe(
			(data) => this.services = data,
			(error) => ?
		)
	this.shopsProvider.getEmployeesFor(shopID)
		.subscribe(
			(response) => {
				this.employees = response.filter(wsoffen)
				if this.employees.length = 0  this.active = false (else reset to true)
			}
		)

employeeSelection(event, employee, serviceID) =>
	if(employee === "Any") employee = ""
	selectedEmployees[serviceID] = employee

lineup(id) =>
	this.queuesProvider.lineup(shopID, employeeName, serviceID)
		.subscribe(
			() => this.navCtrl.push(MyQueuePositionPage),
			(error) => ?
		)
```

### MyQueuePositionPage

```js
import QueuesProvider, ShopsProvider

// template
info [date,positionNumber,employee,shop,address,phone]
button leave

// logic
let shop
let queuePosition

constructor() =>
	this.reloadData()
	
ionViewDidLoad() =>
	this.reloadData()

reloadData() =>
	this.queuesProvider.getMyQueuePosition()
		.subscribe(
			(response) => {
				this.queuePosition = response
				if(this.queuePosition.shopID){
					this.shopsProvider.getShop(this.queuePosition.shopID)
						.subscribe(
							(response) => this.shop = response
						)
				}	
			}
		)

leave() =>
	this.queuesProvider.leave()
		.subscribe(
			() => this.navCtrl.pop();
		)
```

### MyShopsPage

```js
import ShopsProvider
import MyShopSinglePage, ShopInfoPage

// template
navbutton + (create)
list shops [name,address,employeesnumber]
refresher

// logic
let shops = []

constructor() =>
	this.reloadData()

ionViewDidLoad() =>
	this.reloadData()

refresher() =>
	this.reloadData()

reloadData() =>
	this.shopsProvider.getMyShops()
		.subscribe(
			(shops) => this.shops = shops,
			(error) => ?
		)

showShop(id) =>
	this.navCtrl.push(MyShopSinglePage, {id})

createNewShop() =>
	this.navCtrl.push(ShopInfoPage, {new: true})
```

### MyShopSinglePage

```js
import ShopsProvider, AuthenticationProvider, ServicesProvider
import ServiceInfoPage, ShopInfoPage, CoworkersPage

// template
info [name, address, openinghours, phone, email], button Edit
button - add coworkers
list - leaders, on slide: delete, demote
list - employees, on slide: - delete, promote
toggle - "I (the leader) am also working and can open my own queue"
list - services[type,duration,description + button delete]
button - new service

// logic
let shopID
let shop
let managers 
let employees
let imAlsoWorking
let services

constructor() =>
	this.shopID = navParams.get('shopID')
	this.reloadData()

ionViewDidLoad() =>
	this.reloadData()

reloadData() =>
	this.shopsProvider.getShop(shopID)
		.subscribe(
			(shop) => this.shop = shop,
			(error) => ?
		)
	this.shopsProvider.getEmployeesFor(shopID)
		.subscribe(
			(employees) => this.employees = employees,
			(error) =>
		)
	this.shopsProvider.getManagersFor(shopID)
		.subscribe(
			(managers) => {
				this.managers = managers
				this.imAlsoWorking = this.managers.filter(m => m.userID === auth.getUserID())[0].working;
			},
			(error) => ?
		)
	this.servicesProvider.getServicesFor(shopID)
		.subscribe(
			(services) => this.services = services,
			(error) => ?
		)

// todo
demoteManager(userID)
promoteEmployee(userID)

deleteManager(userID) =>
	this.shopsProvider.deleteManager(userID)
		.subscribe(
			() => this.reloadData()
		)

deleteEmployee(userID) =>
	this.shopsProvider.deleteEmployee(userID)
		.subscribe(
			() => this.reloadData()
		)

// todo
toggleChange() =>
	// this.shopsProvider.leaderWorking(boolean)

editShopInfo() =>
	navParams new false
	navCtrl.push(ShopInfoPage)

showService(id) =>
	navCtrl.push(ServiceInfoPage, {new: false})

createService() =>
	navCtrl.push(ServiceInfoPage, {new: true})

addCoworkers() =>
	navCtrl.push(CoworkersPage)
```

### ShopInfoPage

```js
import ShopsProvider
import ServiceInfoPage

// template
errorField - if error -> errorMessage
progress info - if new Shop "Step 1: shop info", // later: step 2 - create a service, step 3 - add coworkers
input [name,phone,email,opening hours, address - zip, city, streetnr, street]
button - if newshop "proceed" else "save" // ShopInfoPage can be called to edit some shops info or when creating a new shop

// logic
let error
let errorMessage
let newShop
let shopID
let name, phone, email, opening hours, zip, city, streetnr, street
let buttonTitle

constructor() =>
	this.newShop = navParams.get('newShop')
	if (!this.newShop){
		this.shopID = navParams.get('shopID')
		this.reloadData()
	}

reloadData() =>
	this.shopsProvider.getShop(shopID)
		.subscribe(
			(shop) => {
				this.name = shop.name
				this.phone = shop.phone
				this.email = shop.email
				this.openingHours = shop.openingHours
				this.address = {
					zip: shop.address.plz,
					city: shop.address.stadt ...
				}
			}
		)

save() =>
	error = false errorMessage = ""
	validate(name, phone, ...)
		if false error = true errorMessage ...
	this.shopsProvider.editShop(shop..)
		.subscribe(
			() => this.navCtrl.pop()
		)
		
proceed() =>
	error = false errorMessage = ""
	validate ...
		if false error errorMessage
	this.shopsProvider.createShop(shop)
		.subscribe(
			() => {
				this.navCtrl.push(ServiceInfoPage, {new: true, id: shopid})
			},
			(error) => ?
		)
```

### ServiceInfoPage

```js
import ValidatorProvider, ServicesProvider
import CoworkersPage

// template
errorField - if error {{errorMessage}}
label + input - duration, type (selectable), description
button - save/proceed
input - custom (new) service type

// logic
let newService
let serviceID
let shopID
let service
let customType = false
let duration, type, description
let types = []
let error = false
let errorMessage = ""

constructor() =>
	servicesProvider.getAllServiceTypes()
		.subscribe(
			(data) => this.types = data
		)
	this.newService = navParams.get('new')
	if (!this.newService){
		this.shopID = navParams.get('shopID')
		this.reloadData()
	}

reloadData() =>
	servicesProvider.getService(serviceID)
		.subscribe(
			(service) => duration, type, description ... = service.duration ...
			// i guess it can be done with ES6 destructuring
		)

checkInput() =>
	error = false errorMessage = ""
	validate (duration, type, description)
	return valid: bool
		
save() =>
	checkInput() // if false return
	this.servicesProvider.editService(serviceID, service)
		.subscribe(
			() => this.navCtrl.pop()
		)

proceed() =>
	checkInput() // if false return
	this.servicesProvider.createService(shopID, service)
		.subscribe(
			() => this.navCtrl.push(CoworkersPage, {new: true, id: shopID})
		)
```

### CoworkersPage

```js
import ShopsProvider, UsersProvider

// template
searchbar
list results/users - swipeable; badge for coworkers
save button - (fixed in footer?)

// logic
let searchName
let users 
let newPage
let shopID

constructor() =>
	this.newpage = this.navParams.get('new')
	if(!newPage){
		this.shopID = this.navParams.get('id')
	}
search() =>
	this.usersProvider.getUsersWithName(searchName)
		.subscribe(
			(users) => this.users = users
		)

hireEmployee(id) =>
	this.shopsProvider.hireEmployee(shopID, userID)
		.subscribe(
			() => {
				users.filter(u => u.id = userID)[0].position = "Employee"
			}
		)

hireManager(id) => 
	this.shopsProvider.hireManager(shopID, userID)
		.subscribe(
			() => {
				users.filter(u => u.id = userID)[0].position = "Manager"
			}
		)

save() =>
	if not new 	pop()
	if new 		popToRoot()
```

### MyQueuesPage

```js
import QueuesProvider
import MyQueueSinglePage

// template
list 
	card items [shop, address, service, active?]

// logic
let queues

constructor() =>
	this.reloadData()

ionViewDidLoad() =>
	this.reloadData()

reloadData() =>
	this.queuesProvider.getMyQueues()
		.subscribe(
			(queues) => this.queues = queues,
			(error) => ?
		)

showQueue(queueID) =>
	navCtrl.push(MyQueueSinglePage, {id: queueID})
```

### MyQueueSinglePage

```js
import QueuesProvider

// template
toggle open
toggle prevent joining -> if open
list users - button start / stop

// logic
let users
let open
let preventJoining 
let queueID

constructor() =>
	this.queueID = navparams.get('id')
	this.reloadData()

ionViewDidLoad() =>
	this.reloadData()

reloadData() =>
	this.queuesProvider.getQueue(queueID)
		.subscribe(
			(users) => this.users = users
		)

// todo handlers:
toggleOpen()
togglePreventJoining()
start()
stop()
```

### EditProfilePage

```js
import UsersProvider, AuthenticationProvider

// template
label + input [name,email,city,zip,streetnr,street]
button - save
errorField - if error {{errorMessage}}

// logic
let name, email, city, zip, streetnr, street
let error, errormessage

constructor() =>
	reloadData()

ionViewDidLoad() =>
	reoloadData()

reloadData() =>
	this.usersProvider.getMe()
		.subscribe(
			(me) => this.name = me.nutzerName || "" ...
		)

save() =>
	validate(username,email,city,zip,streent,street)
	data = { .. }
	this.usersProvider.changeMyInfo(data)
		.subscribe(
			() => this.navCtrl.pop()
		)
```

### EditPasswordPage

```js
import AuthenticationProvider (?), UsersProvider (?)

// template
label + input old password, new password, confirm new password
button - save
errorField 

// logic
let oldPassword, newPassword, confirmNewPassword
let error, errorMessage

save() =>
	validate(newPassword, confirmNewPassword, match)
	this.usersProvider.changeMyPassword(newPassword)
		.subscribe(
			() => this.navCtrl.pop()
		)

```

### ReportProblemPage

```js
// template
{{info}}

// logic
let info = ...
```

### FAQPage

```js
// template
{{info}}

// logic
let info = ...
```

### PrivacyPolicyPage

```js
// template
{{info}}

// logic
let info = ...
```

### AboutNoQueuePage

```js
// template
{{info}}

// logic
let info = ...
```

## Providers

### HttpProvider

```js
import Storage

let ROUTES
let token

constructor() =>
	readToken()

readToken() =>
	if storage
		storage.get('token').then
			this.token = token	
requestOptions() =>
	if this.token !== ""
		headers.append(token)
	return options
	
// TODO: is there a reason not to send the token always?
get(route, searchOptions: {limit, offset, filter}) : Observable<any> =>
	options.search = searchOptions
	return http.get(route, options)
			.map(data => data.json())

delete(route) : Observable<any> =>
	return http.delete(route, requestOptions())
			.map(data => data.json())
	
post(route,body)
patch(route,body)
put(route,body)
	.map(data => data.json())	
```

### AuthenticationProvider

```js
import HttpProvider
import Storage, JwtHelper

let token: String

constructor() =>
	if storage
		storage.get('token').then
			this.token = token

login(username, password) =>
	body = json(username,password)
	return new Promise(function(resolve, reject)){
		this.httpProvider.post(this.http.ROUTES.login, body)
			.subscribe(
				(token) => {
					this.storage.set('token', token);
          		this.token = token;
          		this.http.readToken();	
          		resolve("Logged In");
				},
				(error) => {
					reject(error.message)
				}
			)
	}
	
signup(username,email,password) =>
	body = json(username, email, password)
	return new Promise(function(resolve, reject)){
		this.httpProvider.post(route,body)
			.subscribe(
				(token) => {
					this.storage.set('token', token);
          		this.token = token;
          		this.http.readToken();	
          		resolve("Signed Up");
				},
				(error) => {
					reject(error.message);
				}
			)
	}

logOut() =>
	this.resetToken()

private resetToken() => 
	this.token = "";
    this.storage.remove('token');	
    this.http.readToken();

isLoggedIn() =>
	return token not null, undefined or empty string

getToken() =>
	return this.token
	
getUserId() =>
	decoded = jwthelper.decode(this.token)
	return decoded.userId				
```

### ShopsProvider

```js
import HttpProvider, AuthenticationProvider

getAllShops() =>
	return this.httpProvider.get(route)
	
getNearbyShops(limit, offset, filter) =>
	return this.httpProvider.get(route, options: {limit, offset, filter})
	
getMyShops() =>
	return this.httpProvider.get(route + auth.id)

getShop(id) =>
	return this.httpProvider.get(route)	
	
hireEmployee(userID) =>
	return this.httpProvider.post(..)
	
hireManager(userID) =>
	return this.httpProvider.post(..)

getEmployeesFor(shopID) =>
	return this.httpProvider.get(...)

getManagersFor(shopID) =>
	return this.httpProvider.get(...)
	
fireManager(userID) =>
	return this.httpProvider.delete(...)

fireEmployee(userID) =>
	return this.httpProvider.delete(...)

// todo
demoteManager(userID)
// todo
promoteEmployee(userID)	

editShop(shopID, shop) =>
	return this.httpProvider.patch(...)
	
createShop(shop) =>
	// map to json specification
	return this.httpProvider.post(...)
```

### ServicesProvider

```js
import HttpProvider

getServicesFor(shopID) =>
	return this.httpProvider.get(route)

getService(serviceID) =>
	return this.httpProvider.get(...)
	
getAllServiceTypes() =>
	return this.httpProvider.get(..)
	
createService(shopID, service) =>
	// map service
	return this.httpProvider.post(..)

editService(serviceID, service) =>
	// map service
	return this.httpProvider.patch(..)
```

### QueuesProvider

```js
import HttpProvider, AuthenticationProvider

getMyQueues() =>
	return this.httpProvider.get(route)
	
getMyQueuePosition() =>
	return this.httpProvider.get(route + auth.userID)
	
getQueue(queueID) =>
	return this.httpProvider.get(route)
	
lineup(shopID, serviceID, employeeName) =>
	body = json(...)
	return this.httpProvider.post(route, body)
	
leave() =>
	return this.httpProvider.delete(route, auth.userID?)
```

### UsersProvider

```js
import HttpProvider, AuthenticationProvider

getUsersWithName(name) =>
	return this.httpProvider.get(...)
	
getMe() =>
	return this.httpProvider.get(auth.userID)

changeMyInfo(data) =>
	// map data
	return this.httpProvider.patch(auth.userID, data)
	
changeMyPassword(password) =>
	return this.httpProvider.patch(auth.userID, password)
```

### UserConfigurationProvider

```js
import Storage

selectTheme(theme) =>
	this.storage.set('theme', theme);
	
selectNotificationSettings(notificationSettings) =>
	this.storage.set('notificationSettings', notificationSettings)
```

### ValidatorProvider

```js
// here you can use arrow functions with regex

username(username) =>
	return ...

password()

passwordMatching()

email()

radius()

searchTerm()

street()

zip()

streetNumber()

city()

empty(...strings) =>
	for(value of strings)
		if value = "" || undefined return true
		
duration()

type()

description()
```