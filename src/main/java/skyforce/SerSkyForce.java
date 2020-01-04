package skyforce;


import skyforce.server.ServerManagement;

public class SerSkyForce {
    public static void main(String[] args) {
        ServerManagement.setPort(1234);
        ServerManagement.getInstance().run();
    }
}
