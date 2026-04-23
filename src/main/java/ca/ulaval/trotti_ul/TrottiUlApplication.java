package ca.ulaval.trotti_ul;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import ca.ulaval.trotti_ul.infrastructure.config.JerseyApplication;

public class TrottiUlApplication {

    public static void main(String[] args) throws Exception {
        int port = 8080;

        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(server, "/");
        server.setHandler(context);

        JerseyApplication jerseyConfig = new JerseyApplication();

        ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(jerseyConfig));
        jerseyServlet.setInitOrder(0);

        context.addServlet(jerseyServlet, "/*");

        try {
            server.start();
            System.out.println("Trotti-UL running on http://localhost:" + port);
            server.join();
        } finally {
            server.stop();
            server.destroy();
        }
    }
}
