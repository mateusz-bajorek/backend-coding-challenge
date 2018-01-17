##Prerequisites
Start MySQL server, login, create new database and user:
```mysql
create database solution;
create user 'solution'@'localhost' IDENTIFIED by 'solution';
grant all on solution.* to 'solution'@'localhost'; 
```
If needed, MySQL server details can be changed in `solution/src/main/resources/application.properties`, 
property `spring.datasource.url`.

##Compile
```bash
./mvnw package
```

##Run
```bash
java - jar target/solution-0.0.1-SNAPSHOT.jar
```

##Details
The application is written in latest stable Spring Boot (`1.5.9.RELEASE`). Most of the services come with tests.

Application uses following techniques/frameworks/libraries:
-  `liquibase` - for database migration. Upon first run the tool will create all the needed tables and insert necessary 
data into them.
- `spring security` - for securing the application. I decided to restrict access to the application with 
username:\<encrypted password> combination, but for the sake of the test I added a dummy authentication filter that 
automatically authenticates user with hardcoded details. `JSESSIONID` is returned to the client, AngularJS reuses it in 
a cookie - which makes the app require authentication only once per session. Server CORS are set to enable access only
from `localhost:8080` which is the address of the started gulp server.
- `joda-money` - for currency manipulation
- `YahooFinanceAPI` - for access to an API with real-live exchange rates
- `AspectJ` - for AOP logging
- `JUnitParams` - for easy testing of multiple variable scenarios without duplication
- `Mockito` & `AssertJ` - for mocking beans and fluent assertions

###Tasks covered:
- User story 1: user can save new expenses. Extras:
  - Angular code modified to use HTML5 datepicker
  - Display server validation errors (e.g. date set in the future)
- User story 2: user can see list of submitted expenses with their details
- User story 3: save expenses in euros. Extras:
  - Any valid currency code is accepted, in any of the combinations with amount like `EUR100`, `200 PLN` etc.
  - Show message for invalid currency submitted
  - Show GBP equivalent in list of submitted expenses table, together with currency symbol if possible
  - Not submitting any currency in client defaults to GBP
- User story 4: calculate VAT in real time
