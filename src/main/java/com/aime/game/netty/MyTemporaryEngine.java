package com.aime.game.netty;

import com.aime.game.netty.servers.LoginServer;

public class MyTemporaryEngine {

    public static void main(String[] args) throws Exception {
        new LoginServer(48000).run();
    }


}
