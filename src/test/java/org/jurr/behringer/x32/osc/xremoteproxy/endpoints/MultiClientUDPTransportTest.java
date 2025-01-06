package org.jurr.behringer.x32.osc.xremoteproxy.endpoints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnixDomainSocketAddress;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MultiClientUDPTransportTest
{
	@Test
	@DisplayName("Null source")
	void testNullSource()
	{
		// Arrange
		final SocketAddress source = null;
		final SocketAddress target = UnixDomainSocketAddress.of("/test");

		// Act
		final boolean actual = MultiClientUDPTransport.sameAddress(source).test(target);

		// Assert
		assertFalse(actual);
	}

	@Nested
	@DisplayName("UnixDomainSocketAddress")
	class UnixDomainSocketAddressTests
	{
		@Test
		@DisplayName("Same path")
		void testUnixDomainSocketAddress_samePath()
		{
			// Arrange
			final SocketAddress source = UnixDomainSocketAddress.of("/test");
			final SocketAddress target = UnixDomainSocketAddress.of("/test");

			// Act
			final boolean actual = MultiClientUDPTransport.sameAddress(source).test(target);

			// Assert
			assertTrue(actual);
		}

		@Test
		@DisplayName("Different path")
		void testUnixDomainSocketAddress_differentPath()
		{
			// Arrange
			final SocketAddress source = UnixDomainSocketAddress.of("/test");
			final SocketAddress target = UnixDomainSocketAddress.of("/otherPath");

			// Act
			final boolean actual = MultiClientUDPTransport.sameAddress(source).test(target);

			// Assert
			assertFalse(actual);
		}
	}

	@Nested
	class InetSocketAddressTests
	{
		@Test
		@DisplayName("Same host, same port")
		void testInetSocketAddress_sameHostSamePort()
		{
			// Arrange
			final SocketAddress source = InetSocketAddress.createUnresolved("host", 1234);
			final SocketAddress target = InetSocketAddress.createUnresolved("host", 1234);

			// Act
			final boolean actual = MultiClientUDPTransport.sameAddress(source).test(target);

			// Assert
			assertTrue(actual);
		}

		@Test
		@DisplayName("Same host, different port")
		void testInetSocketAddress_sameHostDifferentPort()
		{
			// Arrange
			final SocketAddress source = InetSocketAddress.createUnresolved("host", 1234);
			final SocketAddress target = InetSocketAddress.createUnresolved("host", 2345);

			// Act
			final boolean actual = MultiClientUDPTransport.sameAddress(source).test(target);

			// Assert
			assertTrue(actual); // Yes, true. We want to prevent relaying an OSC command back to the original sender. But applications send and receive on different ports. So we must not take the port into account.
		}

		@Test
		@DisplayName("Different host, same port")
		void testInetSocketAddress_differentHostSamePort()
		{
			// Arrange
			final SocketAddress source = InetSocketAddress.createUnresolved("host", 1234);
			final SocketAddress target = InetSocketAddress.createUnresolved("otherhost", 1234);

			// Act
			final boolean actual = MultiClientUDPTransport.sameAddress(source).test(target);

			// Assert
			assertFalse(actual);
		}

		@Test
		@DisplayName("Different host, different port")
		void testInetSocketAddress_differentHostDifferentPort()
		{
			// Arrange
			final SocketAddress source = InetSocketAddress.createUnresolved("host", 1234);
			final SocketAddress target = InetSocketAddress.createUnresolved("otherhost", 2345);

			// Act
			final boolean actual = MultiClientUDPTransport.sameAddress(source).test(target);

			// Assert
			assertFalse(actual);
		}
	}
}