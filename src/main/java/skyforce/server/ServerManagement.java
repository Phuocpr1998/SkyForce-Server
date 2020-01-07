package skyforce.server;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import skyforce.server.network.MessageWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerManagement {
    private static int port = 1234;
    public static int maxUserInRoom = 2;

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
        if (instance == null) {
            instance = new ServerManagement();
        }
        return instance;
    }

    public byte JoinRoom(int roomId, ClientManagement clientManagement) {
        for (RoomAreaManagement roomAreaManagement : roomAreaManagements) {
            if (roomAreaManagement.getRoomId() == roomId) {
                if (!roomAreaManagement.isFully()) {
                    clientManagement.setCurrentRoom(roomAreaManagement);
                    return 0;
                } else {
                    return 1;
                }
            }
        }
        return 2;
    }

    public int GetRoomAlready() {
        for (RoomAreaManagement roomAreaManagement : roomAreaManagements) {
            if (!roomAreaManagement.isFully()) {
                return roomAreaManagement.getRoomId();
            }
        }
        return -1;
    }

    public int CreateRoom(ClientManagement clientManagement) {
        RoomAreaManagement roomAreaManagement = new RoomAreaManagement(1000 + roomAreaManagements.size());
        roomAreaManagements.add(roomAreaManagement);
        clientManagement.setCurrentRoom(roomAreaManagement);
        return roomAreaManagement.getRoomId();
    }

    public int RegisterPlayer(String name){
        for (ClientManagement clientManagement: clientManagements) {
            if (clientManagement.getUser() != null && clientManagement.getUser().getName().equals(name)) {
                return -1;
            }
        }
        return clientManagements.size();
    }

    public static void setPort(int port) {
        ServerManagement.port = port;
    }

}
