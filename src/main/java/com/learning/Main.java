package com.learning;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main {
    public static void main(String[] args) {

        // Step 1: Setup
        SessionFactory sf = new Configuration()
                .configure()
                .addAnnotatedClass(Laptop.class)
                .buildSessionFactory();

        Session session = sf.openSession();

        // ======== Case 1: get() - Record exists ========
        System.out.println("Case 1: get() with existing ID (1)");
        Laptop laptop1 = session.get(Laptop.class, 1); // existing
        System.out.println("Laptop 1: " + laptop1);

        // ======== Case 2: get() - Record does not exist ========
        System.out.println("\nCase 2: get() with non-existing ID (100)");
        Laptop laptop2 = session.get(Laptop.class, 100); // non-existing
        System.out.println("Laptop 2: " + laptop2); // prints null

        // ======== Case 3: load() - Record exists ========
        System.out.println("\nCase 3: load() with existing ID (2)");
        Laptop laptop3 = session.load(Laptop.class, 2); // existing
        System.out.println("Laptop 3: " + laptop3); // triggers query

        // ======== Case 4: load() - Record does not exist, not accessed ========
        System.out.println("\nCase 4: load() with non-existing ID (200), not accessed");
        try {
            Laptop laptop4 = session.load(Laptop.class, 200); // proxy created
            System.out.println("Proxy created. Not accessing fields.");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        // ======== Case 5: load() - Record does not exist, accessed (exception) ========
        System.out.println("\nCase 5: load() with non-existing ID (300), field accessed");
        try {
            Laptop laptop5 = session.load(Laptop.class, 300); // proxy created
            System.out.println("Laptop 5 brand: " + laptop5.getBrand()); // triggers exception
        } catch (Exception e) {
            System.out.println("Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        // ======== Case 6: load() - Proxy access after session closed ========
        System.out.println("\nCase 6: load() with existing ID (3), access after session closed");
        Laptop laptop6 = session.load(Laptop.class, 3); // existing
        session.close(); // close session
        try {
            System.out.println("Laptop 6 model: " + laptop6.getModel()); // LazyInitializationException
        } catch (HibernateException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        sf.close();
    }
}
