package listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.impl.ActorDaoImpl;
import dao.impl.DirectorDaoImpl;
import dao.impl.MovieDaoImpl;
import dao.interfaces.ActorDao;
import dao.interfaces.DirectorDao;
import dao.interfaces.MovieDao;
import databaseconnaction.DataSourceHikariPostgreSQL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        final ServletContext servletContext =
                servletContextEvent.getServletContext();

        DirectorDao directorDao = new DirectorDaoImpl();
        ActorDao actorDao = new ActorDaoImpl();
        MovieDao movieDao = new MovieDaoImpl();
        ObjectMapper objectMapper = new ObjectMapper();
        servletContext.setAttribute("directorDao", directorDao);
        servletContext.setAttribute("actorDao", actorDao);
        servletContext.setAttribute("movieDao", movieDao);
        servletContext.setAttribute("objectMapper", objectMapper);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
