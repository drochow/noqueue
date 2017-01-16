# Frontend - REST API

**Bold** - muss beim Frontend angepasst werden  
==Highlighted== - fehlt beim Backend

## AuthenticationProvider


Name          	| Route            | Request                    | Response | Calling Files 
-----------------|------------------|-------------------------|----------|---
✔︎ Login		 	 	| POST /auth       | nutzerName<br>password | token | login.ts
✔︎ Signup          | POST /anwender   | nutzerName<br>nutzerEmail<br>password|token | signup.ts 


## UsersProvider

Name          	| Route            | Request                    | Response | Calling Files
-----------------|------------------|-------------------------|----------|---
✔︎ Get Users with name | GET /anwender/directory<br>?q=name | q=name| [ id<br>nutzerName<br>nutzerEmail ] | coworkers.ts
✔︎ *(Get User)* | GET /anwender/directory/:id | | id<br>nutzerName<br>nutzerEmail
✔︎ Get Me | GET /anwender | | [ id<br>nutzerName<br>nutzerEmail<br>adresse:{<br>id,straße,hausNummer,plz,stadt<br>}] | edit-profile.ts
✔︎ Change Profile Info | PUT /anwender | [nutzerName<br>nutzerEmail<br>{adresse}] | | edit-profile.ts
✔︎ Change Password | PUT /anwender/password | password<br>nutzerName<br>nutzerEmail | | edit-password.ts


## ShopsProvider

Name          	| Route            | Request                    | Response | Calling Files 
-----------------|------------------|-------------------------|----------|---
Get Shops | GET /betrieb<br>?size=5<br>&page=1<br>&q=filter<br>&radius=2<br>**&lat=(double)<br>&long=(double)** | size<br>page<br>q<br>radius <br>**lat<br>long**| [ **{id<br>name<br>kontaktEmail<br>tel<br>oeffnungszeiten<br>{adresse},<br>distanz}** ] | dashboard.ts<br>shops.ts
**~~Get Shops Nearby~~** | | | 
✔︎ Get My Shops | GET /anwender/betrieb | | [betrieb: {id<br>name<br>kontaktEmail<br>tel<br>oeffnungszeiten<br>{adresse}}<br>isLeiter (bool)<br>isAnwesend (bool)] | dashboard.ts<br>my-shops.ts
✔︎ Get Shop | GET /betrieb/:id | | id<br>name<br>kontaktEmail<br>tel<br>oeffnungszeiten<br>{adresse}<br>distanz | my-shop-single.ts<br>shop-info.ts<br>shop-single.ts
✔︎ Create Shop | POST /betrieb | name<br>kontaktEmail<br>tel<br>oeffnungszeiten<br>{adresse} | | shop-info.ts
✔︎ Edit Shop | PUT /betrieb/:id | name<br>kontaktEmail<br>tel<br>oeffnungszeiten<br>{adresse} | | shop-info.ts
✔︎ Get Employees | GET /betrieb/:id/mitarbeiter | | [anwenderId<br>anwesend] | my-shop-single.ts<br>service-single.ts<br>shop-single.ts
✔︎ Get Managers | GET /betrieb/:id/leiter | | [anwenderId<br>anwesend] | my-shop-single.ts
✔︎ Hire Employee | POST /betrieb/:id/mitarbeiter | anwenderId<br>betriebId<br>anwesend | | coworkers.ts<br>my-shop-single.ts
✔︎ Hire Manager | POST /betrieb/:id/leiter | anwenderId<br>betriebId<br>anwesend | | coworkers.ts<br>my-shop-single.ts
✔︎ Fire Employee | DELETE /betrieb/:id/mitarbeiter/:userID | | | my-shop-single.ts 
Fire Manager | DELETE /betrieb/:id/leiter/:userID | | | my-shop-single.ts



## ServicesProvider

Name          	| Route            | Request                    | Response | Calling Files
-----------------|------------------|-------------------------|----------|---
Get Services For | GET /betrieb/:id/dienstleistung | | [ serviceID<br>typ] | my-shop-single.ts<br>shop-single.ts
Get Service | GET /betrieb/:id/dienstleistung/:serviceID | | serviceID<br>betriebID<br>typ<br>dauer<br>kommentar | service-info.ts<br>service-single.ts
**Get Next Time Slots** | **GET /betrieb/:id/dienstleistung/mitarbeiter** | | **[<br>{<br>id<br>mitarbeiter<br>schaetzZeitpunkt<br>}<br>]** | **service-single.ts**
Get All Service Types | **GET /dlt?q=(string)<br>&page=(int)<br>&size=(int)**  | **q <br> page <br> size** | **[ {id: (long), name: (string)} ]** | service-info.ts
Create Service | POST /betrieb/:id/dienstleistung | dauer<br>typ<br>kommentar | | service-info.ts
Edit Service | PUT /betrieb/:id/dienstleistung/:serviceID | dauer<br>typ<br>kommentar | | service-info.ts




## QueuesProvider

Name          	| Route            | Request                    | Response | Calling Files
-----------------|------------------|-------------------------|----------|---
**Get Mitarbeiter Queue**     | **GET /betrieb/:id/ws** | | **{<br>wsps:<br>[<br>id<br>beginnZeitpunkg<br>next<br>anwender<br>dauer<br>dlName<br>dlId<br>]<br>schaetzEnde(timestamp)<br>}** | my-queue-single.ts
**Change Attendance** | **PUT /betrieb/:id/mitarbeiter** | **{ anwesend: (bool) }** | | **my-queue.single.ts**
**Get My Queue Position** | **GET /anwender/wsp** | | **{<br>id<br>mitarbeiter<br>betrieb<br>dlId<br>dlDauer<br>dlName<br><br>schaetzZeitpunkt<br>}** | dashboard.ts<br>my-queue-position.ts
**Line Up** | **POST /anwender/wsp** | **{ <br> dienstleistung: (long)<br>mitarbeiter: (long) <br>}** | **{<br>  id<br>  mitarbeiter<br>  betrieb<br>  dlId<br>  dlDauer<br>  dlName<br>schaetzZeitpunkt<br>}**| service-single.ts
**Leave** | **DELETE /anwender/wsp**  | | | my-queue.position.ts
**Start Work** | **PUT  /betrieb/:id/wsp/:wid**  | | | 
==End Work== | **DELETE  /betrieb/:id/wsp/:wid**  | | | 
