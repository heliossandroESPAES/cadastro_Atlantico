package ao.co.atlantico.candidaturas.web;

import ao.co.atlantico.candidaturas.dao.CandidaturaDAO;
import ao.co.atlantico.candidaturas.dao.ConnectionFactory;
import ao.co.atlantico.candidaturas.dao.DatabaseInitializer;
import ao.co.atlantico.candidaturas.service.CandidaturaService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class AppContextListener implements ServletContextListener {

    public static final String SERVICE_ATTRIBUTE = CandidaturaService.class.getName();
    public static final String STARTUP_ERROR_ATTRIBUTE = "startupDatabaseError";
    private static final Logger LOGGER = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        CandidaturaService service = new CandidaturaService(new CandidaturaDAO(connectionFactory));
        context.setAttribute(SERVICE_ATTRIBUTE, service);

        try {
            new DatabaseInitializer(connectionFactory).initialize();
            LOGGER.info("Esquema PostgreSQL verificado com sucesso.");
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Nao foi possivel preparar a base de dados PostgreSQL.", exception);
            context.setAttribute(STARTUP_ERROR_ATTRIBUTE,
                "Nao foi possivel ligar ao PostgreSQL. Confirme DB_URL, DB_USER e DB_PASSWORD.");
        }
    }
}
