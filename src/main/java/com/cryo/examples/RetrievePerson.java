package com.cryo.examples;

import com.cryo.ConnectionManager;
import com.cryo.DBConnection;
import com.cryo.entities.Person;

import java.util.ArrayList;

public class RetrievePerson {

    public static void main(String[] args) {
        //Creating an instance of ConnectionManager. This should only be done once in your project to ensure proper connection pooling.
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
