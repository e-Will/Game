package com.aime.game.netty;

import com.aime.game.netty.servers.LoginServer;

public class MyTemporaryServerEngine {

    public static void main(String[] args) throws Exception {
        new LoginServer(48000).run();
    }


}
