package org.jurr.behringer.x32.osc.xremoteproxy;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.jurr.behringer.x32.osc.xremoteproxy.clients.AbstractClient;
import org.jurr.behringer.x32.osc.xremoteproxy.endpoints.AbstractEndpoint;
import org.jurr.behringer.x32.osc.xremoteproxy.messages.AbstractOSCMessage;
import org.jurr.behringer.x32.osc.xremoteproxy.routers.AbstractRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bus
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private List<AbstractEndpoint> endpoints;
	private List<AbstractRouter> routers;
	private List<AbstractClient<?>> clients;

	// This class is responsible for keeping track of all clients, routers and endpoints.

	public Bus()
	{
		endpoints = new ArrayList<>();
		routers = new ArrayList<>();
		clients = new ArrayList<>();
	}

	public List<AbstractClient<?>> getClients()
	{
		return List.copyOf(clients);
	}

	public <T extends AbstractClient<?>> List<T> getClients(final Class<T> ofClass)
	{
		return clients.stream().filter(ofClass::isInstance).map(ofClass::cast).toList();
	}

	public void registerEndpoint(final AbstractEndpoint endpoint)
	{
		endpoint.start(this);
		endpoints.add(endpoint);
	}

	public void registerRouter(final AbstractRouter router)
	{
		router.start(this);
		routers.add(router);
	}

	public void registerClient(final AbstractClient<?> client)
	{
		client.start(this);
		clients.add(client);
	}

	public void stop() throws InterruptedException
	{
		for (final AbstractClient<?> client : clients)
		{
			client.signalStop();
		}
		for (final AbstractRouter router : routers)
		{
			router.signalStop();
		}
		for (final AbstractEndpoint endpoint : endpoints)
		{
			endpoint.signalStop();
		}

		for (final AbstractClient<?> client : clients)
		{
			client.waitUntilStopped();
		}
		for (final AbstractRouter router : routers)
		{
			router.waitUntilStopped();
		}
		for (final AbstractEndpoint endpoint : endpoints)
		{
			endpoint.waitUntilStopped();
		}
	}

	public void messageReceived(final AbstractOSCMessage message)
	{
		LOGGER.debug("Received message: {}.", message);

		for (AbstractRouter router : routers)
		{
			router.onMessageReceived(message);
		}
	}
}
