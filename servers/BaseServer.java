import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class BaseServer {

	private final int port;
	// private final String baseLink;
	private final ConcurrentIndex index;

	public BaseServer(int port, ConcurrentIndex index) {
		this.port = port;
		// this.baseLink = baseLink;
		this.index = index;
	}

	public void startServer() {
		Server server = new Server(port);

		ServletHandler handler = new ServletHandler();

		handler.addServletWithMapping(new ServletHolder(new HomeServlet()), "/");
		handler.addServletWithMapping(new ServletHolder(new LoginUserServlet()), "/login");
		handler.addServletWithMapping(new ServletHolder(new ResultsServlet(index)), "/search");
		handler.addServletWithMapping(new ServletHolder(new LoginRegisterServlet()), "/register");

		server.setHandler(handler);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
