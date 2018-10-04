package com.aime.game.netty.servers.handlers;

import com.aime.game.netty.common.protobuf.MessageLoginRequestProtos;
import com.aime.game.netty.common.protobuf.MessageLoginSuccessfulProtos;
import com.aime.game.netty.common.protobuf.MessageWrapperProtos;
import java.sql.Timestamp;
import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 */
public class LoginServerHandler extends SimpleChannelInboundHandler<MessageWrapperProtos.MessageWrapper> {

    protected void channelRead0(ChannelHandlerContext context, MessageWrapperProtos.MessageWrapper message) throws Exception {
        /**
         * Читаем входящий поток тут
         * походу...
         */

        System.out.print("Receive message with code: " + message.getCode() + "\n");

        // Если это MessageLoginRequest
        if (message.getCode() == 1001) {
            // Создаем из массива байтов
            MessageLoginRequestProtos.MessageLoginRequest msg =
                    MessageLoginRequestProtos.MessageLoginRequest.parseFrom(message.getMsg());

            System.out.print("Get data:\nLogin: " + msg.getLogin() + "\nPassword: " + msg.getHashPassword() + "\n");

            // Если логин ewill и пароль qwerty
            if (msg.getLogin().equals("ewill") && msg.getHashPassword().equals("qwerty")) {
                MessageLoginSuccessfulProtos.MessageLoginSuccessful.Builder successfulBuilder =
                        MessageLoginSuccessfulProtos.MessageLoginSuccessful.newBuilder();

                successfulBuilder.setMatchmakingServer("192.168.0.255")
                .setPort(48002)
                .setTokenExpire((int) new Timestamp(System.currentTimeMillis()).getTime())
                .setTokenAccess(UUID.randomUUID().toString());

                // Оборачиваем во Wrapper
                MessageWrapperProtos.MessageWrapper.Builder builder =
                        MessageWrapperProtos.MessageWrapper.newBuilder();

                builder.setCode(1002) // success code
                .setMsg(successfulBuilder.build().toByteString());

                context.writeAndFlush(builder.build());
            }
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