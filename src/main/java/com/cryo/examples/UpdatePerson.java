package com.cryo.examples;

import com.cryo.ConnectionManager;
import com.cryo.DBConnection;
import com.cryo.entities.Person;

public class UpdatePerson {

    public static void main(String[] args) {
        //Creating an instance of ConnectionManager
        ConnectionManager manager = new ConnectionManager();

        //Getting an instance of DBConnection using the 'test_db' database
        DBConnection connection = manager.getConnection("test_db");

        //Retrieve our person from the database, we'll assume there is a person with id=1 in the database
        Person person = connection.selectClass("people", "id=?", Person.class, 1);

        //Our person has changed their name!
        person.setFirstName("John");
        person.setLastName("Poggers");

        //First method. Manually updating using set, more effective imo when only updating a few variables
        connection.set("people", "first_name=?, last_name=?", "id=?", person.getFirstName(), person.getLastName(), 1);

        //Second method. Using reflection, more effective imo when updating a large number of variables
        //Takes in the where clause, the object containing the changed data, an array of the variable names you wish to change, and the values from the where clause
        connection.update("people", "id=?", person, new String[] { "firstName", "lastNameDiffName" }, 1);
    }
}
