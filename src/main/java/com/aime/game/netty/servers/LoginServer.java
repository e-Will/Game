package com.aime.game.netty.servers;

import com.aime.game.netty.common.handlers.UnpackHandler;
import com.aime.game.netty.servers.initializers.LoginServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.logging.Logger;

/**
 * @author e-Will
 *
 * Сервер авторизации (регистрации)
 *
 * Принимает от пользователя данные, проверяет их валидность
 * - в случае успеха направляет поьзователя на игровой сервер
 * - в случае ошибки, отклоняет запрос авторизации
 */
public final class LoginServer {

    private static Logger LOG = Logger.getLogger(LoginServer.class.getName());

    private final int _port;
    // https://netty.io/wiki/forked-tomcat-native.html
    static final boolean SSL = System.getProperty("ssl") != null;

    /**
     * @param port Порт для прослушивания
     */
    public LoginServer( int port ) {
        this._port = port;
    }

    /**
     * Запустить сервер
     */
    public void run() throws Exception {

        final SslContext sslContext;

        if (SSL) {
            SelfSignedCertificate certificate = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
        } else {
            sslContext = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // поток acceptor, снимает с порта входящее подключение
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2); // рабочий. тут происходит вся работа

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new LoginServerInitializer(sslContext));

            LOG.info(">> SERVER START");
            bootstrap.bind(_port).sync().channel().closeFuture().sync();
        } catch (Exception ex) {
            LOG.warning("LoginServer except: " + ex.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
