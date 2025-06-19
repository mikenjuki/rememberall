package com.remberall.remberall.test;

import com.remberall.remberall.model.Person;
import com.remberall.remberall.model.PersonDAO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDAOTest {

    @Test
    public void testInsertAndGetAllPersons() {
        PersonDAO dao = new PersonDAO();
        Person p = new Person(0, "Test User", LocalDate.of(2000, 1, 1), "Notes",
                List.of("Reading", "Coding"), LocalDate.now(), Person.RelationshipType.FRIEND);

        boolean success = dao.insertPerson(p);
        assertTrue(success);

        List<Person> all = dao.getAllPersons();
        assertTrue(all.stream().anyMatch(person -> person.getName().equals("Test User")));
    }
}