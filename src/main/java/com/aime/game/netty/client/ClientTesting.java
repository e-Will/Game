package com.aime.game.netty.client;

import com.aime.game.netty.client.initializers.ClientTestingInitializer;
import com.aime.game.netty.common.protobuf.MessageLoginRequestProtos;
import com.aime.game.netty.common.protobuf.MessageWrapperProtos;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                        channel.writeAndFlush(LoginRequest());
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

    private MessageWrapperProtos.MessageWrapper LoginRequest() {
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
        MessageLoginRequestProtos.MessageLoginRequest.Builder builder =
                MessageLoginRequestProtos.MessageLoginRequest.newBuilder();

        builder.setLogin(login); // указываем логин
        builder.setHashPassword(password); // пароль, потом его нужно хешировать

        // 1001 - код события MessageLoginRequest
        MessageWrapperProtos.MessageWrapper.Builder wrapperBuilder = MessageWrapperProtos.MessageWrapper.newBuilder()
                .setCode(1001)
                .setMsg(builder.build().toByteString()); // Все сообщения оборачиваются в один общий вид, для пересылки по сети

        return wrapperBuilder.build();
    }

}
