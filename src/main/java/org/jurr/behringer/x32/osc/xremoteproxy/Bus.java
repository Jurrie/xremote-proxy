package org.jurr.behringer.x32.osc.xremoteproxy;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.routers.AbstractRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bus
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private List<AbstractEndpoint<?>> endpoints;
	private List<AbstractRouter> routers;

	// This class is responsible for keeping track of all endpoints and routers.

	public Bus()
	{
		endpoints = new ArrayList<>();
		routers = new ArrayList<>();
	}

	public <T extends AbstractEndpoint<?>> List<T> getEndpoints(final Class<T> ofClass)
	{
		return endpoints.stream().filter(ofClass::isInstance).map(ofClass::cast).toList();
	}

	public void registerEndpoint(final AbstractEndpoint<?> endpoint)
	{
		endpoint.start(this);
		endpoints.add(endpoint);
	}

	public void registerRouter(final AbstractRouter router)
	{
		router.start(this);
		routers.add(router);
	}

	public void stop() throws InterruptedException
	{
		for (final AbstractRouter router : routers)
		{
			router.signalStop();
		}
		for (final AbstractEndpoint<?> endpoint : endpoints)
		{
			try
			{
				endpoint.signalStop();
			}
			catch (IOException e)
			{
				LOGGER.error("Error while stopping endpoint.", e);
			}
		}

		for (final AbstractRouter router : routers)
		{
			router.waitUntilStopped();
		}
		for (final AbstractEndpoint<?> endpoint : endpoints)
		{
			endpoint.waitUntilStopped();
		}
	}

	public void messageReceived(final AbstractEndpoint<?> source, final AbstractOSCMessage message)
	{
		LOGGER.debug("Received message: {}.", message);

		for (AbstractRouter router : routers)
		{
			router.onMessageReceived(source, message);
		}
	}
}
