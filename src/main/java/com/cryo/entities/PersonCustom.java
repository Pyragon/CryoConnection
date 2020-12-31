package com.cryo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.ResultSet;
import java.util.Date;

@AllArgsConstructor
@Data
public class PersonCustom extends MySQLDao {

    @MySQLDefault
    private final int id;
    @MySQLRead
    private String firstName;
    @MySQLRead("last_name")
    private String lastNameDiffName;
    private final Date birthDate;

    //For readability in certain examples
    public void setLastName(String lastName) {
        this.lastNameDiffName = lastName;
    }

    public String getLastName() {
        return lastNameDiffName;
    }

    public static PersonCustom loadClass(ResultSet set) {
        return new PersonCustom(-1, "Test", "Test", new Date());
    }

}
