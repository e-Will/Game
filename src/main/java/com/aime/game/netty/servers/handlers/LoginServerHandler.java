package com.aime.game.netty.servers.handlers;

import com.aime.game.netty.common.MessageEntity;
import com.aime.game.netty.common.handlers.UnpackHandler;
import com.aime.game.netty.common.protobuf.MessageLoginRequestProtos;
import com.aime.game.netty.common.protobuf.MessageLoginSuccessfulProtos;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Logger;

public class LoginServerHandler extends SimpleChannelInboundHandler<MessageEntity> {

    private static Logger LOG = Logger.getLogger(LoginServerHandler.class.getName());

    protected void channelRead0(ChannelHandlerContext ctx, MessageEntity message) throws Exception {

        switch (message.getCommand()) {
            case MessageEntity.Command.LOGIN_REQUEST:
                MessageLoginRequestProtos.MessageLoginRequest msg =
                        MessageLoginRequestProtos.MessageLoginRequest.parseFrom(message.getMessage());

               LOG.info("Receive LoginRequest >> Login: " + msg.getLogin() + " & Password: " + msg.getHashPassword() + "\n");

                // Если логин ewill и пароль qwerty
                if (msg.getLogin().equals("ewill") && msg.getHashPassword().equals("qwerty")) {
                    MessageLoginSuccessfulProtos.MessageLoginSuccessful.Builder successfulBuilder =
                            MessageLoginSuccessfulProtos.MessageLoginSuccessful.newBuilder();

                    successfulBuilder.setMatchmakingServer("192.168.0.255")
                            .setPort(48002)
                            .setTokenExpire((int) new Timestamp(System.currentTimeMillis()).getTime())
                            .setTokenAccess(UUID.randomUUID().toString());

                    ctx.writeAndFlush(new MessageEntity(MessageEntity.Command.LOGIN_SUCCESSFUL, successfulBuilder.build().toByteString()));
                }

                break;
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
        // Если кто-то подсоединился.
        super.handlerAdded(context);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        // Обработка ошибок
        cause.printStackTrace();
        context.close();
    }
}

/*
 * Проверить в консоле занятые порты
 *  netstat -aon | more
 */