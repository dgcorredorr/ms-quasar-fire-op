// Importar el módulo 'fs' para operaciones de sistema de archivos
const fs = require('fs');

// Obtener la cadena de conexión y el nombre de la base de datos desde la variable de entorno MONGO
const mongoConnectionString = process.env.MONGO || "mongodb://mongodb:27017,mongodb:27018,mongodb:27019/DBTemplateSpringBoot_DE?replicaSet=rs0&readPreference=primary";
const dbName = mongoConnectionString.split('/').pop(); // Extraer el nombre de la base de datos

// Conectar a MongoDB usando la cadena de conexión completa
const db = connect(mongoConnectionString);

// Seleccionar la base de datos usando el nombre extraído
const database = db.getSiblingDB(dbName);

// Leer y reemplazar documentos en 'coll_message' con los datos del archivo JSON
const collMessageData = fs.readFileSync('/usr/src/app/db-scripts/coll_message.json', 'utf8');
const messages = JSON.parse(collMessageData);
database.coll_message.deleteMany({}); // Eliminar todos los documentos en 'coll_message'
database.coll_message.insertMany(messages); // Insertar los nuevos documentos
print('Datos reemplazados en coll_message.');

// Leer y reemplazar documentos en 'coll_param' con los datos del archivo JSON
const collParamData = fs.readFileSync('/usr/src/app/db-scripts/coll_param.json', 'utf8');
const params = JSON.parse(collParamData);
database.coll_param.deleteMany({}); // Eliminar todos los documentos en 'coll_param'
database.coll_param.insertMany(params); // Insertar los nuevos documentos
print('Datos reemplazados en coll_param.');
