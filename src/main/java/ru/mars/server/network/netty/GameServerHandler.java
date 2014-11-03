package ru.mars.server.network.netty;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;
import ru.mars.server.game.GameWorker;

/**
 * Date: 01.11.14
 * Time: 13:58
 */
public class GameServerHandler extends SimpleChannelUpstreamHandler {
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent)
            logger.info("Handle upstream : " + e.toString());
        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        GameWorker.getInstance().addPlayer(e.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        GameWorker.getInstance().removePlayer(e.getChannel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        GameWorker.getInstance().handlePlayerCommand(e.getChannel(), e.getMessage().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.warn("Unexcepted exception from downstream.", e.getCause());
        GameWorker.getInstance().removePlayer(e.getChannel());
        e.getChannel().close();
    }
}
