package com.aime.game.netty.servers.initializers;

import com.aime.game.netty.common.protobuf.MessageWrapperProtos;
import com.aime.game.netty.servers.handlers.LoginServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

public class LoginServerInitializer extends ChannelInitializer<SocketChannel> {

    final private SslContext _sslContext;

    public LoginServerInitializer(SslContext sslContext) {
        this._sslContext = sslContext;
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        /**
         * В этом месте настраивается повидение, что именно делать с пакетами и как их обрабатывать.
         */

        /** При входящих пакетах **/
        // распостроняеться на входящие пакеты, т.к. унаследованны от ChannelInboundHandler
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(
                /* Наш сгенерированный Wrapper */
                MessageWrapperProtos.MessageWrapper.getDefaultInstance()));

        // При исходящих пакетах
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());

        pipeline.addLast(new LoginServerHandler());

    }
}
