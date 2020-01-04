package skyforce.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerManagement {
    private static int port = 1234;
    public static int maxUserInRoom = 10;

    private ServerSocket serverSocket;
    private List<RoomAreaManagement> roomAreaManagements;
    private List<ClientManagement> clientManagements;

    public static ServerManagement instance;

    private ServerManagement() {
        try {
            this.serverSocket = new ServerSocket(port);
            this.roomAreaManagements = new ArrayList<RoomAreaManagement>();
            this.clientManagements = new ArrayList<ClientManagement>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.printf("Server is stating in port %d\n", port);
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                ClientManagement clientManagement = new ClientManagement(socket);
                clientManagements.add(clientManagement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ServerManagement getInstance() {
        if (instance == null){
            instance = new ServerManagement();
        }
        return instance;
    }

    public static void setPort(int port) {
        ServerManagement.port = port;
    }

}
