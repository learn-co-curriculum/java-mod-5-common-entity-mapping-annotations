# Common Entity Mapping Annotations

## Learning Goals

- Explore various entity mapping annotations.
- Use `@Temporal` and `@Enumerated` for data types such as Date and Enums.
- Use `@Transient` to avoid persisting a property.
- Use `@GeneratedValue` to automatically generate primary key values 

## Introduction

We have looked at the `@Entity`, `@Table`, and `@Id` annotations from the
`javax.persistence` package so far. In this lesson we will go over how to
customize these annotations and learn about new annotations. It is important to
know about different annotations so you can properly create database schemas
using entities.

We will be using the `Student` class to check out some of the customizations
that are available to us. We will add new properties to this model which will be
carried forward to the next lessons so make sure your `Student` class code
matches the code at the end of the lesson.

## Column Customization

A property of a class (i.e. instance variable) is automatically mapped to a table column but
sometimes we may have to customize how the property is mapped .

### @Basic

The `@Basic` annotation is the default behavior for entity annotations. This
annotation uses the property name as the column name and infers the database
data type from the Java data type.

```java
@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    private int id;

    @Basic
    private String name;

    // getters and setters
}
```

Since `@Basic` is the default, we can omit it:

```java
@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    private int id;
    
    private String name;

    // getters and setters
}
```

If no value is set for an entity before persisting it to the database, the
property will take the default value of the data type in Java. For example, the
`name` property would be set to the default value of `NULL`.


### @Column

This annotation is used for customizing how properties will be mapped to a
database column.

```java
@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    private int id;

    @Column(name = "student_name")
    private String name;
    

    // getters and setters
}
```

We can use several values such as `name`, `length`, `unique`, `nullable` which
can modify the column name or set database constraints. For example, the `name`
property’s column name in the database will be set to "student_name" instead of
the default "name".

You can check out the different options and their usage
[here](https://www.objectdb.com/api/java/jpa/Column) and
[here](https://docs.oracle.com/javaee/7/api/javax/persistence/Column.html).

## Handling Different Data Types

We might have to use non-primitive data types such as `Date` or `Enum` in our
Java program. We have to specify how we want them to be mapped to the database
because relying on the defaults may not provide the desired functionality.

### Dates

Let's add a `dob` (Date of Birth) property to
our `Student` class. The `@Temporal` annotation is used to define how the
information will be stored in the database. We are using the `Date` class here
to demonstrate the `@Temporal` annotation. For production apps, you would use
the `LocalDate` class instead of the `Date` class.

```java
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "STUDENT_DATA")
public class Student {
  @Id
  private int id;

  private String name;

  @Temporal(TemporalType.DATE)
  private Date dob;

    // getters and setters
}
```

### Enums

The students often have to do group projects and are divided into three groups:
`LOTUS`, `ROSE`, and `DAISY`. We are going to add a `StudentGroup` enum to our
project to represent these groups.

Create a `StudentGroup` enum in the `model` package and the following:

```java
package org.example.model;

public enum StudentGroup {
  LOTUS,
  ROSE,
  DAISY
}
```

The project structure should look like this:

![project structure](https://curriculum-content.s3.amazonaws.com/6036/jpa-common-entity/studentgroup_enum.png)


Now we have to add a property on our `Student` model to record the group,
and update the getters, setters, and toString:

```java
package org.example.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "STUDENT_DATA")
public class Student {
  @Id
  private int id;

  private String name;

  @Temporal(TemporalType.DATE)
  private Date dob;

  private StudentGroup studentGroup;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getDob() {
    return dob;
  }

  public void setDob(Date dob) {
    this.dob = dob;
  }

  public StudentGroup getStudentGroup() {
    return studentGroup;
  }

  public void setStudentGroup(StudentGroup studentGroup) {
    this.studentGroup = studentGroup;
  }

  @Override
  public String toString() {
    return "Student{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", dob=" + dob +
            ", studentGroup=" + studentGroup +
            '}';
  }
}
```

We need to update `JpaCreateStudent` to set the new `dob` and `studentGroup` property values:

```java
package org.example;

import org.example.model.Student;
import org.example.model.StudentGroup;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class JpaCreateStudent {
    public static void main(String[] args) {
        // create a new student instance
        Student student1 = new Student();
        student1.setId(1);
        student1.setName("Jack");
        try {
            student1.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01"));
        }
        catch (ParseException e) {}
        student1.setStudentGroup(StudentGroup.ROSE);


        // create EntityManager
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        // access transaction object
        EntityTransaction transaction = entityManager.getTransaction();

        // create and use transactions
        transaction.begin();
        entityManager.persist(student1);
        transaction.commit();

        //close entity manager and factory
        entityManager.close();
        entityManagerFactory.close();
    }
}

```


Run `JpaCreateStudent.main` to persist the `Student` object with the
new properties `dob` and `studentGroup` to the database.
The output should show the SQL generated by Hibernate:

```text
Hibernate: 
    insert 
    into
        STUDENT_DATA
        (dob, name, studentGroup, id) 
    values
        (?, ?, ?, ?)
```

If we then run `JpaReadStudent.main`, Hibernate includes the new columns in the query:

```text
Hibernate: 
    select
        student0_.id as id1_0_0_,
        student0_.dob as dob2_0_0_,
        student0_.name as name3_0_0_,
        student0_.studentGroup as studentg4_0_0_ 
    from
        STUDENT_DATA student0_ 
    where
        student0_.id=?
```

The correct property values are printed by the `toString()` method call:

```text
Student{id=1, name='Jack', dob=2000-01-01, studentGroup=ROSE}
```

However, query the table in **pgAdmin**.  The
student group value is stored as an integer where the value corresponds to the order of the
enum, i.e., “ROSE” will be saved as `1` since it’s the second value in the
`StudentGroup` enum (0-based indexing):

![enum stored as integer](https://curriculum-content.s3.amazonaws.com/6036/entity-mapping-annotations/studentgroup_num.png)

This is not safe since any reordering of the values in the `StudentGroup` enum
will invalidate the database values. We can store the value of the enum as a string instead
of an integer using the `@Enumerated` annotation.  Update `Student` to add the annotation
`@Enumerated(EnumType.STRING)` to the `studentGroup` instance variable:

```java
@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    private int id;

    private String name;

    @Temporal(TemporalType.DATE)
    private Date dob;

    @Enumerated(EnumType.STRING)
    private StudentGroup studentGroup;

    // getters,  setters, toString
}
```


Run `JpaCreateStudent.main` to recreate the table and persist the `Student` object with the
 `studentGroup` property stored as a string rather than integer.

```text
Hibernate: 
    
    create table STUDENT_DATA (
       id int4 not null,
        dob date,
        name varchar(255),
        studentGroup varchar(255),
        primary key (id)
    )

Hibernate: 
    insert 
    into
        STUDENT_DATA
        (dob, name, studentGroup, id) 
    values
        (?, ?, ?, ?)
```

Query the table in **pgAdmin** to confirm the property was stored as the string:

![enum stored as string](https://curriculum-content.s3.amazonaws.com/6036/entity-mapping-annotations/studentgroup_string.png)

If it’s guaranteed that your enum ordering won’t change, you can keep the
default behavior. But if you want to ensure that your database values are not
invalidated in the future due to order changes in the enum, use the
`@Enumerated` annotation with the `EnumType.STRING` value.

Note that changing the value of an enum will also invalidate database values.
For example, if we change a group name from `LOTUS` to `LILY`, we would have to
manually update all the previous data in the database.

### Transient Properties

We can use the `@Transient` annotation if we don’t want a property to be saved to the database.
For example, if we were to add a `debugMessage` property (don't add it, this is just an example),
the property would not be stored in the database table.

```java
    @Transient
    private String debugMessage;

```

## Primary Key Generation

The `@GeneratedValue` annotation makes the database generate unique IDs
automatically. It’s added to the field along with the `@Id` annotation.

```java
@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    @Generated
    private int id;

    private String name;

    @Temporal(TemporalType.DATE)
    private Date dob;

    @Enumerated(EnumType.STRING)
    private StudentGroup studentGroup;

		// getters and setters
}
```

Update `JpaCreateStudent.main` to remove the call to `setId`, since the database will
automatically generate the id value.  We will also create 2 additional students.  Give them different
values for name, dob, and student group.  Make sure the entity manager persists both students:

```java
package org.example;

import org.example.model.Student;
import org.example.model.StudentGroup;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class JpaCreateStudent {
    public static void main(String[] args) {
        // create a new student instance
        Student student1 = new Student();
        student1.setName("Jack");
        try {
            student1.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01"));
        }
        catch (ParseException e) {}
        student1.setStudentGroup(StudentGroup.ROSE);


        // create a new student instance
        Student student2 = new Student();
        student2.setName("Lee");
        try {
            student2.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01"));
        }
        catch (ParseException e) {}
        student2.setStudentGroup(StudentGroup.DAISY);


        // create a new student instance
        Student student3 = new Student();
        student3.setName("Amal");
        try {
            student3.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("1980-01-01"));
        }
        catch (ParseException e) {}
        student3.setStudentGroup(StudentGroup.LOTUS);


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

```

Querying the table should show the new rows with the id automatically generated
in increasing order:

![generate id](https://curriculum-content.s3.amazonaws.com/6036/entity-mapping-annotations/generate_id.png)

##  Final Project Structure and Code Check

The project structure should look like this:

![project structure](https://curriculum-content.s3.amazonaws.com/6036/jpa-common-entity/project_structure.png)

The `Student` class:

```java
package org.example.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "STUDENT_DATA")
public class Student {

    @GeneratedValue
    @Id
    private int id;

    private String name;

    @Temporal(TemporalType.DATE)
    private Date dob;

    @Enumerated(EnumType.STRING)
    private StudentGroup studentGroup;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public StudentGroup getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(StudentGroup studentGroup) {
        this.studentGroup = studentGroup;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", studentGroup=" + studentGroup +
                '}';
    }
}
```

The `StudentGroup` enum:

```java
package org.example.model;

public enum StudentGroup {
    LOTUS,
    ROSE,
    DAISY
}
```

The `JpaCreateStudent` class:

```java
package org.example;

import org.example.model.Student;
import org.example.model.StudentGroup;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class JpaCreateStudent {
    public static void main(String[] args) {
        // create a new student instance
        Student student1 = new Student();
        student1.setName("Jack");
        try {
            student1.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01"));
        }
        catch (ParseException e) {}
        student1.setStudentGroup(StudentGroup.ROSE);


        // create a new student instance
        Student student2 = new Student();
        student2.setName("Lee");
        try {
            student2.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("1999-01-01"));
        }
        catch (ParseException e) {}
        student2.setStudentGroup(StudentGroup.DAISY);


        // create a new student instance
        Student student3 = new Student();
        student3.setName("Amal");
        try {
            student3.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("1980-01-01"));
        }
        catch (ParseException e) {}
        student3.setStudentGroup(StudentGroup.LOTUS);


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

```

The `JpaReadStudent` class:

```java
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
```

The `persistence.xml` file:

```xml
<persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
                         http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0" xmlns="http://java.sun.com/xml/ns/persistence">

    <persistence-unit name="example" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.ejb.HibernatePersistence</provider>
        <properties>
            <!-- connect to database -->
            <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver" /> <!-- DB Driver -->
            <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/student_db" /> <!--DB URL-->
            <property name="javax.persistence.jdbc.user" value="postgres" /> <!-- DB User -->
            <property name="javax.persistence.jdbc.password" value="postgres" /> <!-- DB Password -->
            <!-- configure behavior -->
            <property name="hibernate.hbm2ddl.auto" value="create" /> <!-- create / create-drop / update -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQL94Dialect"/>
            <property name="hibernate.show_sql" value="true" /> <!-- Show SQL in console -->
            <property name="hibernate.format_sql" value="true" /> <!-- Show SQL formatted -->
        </properties>
    </persistence-unit>
</persistence>
```

The  `pom.xml` file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>jpa-example</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <!-- Add the following dependencies for Hibernate and PostgreSQL -->
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.5.0</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-entitymanager</artifactId>
            <version>5.6.8.Final</version>
        </dependency>
    </dependencies>

</project>
```



## Conclusion

We have used various annotations to define how the Java objects should be mapped
to the database. Use the referenced docs to check out annotations as required
when you are building your own projects.

## References

- [https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html](https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html)
  (check out the “Annotations Types Summary” section.   
- [https://www.objectdb.com/api/java/jpa/Column](https://www.objectdb.com/api/java/jpa/Column)
