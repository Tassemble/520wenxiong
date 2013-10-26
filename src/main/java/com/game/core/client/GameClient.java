package com.game.core.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.game.core.GameProtocolHandler;

/**
 * @author CHQ
 * @since 2013-7-28
 */
public class GameClient {

	public static void main(String[] args) {
		IoConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.setHandler(new GameProtocolHandler());
		ConnectFuture future = connector.connect(new InetSocketAddress("127.0.0.1", 8888));
		future.awaitUninterruptibly();
		IoSession session = future.getSession();
		
		byte[] bytes = new byte[100];
		bytes[0] = 'a';
		bytes[1] = 'b';
		bytes[2] = 12;
		bytes[3] = 32;
		bytes[4] = 12;
		bytes[5] = '\n';
		
		session.write(bytes);
		// wait until the summation is done
		session.getCloseFuture().awaitUninterruptibly();
		connector.dispose();

	}
}
