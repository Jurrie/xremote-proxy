package org.jurr.behringer.x32.osc.xremoteproxy;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.qlcplus.QLCPlusEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.x32.X32Endpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.routers.X32ToQLCPlusRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
	static
	{
		// Silence SLF4J internal info logging. This is in a static block because it needs to be done before any SLF4J logger is created.
		System.setProperty(org.slf4j.helpers.Reporter.SLF4J_INTERNAL_VERBOSITY_KEY, "WARN");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final int EXIT_OK = 0;
	private static final int EXIT_CMDLINE_INVALID = 1;
	private static final int EXIT_EXCEPTION = 2;

	public static void main(final String... args) throws InterruptedException, IOException
	{
		LOGGER.info("xremote-proxy version {} ({})", XRemoteProxy.getVersion(), XRemoteProxy.getBuildNumber());

		final JCommander jCommander = JCommander.newBuilder().addObject(Settings.INSTANCE).build();
		try
		{
			jCommander.setProgramName(getCurrentExecutable());
			jCommander.parse(args);
		}
		catch (ParameterException e)
		{
			LOGGER.error(e.getLocalizedMessage());
			e.usage();
			System.exit(EXIT_CMDLINE_INVALID);
		}

		if (Settings.INSTANCE.isHelp())
		{
			jCommander.usage();
			System.exit(EXIT_OK);
		}

		try
		{
			startApplication();
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage());
			if (e.getCause() != null)
			{
				LOGGER.trace("Stack trace: ", e.getCause());
			}
			System.exit(EXIT_EXCEPTION);
			throw e; // This is unreachable, but it makes the compiler happy.
		}
		catch (Throwable e)
		{
			// Something severe has happened, like OOM or disk full.
			// Try to log anyway; maybe we can still log some info.
			LOGGER.error("Unchecked exception thrown: {}", e.getMessage());
			throw e;
		}
	}

	private static void startApplication() throws InterruptedException, IOException
	{
		LOGGER.debug("Application starting.");

		final Bus hub = new Bus();

		final X32Endpoint newX32Endpoint = new X32Endpoint(Settings.INSTANCE.getX32ListenAddress(), Settings.INSTANCE.getX32Addresses(), Settings.INSTANCE.getFakeX32Addresses());
		hub.registerEndpoint(newX32Endpoint);

		final QLCPlusEndpoint qlcPlusEndpoint = new QLCPlusEndpoint(Settings.INSTANCE.getQLCPlusListenAddress(), Settings.INSTANCE.getQLCPlusAddresses());
		hub.registerEndpoint(qlcPlusEndpoint);

		final X32ToQLCPlusRouter x32ToQLCPlusRouter = new X32ToQLCPlusRouter();
		hub.registerRouter(x32ToQLCPlusRouter);

		LOGGER.info("Application started.");

		if (Settings.INSTANCE.isRunInForeground())
		{
			try
			{
				System.in.read();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			LOGGER.info("Application stopping.");
			hub.stop();

			LOGGER.debug("Application stopped.");
		}
	}

	private static String getCurrentExecutable()
	{
		try
		{
			final String uri = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString();
			// We are a JAR, or a JAR within a WAR
			final String innerURI = uri.replaceFirst("jar:", "").replaceFirst("file:", "").replaceFirst("!/WEB-INF/classes!/", "");
			final Path currentExecutablePath = Paths.get(innerURI);
			if (!currentExecutablePath.toFile().isDirectory())
			{
				final Path fileName = currentExecutablePath.getFileName();
				if (fileName != null)
				{
					return fileName.toString();
				}
			}
		}
		catch (final URISyntaxException | RuntimeException e)
		{
			// Do nothing, just return the default
		}

		// We can not determine the jar file from which we run.
		// We are probably running the class directly from IntelliJ or Eclipse.
		// Default to returning the canonical name of this class.
		return Main.class.getCanonicalName();
	}
}
