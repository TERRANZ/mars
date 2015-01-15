package ru.mars.server;

import com.beust.jcommander.JCommander;
import ru.mars.server.network.GameServer;
import ru.mars.server.network.policyserver.PolicyServer;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException, InterruptedException {
        final Parameters parameters = Parameters.getInstance();
        new JCommander(parameters, args);
        System.out.println(parameters.isDebug());
        new Thread(new Runnable() {
            @Override
            public void run() {
                new PolicyServer(parameters.getPolicyServerPort(), new String[]{"*:" + parameters.getGameServerPort()}).start();
                new GameServer(parameters.getGameServerPort()).start();
            }
        }).start();
    }

}
