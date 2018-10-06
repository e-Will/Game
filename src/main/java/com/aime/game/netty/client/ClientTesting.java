package com.aime.game.netty.client;

import com.aime.game.netty.client.initializers.ClientTestingInitializer;
import com.aime.game.netty.common.MessageEntity;
import com.aime.game.netty.common.protobuf.MessageLoginRequestProtos;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.Scanner;

/**
 * Тестовый клиент
 * Оригинальный клиент - это экземпляр игры на Unity3D
 */
public class ClientTesting {

    public static void main(String[] args) throws Exception {
        new ClientTesting("localhost", 48000).run();
    }

    private final String _host;
    private final int _port;

    public ClientTesting(String host, int port) {
        _host = host;
        _port = port;
    }

    void run() throws Exception{

        final SslContext sslContext;

        // Потом у нас будет SSL
        if (false) {
            sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslContext = null;
        }

        // Поток для работы с сетью
        EventLoopGroup group = new NioEventLoopGroup();

        // для общения с консолью
        Scanner scanner = new Scanner(System.in);

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientTestingInitializer(sslContext));

            // Подключаемся к серверу / Исключение, если сервер недоступен
            Channel channel = bootstrap.connect(_host, _port).sync().channel();

            // Общение с сервером
            while (channel.isOpen()) {

                System.out.println("Make choice:\n1. LoginRequest: ");

                switch (scanner.nextInt()) {
                    case 1:
                        LoginRequest(channel);
                        break;

                    default: break;
                }
            }
        } catch (Exception ex) {
            System.out.println("ex: " + ex.getMessage() + "\ntrace: " + ex.getStackTrace());
        } finally {
            group.shutdownGracefully();
        }
    }

    private void LoginRequest(Channel channel) {
        String login, password;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Login: ");
        login = scanner.next().trim();

        System.out.println("Password: ");
        password = scanner.next().trim();

        // Request here
        System.out.println("Request to \'" + _host + ":" + _port
                + "\' with l:" + login + " and p:" + password + "\n");

        // Создаем Message
        MessageLoginRequestProtos.MessageLoginRequest result =
                MessageLoginRequestProtos.MessageLoginRequest.newBuilder()
                .setLogin(login)    // указываем логин
                .setHashPassword(password)
                .build(); // пароль, потом его нужно хешировать

        System.out.println("Write in stream MessageEntity");
        channel.writeAndFlush(new MessageEntity(MessageEntity.Command.LOGIN_REQUEST, result.toByteString()));
    }
}
