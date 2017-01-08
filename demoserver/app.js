/**
 * a dirty implementation of a fake Node server
 * used for demo purposes for the NoQueue frontend
 * 
 * based on a template provided by Prof. Dr.-Ing. Johannes Konert
 */
"use strict";  

var path = require('path');
var express = require('express');
var bodyParser = require('body-parser');

var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.use(function(req, res, next) {
    console.log('Request of type '+req.method + ' to URL ' + req.originalUrl);
    next();
});

// Dummy Data Collections
var id = 0;

function createShop(name, phone, email, openingHours, street, streetNr, zip, city, distance){
    id += 1;
    return {
        betriebID: id,
        name: name,
        tel: phone,
        kontaktEmail: email,
        oeffnungsZeiten: openingHours,
        adresse : {
            strasse: street,
            hausNummer: streetNr,
            plz: zip,
            stadt: city
        },
        distanz: distance
    }
}

var shops = [   createShop("Salon Marko", "01577-368-1252", "contact@salonmarco.de", "08:00 - 17:00 Mo - Sa", "Milcshtr", "2", "13555", "Berlin", "2100"),
                createShop("Frisur für Dich!", "01686-125-5552", "frisurfuerdich@gmail.com", "09:00 - 15:00 Mo - Fr", "Musterstr", "52", "27555", "Hamburg", "14200"),
                createShop("Beauty Salon Nature", "01578-3512-444", "beautysalonnature@gmx.de", "08:00 - 18:00 Mo - Do", "Schönstr", "21", "39952", "Magdeburg", "20500"),
                createShop("Studio Schöneberg", "01562-555-5125", "studioschoneberg@gmx.de", "08:00 - 13:00 Mo, Mi, Do 14:00 - 18:00 Di", "Rathaus Schöneberg", "12", "13071", "Berlin", "800"),
                createShop("Nagelstudio Tempelhof", "01528-8812-234", "kontakt@nagelstudiotempelhof.de", "09:00 - 18:00 Mo - Fr", "Tempelhofer Damm", "241A", "12827", "Berlin", "4200"),
                createShop("China Massage", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("Shop ABC", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("Alfredos", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("Friseursalon X", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("That special place.", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("Asia Salon", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("Beste Massage!", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("Eine nicht so gute Massage", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("Mann, ist das lustig", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272"),
                createShop("Fahrrad-Reparatur", "01572-244-1252", "massagechina@gmx.de", "10:00 - 16:00 Mo-Do", "Massagestr.", "15B", "13005", "Berlin", "1272")

            ];


function createCoworker(betriebID, anwesend, nutzerName){
    id += 1;
    return {
        betriebID: betriebID,
        userID: id,
        anwesend: anwesend,
        nutzerName: nutzerName
    }
}

var mitarbeiter = [ createCoworker(1, true, "Michael"),
                    createCoworker(1, true, "Angela"),
                    createCoworker(1, false, "Philipp"),
                    createCoworker(1, true, "Sascha"),
                    createCoworker(2, true, "Oliver"),
                    createCoworker(3, true, "Ursula"),
                    createCoworker(4, true, "Martin"),
                    createCoworker(5, true, "Johanna"),
                    createCoworker(6, true, "Li")
                  ];

var leiter = [      createCoworker(1, false, "Milan"),
                    createCoworker(1, true, "Marko"),
                    createCoworker(2, false, "Sebastian"),
                    createCoworker(3, false, "Viktoria"),
                    createCoworker(4, false, "Ralf"),
                    createCoworker(5, false, "Günther"),
                    createCoworker(6, false, "Xin")
                  ];

function createService(shopID, typ, dauer, beschreibung = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut."){
    id += 1;
    return {
        serviceID: id,
        betriebID: shopID,
        typ: typ,
        dauer: dauer,
        beschreibung: beschreibung
    }
}

var services = [    createService(1, "Frisur", 20),
                    createService(1, "Färben", 50),
                    createService(2, "Frisur", 30),
                    createService(3, "Pediküre", 40),
                    createService(4, "Gelnägel", 50),
                    createService(5, "Maniküre", 60),
                    createService(6, "Massage", 45)   
               ];

var servicesTypes = ["Frisur", "Färben", "Pediküre", "Maniküre", "Massage", "Gelnägel", "Thai Massage", "Aromaöl Massage", "Wimpernwelle", "Styling"];

function makeQueue(betriebID, serviceID, offen){
    id += 1;

    return {
        queueID: id,
        betriebID: betriebID,
        serviceID: serviceID, 
        name: shops[betriebID].name,
        adresse: shops[betriebID].adresse,
        dienstleistung: services[betriebID].typ,
        wsoffen: offen,
        kunden: [
            {nutzerName: "patrick_1", uhrzeit: "10:00"},
            {nutzerName: "258dumbo", uhrzeit: "10:30"},
            {nutzerName: "bartsimpson", uhrzeit: "11:00"},
        ]
    }
}

var myQueues = [makeQueue(1, 7, true), makeQueue(2, 8, false)];

var myQueuePosition = {
    id: 1,
    uhrzeit: "11:00",
    mitarbeiter: "Michael",
    adresse: "Milcshtr 2 | 13555 Berlin",
    distanz: 2100,
    platzNummer: "011",
    tel: "01577-368-1252",
    betriebName: "Salon Marco"
};

function createUser(name, email, zip = "", city = "",  street = "", streetnr = ""){
    id += 1;
    return {
        userID: id,
        nutzerName: name,
        nutzerEmail: email,
        adresse: {
            plz: zip,
            stadt: city,
            strasse: street,
            hausNummer: streetnr
        }
    }
}

var users = [   createUser("otto_schmelzer", "ottoschmelzer@gmx.de"),
                createUser("rennate-bausch122", "rbausch@gmail.com"),
                createUser("robert-dimitrov", "robertdimitrov@example.org", "12683", "Berlin", "Oberfeldstr.", "132"),
                createUser("norbert210", "norbert210@email.de"),
                createUser("sandra-stamm", "sandra210@example.org"),
                createUser("sandra-bernstein12", "sandrabernstein@email.org")
];

var token = "eyJhbGciOiJIUzI1NiJ9.eyJiIjoidGVzdCJ9.s5IHuxC-TfFLX8Nu_t_RJoMugvYX_7dVCYShpMWmxS4";


// Routes ***************************************

var authRouter = express.Router();

authRouter.post('/', function(req, res, next){
    res.status(200);
    res.json(token);
});

app.use("/auth", authRouter);

var anwenderRouter = express.Router();

anwenderRouter.post('/', function(req,res,next){
    res.status(200);
    res.json(token);
});

anwenderRouter.get('/:id/betrieb', function(req,res,next){
    res.status(200).json([shops[0]]);
});

anwenderRouter.get('/:id/queues', function(req, res, next){
    res.status(200).json(myQueues);
});

anwenderRouter.get('/:id/queues/:qid', function(req, res, next){
    res.status(200).json(myQueues[req.params.qid-1]);
});

anwenderRouter.get('/:id/queueposition', function(req, res, next){
    console.log("------", myQueuePosition);
    res.status(200).json(myQueuePosition);
});

anwenderRouter.get('/', function(req, res, next){
    let searchTerm = req.query.q;
    let result = users.filter( u => u.nutzerName.indexOf(searchTerm) > -1);
    res.status(200).json(result);
});

anwenderRouter.get('/:me', function(req, res, next){
    res.status(200).json(users[2]);
});

anwenderRouter.put('/:id', function(req, res, next){
    res.status(200).end();
});

anwenderRouter.patch('/:id', function(req, res, next){
    res.status(200).end();
});

app.use("/anwender", anwenderRouter);



var shopsRouter = express.Router();

shopsRouter.get('/', function(req,res,next){
    let size = parseInt(req.query.size);
    let page = parseInt(req.query.page);
    let start = size * (page - 1);
    let end = start + size; 

    console.log("returning shops: " + start + " to " + end);

    let result = [];
    for(var i = start; i < end; i++){
        if(i < shops.length){
            result.push(shops[i]);
        }
    } 
    res.status(200);
    res.json(result);
});

shopsRouter.get("/:id", function(req, res, next){
    var result = shops.filter( s => s.betriebID === Number(req.params.id))[0];
    res.status(200).json(result);
});

shopsRouter.get("/:id/mitarbeiter", function(req,res,next){
    var result = mitarbeiter.filter( m => m.betriebID === Number(req.params.id));
    res.status(200).json(result);
});

shopsRouter.get("/:id/leiter", function(req,res,next){
    var result = leiter.filter( l => l.betriebID === Number(req.params.id));
    res.status(200).json(result);
});

shopsRouter.post('/:id/mitarbeiter', function(req, res, next){
    res.status(200).end();
});

shopsRouter.post('/:id/leiter', function(req, res, next){
    res.status(200).end();
});

shopsRouter.delete('/:id/mitarbeiter', function(req, res, next){
    res.status(200).end();
});

shopsRouter.delete('/:id/leiter', function(req, res, next){
    res.status(200).end();
});

shopsRouter.post('/', function(req, res, next){
    res.status(200).end();
});

shopsRouter.patch('/:id', function(req,res,next){
    res.status(200).end();
});

shopsRouter.put('/:id', function(req,res,next){
    res.status(200).end();
});

shopsRouter.get('/:id/dienstleistung', function(req, res, next){
    var result = services.filter(s => s.betriebID === Number(req.params.id));
    res.status(200).json(result);
});

shopsRouter.get('/:id/dienstleistung/:dlid', function(req, res, next){
    var result = services.filter(s => s.betriebID === Number(req.params.id))[0];
    res.status(200).json(result);
});

app.use("/betrieb", shopsRouter);



var servicesRouter = express.Router();

servicesRouter.post('/', function(req, res, next){
    res.status(200).end();
});

servicesRouter.patch('/:dlid', function(req, res, next){
    res.status(200).end();
});

servicesRouter.put('/:dlid', function(req, res, next){
    res.status(200).end();
});



app.use('/betrieb/:id/dienstleistung', servicesRouter);



var dltsRouter = express.Router();
dltsRouter.get('/', function(req,res,next){
    res.status(200).json(servicesTypes);
});
app.use('/dlts', dltsRouter);


var queuesRouter = express.Router();
queuesRouter.use('/', function(req,res,next){
    res.status(200).end();
});
app.use('/queues', queuesRouter);


// CatchAll for the rest (unfound routes/resources) ********

// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers (express recognizes it by 4 parameters!)

// development error handler
// will print stacktrace as JSON response
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        console.log('Internal Error: ', err.stack);
        res.status(err.status || 500);
        res.json({
            error: {
                message: err.message,
                error: err.stack
            }
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.json({
        error: {
            message: err.message,
            error: {}
        }
    });
});


// Start server ****************************
app.listen(3000, function(err) {
    if (err !== undefined) {
        console.log('Error on startup, ',err);
    }
    else {
        console.log('Listening on port 3000');
    }
});