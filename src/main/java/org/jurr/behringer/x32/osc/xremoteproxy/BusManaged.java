package org.jurr.behringer.x32.osc.xremoteproxy;

import java.io.IOException;

public interface BusManaged
{
	void start(Bus bus);

	void signalStop() throws IOException;

	void waitUntilStopped() throws InterruptedException;

	void waitUntilStopped(long millis) throws InterruptedException;

	void waitUntilStopped(long millis, int nanos) throws InterruptedException;
}
