package ru.mars.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import ru.mars.server.network.GameServer;
import ru.mars.server.network.policyserver.PolicyServer;

import java.io.IOException;

public class Main {

    public static class Parameters {
        @Parameter(names = {"-g"}, description = "Game server port")
        private Integer gameServerPort = 56777;
        @Parameter(names = {"-p"}, description = "Policy server port")
        private Integer policyServerPort = 1008;
        @Parameter(names = "--help", help = true)
        private boolean help;

        public Integer getGameServerPort() {
            return gameServerPort;
        }

        public void setGameServerPort(Integer gameServerPort) {
            this.gameServerPort = gameServerPort;
        }

        public Integer getPolicyServerPort() {
            return policyServerPort;
        }

        public void setPolicyServerPort(Integer policyServerPort) {
            this.policyServerPort = policyServerPort;
        }
    }


    public static void main(String args[]) throws IOException, InterruptedException {
        final Parameters parameters = new Parameters();
        new JCommander(parameters, args);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new PolicyServer(parameters.getPolicyServerPort(), new String[]{"*:" + parameters.getGameServerPort()}).start();
                new GameServer(parameters.getGameServerPort()).start();
            }
        }).start();
    }

}
