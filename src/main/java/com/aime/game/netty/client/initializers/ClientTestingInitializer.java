package com.aime.game.netty.client.initializers;

import com.aime.game.netty.client.handlers.ClientTestingHandler;
import com.aime.game.netty.common.handlers.PackHandler;
import com.aime.game.netty.common.protobuf.MessageWrapperProtos;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

/**
 * Инициализирует обработку входящих и исходящих сообщений
 *
 */
public class ClientTestingInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext _sslContext;

    public ClientTestingInitializer(SslContext sslContext) {
        _sslContext = sslContext;
    }

    protected void initChannel(SocketChannel socket) {

        // Вешаем обработчики для входящего и исходящего потока
        ChannelPipeline pipeline = socket.pipeline();

        // Для чтения (снизу вверх)
        pipeline.addLast(new ProtobufVarint32FrameDecoder()); // Декодер, который динамически разбивает полученные ByteBufs на значение поля цепочки целочисленной длины базы данных Google Protocol 128 Varints в сообщении.
        pipeline.addLast(new ProtobufDecoder(MessageWrapperProtos.MessageWrapper.getDefaultInstance()));

        // Для записи в поток (сверху вниз?)
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new PackHandler());

        pipeline.addLast(new ClientTestingHandler());
    }
}
/**
 *                                                  I/O Request
 *                                             via Channel or
 *                                         ChannelHandlerContext
 *                                                       |
 *   +---------------------------------------------------+---------------+
 *   |                           ChannelPipeline         |               |
 *   |                                                  \|/              |
 *   |    +---------------------+            +-----------+----------+    |
 *   |    | Inbound Handler  N  |            | Outbound Handler  1  |    |
 *   |    +----------+----------+            +-----------+----------+    |
 *   |              /|\                                  |               |
 *   |               |                                  \|/              |
 *   |    +----------+----------+            +-----------+----------+    |
 *   |    | Inbound Handler N-1 |            | Outbound Handler  2  |    |
 *   |    +----------+----------+            +-----------+----------+    |
 *   |              /|\                                  .               |
 *   |               .                                   .               |
 *   | ChannelHandlerContext.fireIN_EVT() ChannelHandlerContext.OUT_EVT()|
 *   |        [ method call]                       [method call]         |
 *   |               .                                   .               |
 *   |               .                                  \|/              |
 *   |    +----------+----------+            +-----------+----------+    |
 *   |    | Inbound Handler  2  |            | Outbound Handler M-1 |    |
 *   |    +----------+----------+            +-----------+----------+    |
 *   |              /|\                                  |               |
 *   |               |                                  \|/              |
 *   |    +----------+----------+            +-----------+----------+    |
 *   |    | Inbound Handler  1  |            | Outbound Handler  M  |    |
 *   |    +----------+----------+            +-----------+----------+    |
 *   |              /|\                                  |               |
 *   +---------------+-----------------------------------+---------------+
 *                   |                                  \|/
 *   +---------------+-----------------------------------+---------------+
 *   |               |                                   |               |
 *   |       [ Socket.read() ]                    [ Socket.write() ]     |
 *   |                                                                   |
 *   |  Netty Internal I/O Threads (Transport Implementation)            |
 *   +-------------------------------------------------------------------+
 */