package com.cryo.examples;

import com.cryo.ConnectionManager;
import com.cryo.DBConnection;

public class DeletePerson {

    public static void main(String[] args) {
        //Creating an instance of ConnectionManager
        ConnectionManager manager = new ConnectionManager();

        //Getting an instance of DBConnection using the 'test_db' database
        DBConnection connection = manager.getConnection("test_db");

        //Delete person with the id 1
        connection.delete("people", "id=?", 1);

        //Delete everyone with the first name 'cody'
        connection.delete("people", "first_name=?", "cody");
    }

}
