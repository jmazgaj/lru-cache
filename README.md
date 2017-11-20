# lru-cache
Java based implementation of LRU cache as microservice with REST API.

## Installation
Clone the repository and run **mvn clean install package** in the directory where pom.xml is located.

Then navigate to **target** directory and run `java -jar lru-cache-1.0.jar` which will startup _Spring Boot_ application.

## Customisation
To change default properties edit **src/resources/application.properties** file and then repeat steps form **Installation** section.
* **server.port** is used to set port used by application (default: 8080).
* __logging.level.*__ are used to set logging level (default: INFO).
* **logging.file** stores file name for logging (default: lru-cache.log).
* **lru.cache.size** is responsible for initial cache capacity (default: 5).

## Usage
Application serves few endpoints allowing cache usage.
* **/get/\{key\}** - Uses HTTP GET Method. Returns value stored under _key_, nothing if the value for _key_ is not set. 
* **/put/\{key\}** - Uses HTTP POST Method. Puts the value from the request body to cache under specified _key_.
* **/changeCapacity** - Uses HTTP PUT Method. Changes cache capacity based on request body.

Any text alike values should be posted as raw text in request body for **/put** operation.

Tip: Application provides support for uploading files (maximum file limit is 1MB).
To upload file for caching put it in a POST request body under key named **'file'** (eg. standard HTML form with file field). Then you can simply /get it with the key you provided.

### Design
_**LauncherClass**_ contains `static void main` method for Spring Boot application.
_**LRUController**_ class serves the endpoints and uses _**LRUService**_ for caching operations.
_**LRUServiceImpl**_ implements _**LRUService**_ and contains base implementation for LRU cache.
_**BackgroundService**_ runs every 10 seconds a helper cache clearing method from _**LRUService**_.
_**GlobalExceptionHandler**_ provides proper exception handling for exceeding file size. 

#### Future improvements
- Ability to change maximum file size by application property. 
- Overriding default properties by providing specified VM parameters at startup.
- Switching to new log file daily with date pattern.
- Authentication and separate cache per user (also service for app properties per user).

