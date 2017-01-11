# Frontend - REST API

**Bold** - muss beim Frontend angepasst werden  
==Highlighted== - fehlt beim Backend

## AuthenticationProvider


Name          	| Route            | Request                    | Response
-----------------|------------------|-------------------------|----------
Login		 	 	| POST /auth       | nutzerName<br>password | token
Signup          | POST /anwender   | nutzerName<br>nutzerEmail<br>password|token


## UsersProvider

Name          	| Route            | Request                    | Response
-----------------|------------------|-------------------------|----------
Get Users with name | **GET /anwender/directory<br>?q=name** | q=name| [ **id**<br>nutzerName<br>nutzerEmail ]
**Get User** | **GET /anwender/directory/:id** | | **id<br>nutzerName<br>nutzerEmail**
Get Me | **GET /anwender** | | [ **id**<br>nutzerName<br>nutzerEmail<br>adresse:{<br>**id,straße**,hausNummer,plz,stadt<br>}]
Change Profile Info | **PUT /anwender** | [ nutzerName<br>nutzerEmail<br>{adresse} ] | 
Change Password | **PUT /anwender/password** | password<br>**nutzerName<br>nutzerEmail** |   


## ShopsProvider

Name          	| Route            | Request                    | Response
-----------------|------------------|-------------------------|----------
==Get Shops== | GET /betrieb<br>?size=5<br>&page=1<br>&q=filter<br>&radius=2 | size<br>page<br>q<br>radius | [ **id**<br>name<br>kontaktEmail<br>tel<br>**oeffnungszeiten**<br>{adresse}<br>distanz ] 
**~~Get Shops Nearby~~** | | | 
==Get My Shops== | **GET /anwender/betrieb** | | [ **id**<br>name<br>kontaktEmail<br>tel<br>**oeffnungszeiten**<br>{adresse}<br>distanz ] 
Get Shop | GET /betrieb/:id | | **id**<br>name<br>kontaktEmail<br>tel<br>**oeffnungszeiten**<br>{adresse}<br>distanz 
Create Shop | POST /betrieb | name<br>kontaktEmail<br>tel<br>**oeffnungszeiten**<br>{adresse} | 
Edit Shop | PUT /betrieb/:id | name<br>kontaktEmail<br>tel<br>**oeffnungszeiten**<br>{adresse} | 
Get Employees | GET /betrieb/:id/mitarbeiter | | [ **~~betriebID~~**<br>**anwenderId**<br>anwesend<br>**~~nutzerName~~** ]
Get Managers | GET /betrieb/:id/leiter | | [ **~~betriebID~~**<br>**anwenderId**<br>anwesend<br>**~~nutzerName~~** ]
Hire Employee | POST /betrieb/:id/mitarbeiter | **anwenderId<br>betriebId<br>anwesend** | 
Hire Manager | POST /betrieb/:id/leiter | **anwenderId<br>betriebId<br>anwesend** | 
Fire Employee | DELETE /betrieb/:id/mitarbeiter/:userID | | 
Fire Manager | DELETE /betrieb/:id/leiter/:userID |



## ServicesProvider

Name          	| Route            | Request                    | Response
-----------------|------------------|-------------------------|----------
Get Services For | GET /betrieb/:id/dienstleistung | | [ serviceID<br>**~~betriebID~~**<br>typ<br>**~~dauer~~**<br>**~~kommentar~~** ]
Get Service | GET /betrieb/:id/dienstleistung/:serviceID | | serviceID<br>betriebID<br>typ<br>dauer<br>**kommentar**
Get All Service Types | **POST /dlts** ?? | | [ *strings* ] 
Create Service | POST /betrieb/:id/dienstleistung | dauer<br>typ<br>**kommentar**
Edit Service | PUT /betrieb/:id/dienstleistung/:serviceID | dauer<br>typ<br>**kommentar** | 




## QueuesProvider

Name          	| Route            | Request                    | Response
-----------------|------------------|-------------------------|----------
==Get My Queues== | **GET /anwender/queues** | | [ queueID<br>name (*betrieb*) <br>dienstleistung<br>wsoffen ]
==Get Queue==     | **GET /anwender/queues/:queueID** | | queueID<br>betriebID<br>serviceID<br>**~~name~~**<br>dienstleistung<br>wsoffen<br>[ kunden:<br> {nutzerName<br> userID<br> uhrzeit}<br>]
==Get My Queue Position== | **GET /anwender/queueposition** | | uhrzeit<br>mitarbeiterID<br>betriebID<br>positionID
==Line Up== | POST /queues | userID<br>dienstleistungID<br>mitarbeiterID | 
==Leave== | DELETE /queues/:queueID | | 
