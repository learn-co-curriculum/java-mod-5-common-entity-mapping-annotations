package org.example;

import org.example.model.Student;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaReadStudent {
    public static void main(String[] args) {
        // create EntityManager
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // get student data using primary key id=1
        Student student1 = entityManager.find(Student.class, 1);
        System.out.println(student1);

        // close entity manager and factory
        entityManager.close();
        entityManagerFactory.close();
    }
}
