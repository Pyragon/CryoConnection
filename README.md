# CryoConnection
Provides an easy to use library for querying MySQL databases.

# README IS A WIP CURRENTLY!

## Setup
You can get the JAR from the Releases page, or follow the instructions below to add via Maven.

A properties file must be present within a 'data' folder with the following values present:

```json
{
  "db-host": "",
  "db-user": "",
  "db-pass": "",
  "db-port": ""
}
```

## Getting Started
To get started, create an instance of [ConnectionManager](src/main/java/com/cryo/ConnectionManager.java).

To perform queries on a database, retrieve a copy of [DBConnection](src/main/java/com/cryo/DBConnection.java) through 
```java
ConnectionManager.getConnection(schema);
```

DBConnection contains helper methods for most mainstream SQL commands. Examples below.

## Selecting from a database
Selection from a database is done through reflection, returning a DAO (Data Access Object) you provide containing the needed data from the database.

For example, for selecting a basic 'Person' from the database.

We can setup our table like so:
```sql
CREATE DATABASE IF NOT EXISTS `test_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `test_db`;

CREATE TABLE IF NOT EXISTS `people` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `birth_date` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

Then we create a corresponding DAO within our server:
```java
package com.cryo.entities;

import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class Person extends MySQLDao {

    @MySQLDefault
    private final int id;
    @MySQLRead
    private String firstName;
    @MySQLRead("last_name")
    private String lastNameDiffName;
    private final Date birthDate;

}
```
Our project uses lombok, you can choose not to use it if you prefer.

Your class MUST have a constructor that matches your database and in the correct order.
In our example above, we can keep the ```@AllArgsConstructor``` as all variables in our class need to be read.

The ```@MySQLDefault``` annotation and ```MySQLDao``` class will be explained below in the [Inserting](#inserting) section.

Once we have everything setup, we can now begin selecting data from our table using the following example:

```java
package com.cryo.examples;

import com.cryo.ConnectionManager;
import com.cryo.DBConnection;
import com.cryo.entities.Person;

import java.util.ArrayList;

public class RetrievePerson {

    public static void main(String[] args) {
        //Creating an instance of ConnectionManager
        ConnectionManager manager = new ConnectionManager();

        //Getting an instance of DBConnection using the 'test_db' database
        DBConnection connection = manager.getConnection("test_db");

        //Selecting a person from the database with the name 'cody'
        Person person = connection.selectClass("people", "first_name=?", Person.class, "cody");

        //Selecting a person from the database with the first name 'cody' and the last name 'smith'
        person = connection.selectClass("people", "first_name=? AND last_name=?", Person.class, "cody", "smith");

        //Selecting a list of people from the database with the first name's 'cody'
        ArrayList<Person> people = connection.selectList("people", "first_name=?", Person.class, "cody");

        //Selecting a list of people from the database limited to 10
        people = connection.selectList("people", null, "LIMIT 10", Person.class, null);

        //selecting a count of people in database with the first name 'cody'
        int count = connection.selectCount("people", "first_name=?", "cody");
    }

}
```
Parameters and arguments for our selecting methods can be found [here](src/main/java/com/cryo/DBConnection.java). (Line 81)

Notes: Leaving some arguments out may result in an error with ambiguous methods. Just use 'null' if this occurs.

```@MySQLRead``` annotation: 
* When selecting from a database, our select method only takes into effect the variables with a final modifier. Should you need for a variable that is not final to be read from the database, you can include this annotation on the variable, and the library will read the variable as well.
* The library gets the column names by converting the variable name from camelCase to snake_case. If a variable does not directly correspond this way, you can include a ```name``` value. The library will use this name instead.

## Inserting

Now, we go in the opposite direction.

To explain the ```@MySQLDefault``` annotation and ```MySQLDao``` class used above:

```@MySQLDefault``` is an annotation used for when we want the library to simply insert 'DEFAULT' into the query to allow MySQL to handle default values itself.
Examples can be seen below.

```MySQLDao``` is an abstract class we can extend from all of our DAOs. It provides one method, ```data()``` that uses reflection to create an Object array containing all values to be inserted. Note: The same reasons above that may cause errors with variables also apply. (Final modifiers and the ```@MySQLRead``` annotation)

The ```data()``` method can be directly passed into the insert method for very easy inserting.

```java
package com.cryo.examples;

import com.cryo.ConnectionManager;
import com.cryo.DBConnection;
import com.cryo.entities.Person;

import java.sql.Date;
import java.util.Calendar;

public class InsertPerson {

    public static void main(String[] args) {
        //Creating an instance of ConnectionManager
        ConnectionManager manager = new ConnectionManager();

        //Getting an instance of DBConnection using the 'test_db' database
        DBConnection connection = manager.getConnection("test_db");

        //Creating a new instance of Person. We leave the id as -1, as this will get inserted into the database as just 'DEFUALT'
        Person person = new Person(-1, "cody", "smith", getBirthDate());
        
        //Inserting our person into the database using the data() method taken from MySQLDao
        connection.insert("people", person.data());
    }
    
    public static Date getBirthDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1995);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.DAY_OF_MONTH, 26);
        cal.set(Calendar.HOUR, 1);
        cal.set(Calendar.MINUTE, 23);
        return new Date(cal.getTimeInMillis());
    }
}
```