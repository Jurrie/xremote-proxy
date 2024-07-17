package org.jurr.behringer.x32.osc.xremoteproxy;

public interface BusManaged
{
	void start(Bus bus);

	void signalStop();

	void waitUntilStopped() throws InterruptedException;

	void waitUntilStopped(long millis) throws InterruptedException;

	void waitUntilStopped(long millis, int nanos) throws InterruptedException;
}
