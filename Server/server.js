var MongoClient = require('mongodb').MongoClient
	, ObjectID = require('mongodb').ObjectID
    , format = require('util').format;

var http = require('http'),
	express = require('express'),
	path = require('path');
	fs = require('fs');

var uri = 'mongodb://admin:Proyecto2@ds043947.mongolab.com:43947/proyecto2'
	
var app = express();
app.set('port', process.env.PORT || 3000);
app.set('view engine', 'jade');
app.use(express.static(path.join(__dirname, 'public')));



app.use(express.json());       // to support JSON-encoded bodies
app.use(express.urlencoded()); // to support URL-encoded bodies


//Web service post feed
app.post('/guardar', function (req, res) {
	var feed = req.body;
	postFeed(feed, function(results){
	res.send(results);
	})
});


//Post feed en DB
function postFeed(feed, fn){
	MongoClient.connect(uri, function(err, db) {
	
		if(err) throw err;
		
		var collection = db.collection('feed');
		collection.insert(feed, function(err, docs) {
			if (err) fn("Ha ocurrido un error: " + err);
			collection.count(function(err, count) {
				fn("El feed " + feed.nombre + " ha sido creado satisfactoriamente.");
				console.log(format("New feed inserted. count = %s", count));
				db.close();
			});
		});
		
	});
}


//Web service get feeds
app.get('/feeds', function (req, res) {
	getFeeds(function(results){
	res.send({'source': results});
	})
});


//Get feeds de DB
function getFeeds(fn){
	MongoClient.connect(uri, function(err, db) {
	
		if(err) throw err;
		
		var collection = db.collection('feed');
		collection.find().toArray(function(err, results) {
			fn(results);
			db.close();
		});
		
	});
}


//Web service get cats
app.get('/cats', function (req, res) {
	getCats(function(results){
	res.send({'categorias': results});
	})
});


//Get categorias de DB
function getCats(fn){
	MongoClient.connect(uri, function(err, db) {
	
		if(err) throw err;
		
		var collection = db.collection('feed');
		collection.distinct('categoria', function(err, docs){
			fn(docs);
			db.close();
		});
		
	});
}



//Web service get feeds por categoria
app.get('/feeds/:search', function (req, res) {
	var params = req.params.search.split(",");
	
	for (var i = 0; i < params.length; i++){
		params[i] = params[i].replace("+", " ");
		params[i] = params[i].trim();
	}
	
	searchFeeds(params, function(results){
	res.send({'source': results});
	})
});


//Buscar feeds de DB por categoría
function searchFeeds(params, fn){
	MongoClient.connect(uri, function(err, db) {
		if(err) throw err;
		
		var collection = db.collection('feed');
		collection.find( {'categoria': {'$in': params}}).toArray(function(err, results) {
			fn(results);
			// Let's close the db
			db.close();
		});
		
	});
}



/*
//Web service get eventos.html búsqueda
app.get('/eventos.html/:search?', function (req, res) {
	res.sendfile('public/eventos.html');
});



//Web service get evento.html
app.get('/evento.html/:id?', function (req, res) {
	res.sendfile('public/evento.html');
});


//Web service get modificar.html
app.get('/modificar.html/:id?', function (req, res) {
	res.sendfile('public/modificar.html');
});


//Web service get evento
app.get('/evento/:id?', function (req, res) {
	var oid = req.params.id;
	getEvent(oid, function(result){
	res.send(result);
	})
});


//Web service modificar evento
app.post('/modificar', function (req, res) {
	var event = req.body;
	updateEvent(event, function(results){
	res.send(results);
	})
});



//Web service post usuario
app.post('/crearusuario', function (req, res) {
	var user = req.body;
	postUser(user, function(results){
	res.send(results);
	})
});


//Web service validar usuario
app.post('/validarusuario', function (req, res) {
	var user = req.body;
	validateUser(user, function(results){
	res.send(results);
	})
});


//Get evento de DB
function getEvent(oid, fn){
	MongoClient.connect(uri, function(err, db) {
	
		if(err) throw err;
		
		var collection = db.collection('evento');
		collection.findOne({"_id": ObjectID.createFromHexString(oid)},function(err, result) {
			fn(result);
			// Let's close the db
			db.close();
		});
		
	});
}



//Update evento en DB
function updateEvent(event, fn){
	MongoClient.connect(uri, function(err, db) {
	
		if(err) throw err;
		
		var collection = db.collection('evento');
		collection.update({nombre: event.nombre}, event, function(err, docs) {
			if (err) fn("Ha ocurrido un error: " + err);
			collection.count(function(err, count) {
				fn("El evento " + event.nombre + " ha sido modificado satisfactoriamente.");
				console.log(format("Event updated. count = %s", count));
				db.close();
			});
		});
		
	});
}


//Post usuario en DB
function postUser(user, fn){
	MongoClient.connect(uri, function(err, db) {
	
		if(err) throw err;
		
		var collection = db.collection('usuario');
		collection.insert(user, function(err, docs) {
			if (err) fn("Ha ocurrido un error: " + err);
			collection.count(function(err, count) {
				fn("El usuario " + user.username + " ha sido creado satisfactoriamente.");
				console.log(format("New user inserted. count = %s", count));
				db.close();
			});
		});
		
	});
}

//Post usuario en DB
function validateUser(user, fn){
	MongoClient.connect(uri, function(err, db) {
		if(err) throw err;
		
		var collection = db.collection('usuario');
		collection.findOne(user,function(err, result) {
			if (result){
				fn(result);
				console.log(format("User %s logged in", result.username));
			}
			else{
				console.log("Login refused to " + user.username);
				fn("Credenciales inválidos")
			}
			// Let's close the db
			db.close();
		});
		
	});
}

*/


http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});



