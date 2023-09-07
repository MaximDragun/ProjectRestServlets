package listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.impl.ActorDaoImpl;
import dao.impl.DirectorDaoImpl;
import dao.impl.MovieDaoImpl;
import dao.interfaces.ActorDao;
import dao.interfaces.DirectorDao;
import dao.interfaces.MovieDao;
import databaseconnaction.DataSourceConnaction;
import services.ActorService;
import services.DirectorService;
import services.MovieService;
import services.impl.ActorServiceImpl;
import services.impl.DirectorServiceImpl;
import services.impl.MovieServiceImpl;

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

        DataSourceConnaction dataSourceConnaction = new DataSourceConnaction();

        DirectorDao directorDao = new DirectorDaoImpl(dataSourceConnaction);
        ActorDao actorDao = new ActorDaoImpl(dataSourceConnaction);
        MovieDao movieDao = new MovieDaoImpl(dataSourceConnaction);
        ObjectMapper objectMapper = new ObjectMapper();

        DirectorService directorService= new DirectorServiceImpl(directorDao);
        MovieService movieService= new MovieServiceImpl(movieDao);
        ActorService actorService= new ActorServiceImpl(actorDao);

        servletContext.setAttribute("directorService", directorService);
        servletContext.setAttribute("actorService", actorService);
        servletContext.setAttribute("movieService", movieService);
        servletContext.setAttribute("objectMapper", objectMapper);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
