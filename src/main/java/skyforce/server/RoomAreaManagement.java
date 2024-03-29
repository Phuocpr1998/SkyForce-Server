package skyforce.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import skyforce.entry.User;
import skyforce.server.network.Message;
import skyforce.server.network.MessageWriter;

import java.util.ArrayList;
import java.util.List;

public class RoomAreaManagement {
    private List<ClientManagement> clientManagements;
    private int roomId;

    public RoomAreaManagement(int roomId) {
        this.roomId   = roomId;
        this.clientManagements = new ArrayList<ClientManagement>();
    }

    public List<ClientManagement> getClientManagements() {
        return clientManagements;
    }

    public void setClientManagements(List<ClientManagement> clientManagements) {
        this.clientManagements = clientManagements;
    }


    public int getRoomId() {
        return roomId;
    }

    public void leftRoom(ClientManagement clientManagement) {
        System.out.println("Client left room");
        clientManagements.remove(clientManagement);
        if (clientManagements.size() == 0) {
            ServerManagement.getInstance().RemoveRoom(this);
        } else {
            Message ms = MessageWriter.getInstance().getMessageLeftRoomArea();
            for (ClientManagement cl : clientManagements) {
                cl.sendMessage(ms);
            }
        }
    }

    public void joinRoom(ClientManagement clientManagement) {
        System.out.println("Client join room");
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        clientManagements.add(clientManagement);
        Message ms = MessageWriter.getInstance().getMessagePlayerJoinRoomArea(gson.toJson(clientManagement.getUser()));
        for (ClientManagement cl : clientManagements) {
            if (cl.getUser().getUuid() != clientManagement.getUser().getUuid())
                cl.sendMessage(ms);
        }
    }

    public void notifyAllClient(User userChange, Message message) {
        for (ClientManagement cl : clientManagements) {
            if (cl.getUser() != null && cl.getUser().getUuid() != userChange.getUuid())
                cl.sendMessage(message);
        }
    }

    public boolean isFully() {
        if (clientManagements.size() >= ServerManagement.maxUserInRoom) {
            return true;
        }
        return false;
    }
}
