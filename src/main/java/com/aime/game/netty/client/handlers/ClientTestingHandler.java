package com.aime.game.netty.client.handlers;

import com.aime.game.netty.common.protobuf.MessageLoginSuccessfulProtos;
import com.aime.game.netty.common.protobuf.MessageWrapperProtos;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Обработчик входящих пакетов
 */
public class ClientTestingHandler extends SimpleChannelInboundHandler<MessageWrapperProtos.MessageWrapper> {

    private static final Logger LOGGER = Logger.getLogger( ClientTestingHandler.class.getName() );

    @Override
    protected void channelRead0(ChannelHandlerContext context,
                                MessageWrapperProtos.MessageWrapper message) throws Exception {

        // Пришло сообщение

        switch (message.getCode()) {
            // Login Successful
            case 1002:
                LOGGER.log(Level.INFO, "Get successful login!\n");

                // Разворачиваем сообщение из массива байтов
                MessageLoginSuccessfulProtos.MessageLoginSuccessful successful =
                        MessageLoginSuccessfulProtos.MessageLoginSuccessful.parseFrom(message.getMsg());

                LOGGER.log(Level.INFO,
                        "We must reconnect to server: " + successful.getMatchmakingServer()
                                + " with port " + successful.getPort()
                                + ". Access token: " + successful.getTokenAccess()
                                + " that expire " + successful.getTokenExpire() + " + 2 min.\n");
                break;

            default: LOGGER.log(Level.INFO, "Code: " + message.getCode() + " not processed!");
        }
    }
}
