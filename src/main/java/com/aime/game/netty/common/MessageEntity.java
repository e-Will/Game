package com.aime.game.netty.common;

/**
 * Обертка над пакетом данных
 */
public class MessageEntity {

    private final int _command;
    private final com.google.protobuf.ByteString _message;


    public MessageEntity(int code, com.google.protobuf.ByteString message) {
        _command = code;
        _message = message;
    }

    public int getCommand() { return _command; }
    public com.google.protobuf.ByteString getMessage() {return _message; }

    /**
     * Константы команд
     */
    public final class Command {

        private Command() {}

        // LOGIN CODES
        public static final int LOGIN_REQUEST = 1001;
        public static final int LOGIN_SUCCESSFUL = 1002;


    }
}
