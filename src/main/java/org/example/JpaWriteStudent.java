package org.example;

import org.example.model.Student;
import org.example.model.StudentGroup;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class JpaWriteStudent {
    public static void main(String[] args) {
        // create a new student instance
        Student student1 = new Student();
        student1.setName("Lee");
        try {
            student1.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01"));
        }
        catch (ParseException e) {}
        student1.setStudentGroup(StudentGroup.DAISY);


        // create a new student instance
        Student student2 = new Student();
        student2.setName("Amal");
        try {
            student2.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("1980-01-01"));
        }
        catch (ParseException e) {}
        student2.setStudentGroup(StudentGroup.LOTUS);


        // create EntityManager
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // access transaction object
        EntityTransaction transaction = entityManager.getTransaction();

        // create and use transactions
        transaction.begin();
        entityManager.persist(student1);
        entityManager.persist(student2);
        transaction.commit();

        //close entity manager and factory
        entityManager.close();
        entityManagerFactory.close();
    }
}

