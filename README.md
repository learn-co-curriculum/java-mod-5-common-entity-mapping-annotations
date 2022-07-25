# Common Entity Mapping Annotations

## Learning Goals

- Create auto generated primary keys.
- Use various entity mapping annotations.

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

The properties of a class is automatically mapped to a table column but
sometimes we may have to customize how they are mapped to the database.

### @Basic

The `@Basic` annotation is the default behavior for entity annotations. This
annotation uses the property name as the column name and infers the database
data type from the Java data type.

If no value is set for an entity before persisting it to the database, it will
take the default value of the data type in Java. For example, a student’s age
will be set to `0` in the database if we don’t explicitly set it in our `main`
method.

```java
@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    private int id;

		@Basic
    private String name;

		private int age;

		// getters and setters
}
```

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

		private int age;

		// getters and setters
}
```

We can use several values such as `name`, `length`, `unique`, `nullable` which
can modify the column name or set database constraints. For example, the `name`
property’s column name in the database will be set to “student_name” instead of
“name”.

You can check out the different options and their usage
[here](https://www.objectdb.com/api/java/jpa/Column) and
[here](https://docs.oracle.com/javaee/7/api/javax/persistence/Column.html).

## Handling Different Data Types

We might have to use non-primitive data types such as `Date`, `Enums` in our
Java program. We have to specify how we want them to be mapped to the database
because relying on the defaults may not be what you want.

### Dates

We will remove the `age` property and add a `dob` (Date of Birth) property to
our `Student` class. The `@Temporal` annotation is used to define how the
information will be stored in the database. We are using the `Date` class here
to demonstrate the `@Temporal` annotation. For production apps, you would use
the `LocalDate` class instead of the `Date` class.

```java
@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    private int id;

		@Column(name = "student_name")
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

Create an `enum` package in the `org.example` package, create a `StudentGroup`
enum, and the following to the `StudentGroup.java` file:

```java
package org.example.enums;

public enum StudentGroup {
    LOTUS,
    ROSE,
    DAISY
}
```

Your directory structure should look like this:

```java
├── pom.xml
├── src
    ├── main
    │   ├── java
    │   │   └── org
    │   │       └── example
    │   │           ├── JpaMain.java
    │   │           ├── enums
    │   │           │   └── StudentGroup.java
    │   │           └── models
    │   │               └── Student.java
    │   └── resources
    │       └── META-INF
    │           └── persistence.xml
    └── test
        └── java
```

Now we have to add a property on our `Student` model to record the group:

```java
@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    private int id;

    private String name;

    @Temporal(TemporalType.DATE)
    private Date dob;

    private StudentGroup studentGroup;

		// getters and setters
}
```

If you run the `main` method in the `JpaMain` file now, it will insert the
student group data as an integer where the value corresponds to the order of the
enum, i.e., “LOTUS” will be saved as `0` since it’s the first value in the
`StudentGroup` enum.

| ID  | DOB        | NAME | STUDENTGROUP |
| --- | ---------- | ---- | ------------ |
| 1   | 2022-06-12 | Jack | 0            |

This is not safe since any reordering of the values in the `StudentGroup` enum
will invalidate the database values. We can store the value of the enum instead
using the `@Enumerated` annotation.

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

		// getters and setters
}
```

| ID  | DOB        | NAME | STUDENTGROUP |
| --- | ---------- | ---- | ------------ |
| 1   | 2022-06-12 | Jack | LOTUS        |

If it’s guaranteed that your enum ordering won’t change, you can keep the
default behavior. But if you want to ensure that your database values are not
invalidated in the future due to order changes in the enum, use the
`@Enumerated` annotation with the `EnumType.STRING` value.

Note that changing the value of an enum will also invalidate database values.
For example, if we change a group name from `LOTUS` to `LILY`, we would have to
manually update all the previous data in the database.

### Transient Properties

We can use the `@Transient` annotation if we don’t want a property to be used
for database creation.

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

		@Transient
		private String debugMessage;

		// getters and setters
}
```

## Primary Key Generation

The `@Generated` annotation makes the database generate unique IDs
automatically. It’s added to the field that along with the `@Id` annotation.

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

## Project Structure and Code Check

At this point, your project structure look like this:

```java
├── pom.xml
├── src
    ├── main
    │   ├── java
    │   │   └── org
    │   │       └── example
    │   │           ├── JpaMain.java
    │   │           ├── enums
    │   │           │   └── StudentGroup.java
    │   │           └── models
    │   │               └── Student.java
    │   └── resources
    │       └── META-INF
    │           └── persistence.xml
    └── test
        └── java
```

Your code should look like this:

```java
// Student.java

package org.example.models;

import org.example.enums.StudentGroup;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "STUDENT_DATA")
public class Student {
    @Id
    @GeneratedValue
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
}
```

```java
// StudentGroup.java
package org.example.enums;

public enum StudentGroup {
    LOTUS,
    ROSE,
    DAISY
}
```

```java
// JpaMain.java
package org.example;

import org.example.enums.StudentGroup;
import org.example.models.Student;

import javax.persistence.Persistence;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;

public class JpaMain {
    public static void main(String[] args) {
        // create student instances
        Student student1 = new Student();
        student1.setName("Jack");
        student1.setDob(new Date());
        student1.setStudentGroup(StudentGroup.LOTUS);

        Student student2 = new Student();
        student2.setName("Leslie");
        student2.setDob(new Date());
        student2.setStudentGroup(StudentGroup.ROSE);

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
    }
}
```

## Conclusion

We have used various annotations to define how the Java objects should be mapped
to the database. Use the referenced docs to check out annotations as required
when you are building your own projects.

## References

- [https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html](https://docs.oracle.com/javaee/7/api/javax/persistence/package-summary.html)
  (check out the “Annotations Types Summary” section.
- [https://www.objectdb.com/api/java/jpa/Column](https://www.objectdb.com/api/java/jpa/Column)
