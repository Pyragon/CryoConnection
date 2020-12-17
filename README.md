# CryoConnection
Provides an easy to use library for querying MySQL databases.

#README IS A WIP CURRENTLY!

##Setup
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
ConnectionManager.getConnection(schema)
```

DBConnection contains many helper methods most mainstream SQL commands. Examples below.

##Selecting from a database
Selection from a database is done through reflection, returning a DAO (Data Access Object) you provide containing the needed data from the database.

For example, for selecting a basic 'Person' from the database.

We can setup our table like so:
```sql
CREATE TABLE `people` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`first_name` VARCHAR(50) NOT NULL,
	`last_name` VARCHAR(50) NOT NULL,
	`birth_date` DATE NOT NULL,
	PRIMARY KEY (`id`)
);
```

Then we create a corresponding DAO within our server:
```java
package com.cryo.entities;

import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class Person {

    @MySQLDefault
    private final int id;
    private final String firstName;
    private final String lastName;
    private final Date birthDate;

}

```
Our project uses lombok, you can choose not to use it if you prefer.

The ```@MySQLDefault``` annotation will be explained below in the [Inserting](#inserting) section.

## Inserting
