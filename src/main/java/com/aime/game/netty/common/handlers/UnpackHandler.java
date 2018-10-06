package com.aime.game.netty.common.handlers;

import com.aime.game.netty.common.MessageEntity;
import com.aime.game.netty.common.protobuf.MessageWrapperProtos;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Logger;

/**
 * При чтении входящего потока распаковывает MessageWrapper и преобразует его в MessageEntity
 */
public class UnpackHandler extends SimpleChannelInboundHandler<MessageWrapperProtos.MessageWrapper> {

    private static Logger LOG = Logger.getLogger(UnpackHandler.class.getName());

    protected void channelRead0(ChannelHandlerContext context, MessageWrapperProtos.MessageWrapper message) throws Exception {
        LOG.info("Распаковываем MessageWrapper (code:" + message.getCode() + ") и пихаем его в MessageEntity");
        context.fireChannelRead(new MessageEntity(message.getCode(), message.getMsg()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        LOG.warning("Exception: " + cause.getMessage());
        // Обработка ошибок
        cause.printStackTrace();
        context.close();
    }
}
