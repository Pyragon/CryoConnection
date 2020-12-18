package com.cryo.examples;

import com.cryo.ConnectionManager;
import com.cryo.DBConnection;
import com.cryo.entities.Person;

import java.sql.Date;
import java.util.Calendar;

public class InsertPerson {

    public static void main(String[] args) {
        //Creating an instance of ConnectionManager. This should only be done once in your project to ensure proper connection pooling.
        ConnectionManager manager = new ConnectionManager();

        //Getting an instance of DBConnection using the 'test_db' database
        DBConnection connection = manager.getConnection("test_db");

        //Creating a new instance of Person. We leave the id as -1, as this will get inserted into the database as just 'DEFUALT'
        Person person = new Person(-1, "Cody", "Smith", getBirthDate());

        //Inserting our person into the database using the data() method taken from MySQLDao and returning the id
        int id = connection.insert("people", person.data());
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
