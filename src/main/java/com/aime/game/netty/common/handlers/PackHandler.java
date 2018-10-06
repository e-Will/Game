package com.aime.game.netty.common.handlers;

import com.aime.game.netty.common.MessageEntity;
import com.aime.game.netty.common.protobuf.MessageWrapperProtos;
import io.netty.channel.*;

import java.util.logging.Logger;

/**
 * Оборачивает сообщение в Protobuf MessageWrapper перед отправкой
 * MessageEntity only
 */
public class PackHandler extends ChannelOutboundHandlerAdapter {

    private static Logger LOG = Logger.getLogger(PackHandler.class.getName());

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof MessageEntity ){
            // Оборачиваем во Wrapper
            MessageEntity entity = (MessageEntity) msg;

            LOG.info("PackHandler -> запаковываем сообщение во MessageWrapper (code: " + entity.getCommand() + ")");

            MessageWrapperProtos.MessageWrapper result =
                    MessageWrapperProtos.MessageWrapper.newBuilder()
                            .setCode(entity.getCommand())
                            .setMsg(entity.getMessage())
                            .build();

            super.write(ctx, result, promise);
        }
        else
            super.write(ctx, msg, promise);
    }
}
