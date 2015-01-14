package ru.mars.server.network;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import ru.mars.server.network.netty.ChannelsHolder;
import ru.mars.server.network.netty.GameServerPipeLineFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Date: 01.11.14
 * Time: 13:54
 */
public class GameServer {
    private int port;
    protected Logger logger = Logger.getLogger(this.getClass());

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        logger.debug("Starting game server on " + port);
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new GameServerPipeLineFactory());
        Channel channel = bootstrap.bind(new InetSocketAddress(port));
        ChannelsHolder.getInstance().getChannels().add(channel);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChannelGroupFuture future = ChannelsHolder.getInstance().getChannels().close();
        future.awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }
}
