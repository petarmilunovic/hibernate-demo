package io.github.petarmilunovic;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    /**
     * methods for connecting and disconnecting from the database
     */

    public static SessionFactory createSessionFactory() {

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Musician.class);
        configuration.addAnnotatedClass(Album.class);
        configuration.configure();

        serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).
                build();

        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        sessionFactory.close();
    }

}