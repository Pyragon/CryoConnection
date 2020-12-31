package com.cryo.examples;

import com.cryo.ConnectionManager;
import com.cryo.DBConnection;
import com.cryo.entities.Person;
import com.cryo.entities.PersonCustom;

import java.util.ArrayList;

public class LoadClassCustom {

    public static void main(String[] args) {
        //Creating an instance of ConnectionManager. This should only be done once in your project to ensure proper connection pooling.
        ConnectionManager manager = new ConnectionManager();

        //Getting an instance of DBConnection using the 'test_db' database
        DBConnection connection = manager.getConnection("test_db");

        //Load an instance of the class containing a custom 'loadClass' method
        PersonCustom custom = connection.selectClass("people", "first_name=?", PersonCustom.class, "cody");

    }
}
