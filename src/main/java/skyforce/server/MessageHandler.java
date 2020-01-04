package skyforce.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import skyforce.entry.User;
import skyforce.server.network.IMessageHandler;
import skyforce.server.network.Message;
import skyforce.server.network.MessageCode;
import skyforce.server.network.MessageWriter;

import java.io.DataInputStream;
import java.io.IOException;

public class MessageHandler implements IMessageHandler {

    private ClientManagement clientManagement;

    public MessageHandler(ClientManagement clientManagement) {
        this.clientManagement = clientManagement;
    }

    @Override
    public void onMessage(Message message) {
        System.out.printf("Get message %d\n", message.getCommand());
        try {
            DataInputStream data = message.reader();
            int roomId;
            RoomAreaManagement currentRoom;
            Gson gson = new GsonBuilder().create();
            switch (message.getCommand()) {
                case MessageCode.HANSAKE_CODE:
                    clientManagement.hansakeMessage();
                    break;
                case MessageCode.REGISTER_PLAYER:
                    String name = data.readUTF().trim();
                    int idUser = ServerManagement.getInstance().RegisterPlayer(name);
                    if (idUser > 0) {
                        clientManagement.setUser(new User(idUser, name));
                        clientManagement.sendMessage(MessageWriter.getInstance().getMessageResponseRegisterPlayer((byte)1));
                    } else {
                        clientManagement.sendMessage(MessageWriter.getInstance().getMessageResponseRegisterPlayer((byte)0));
                    }
                    break;
                case MessageCode.GET_PLAYER_IN_ROOM:
                    currentRoom = clientManagement.getCurrentRoom();
                    for (ClientManagement cl : currentRoom.getClientManagements()) {
                        if (cl.getUser().getUuid() != clientManagement.getUser().getUuid()) {
                            Message ms = MessageWriter.getInstance().getMessageGetPlayerRoomArea(gson.toJson(cl.getUser()));
                            clientManagement.sendMessage(ms);
                        }
                    }
                    break;
                case MessageCode.CREATE_ROOM_CODE:
                    roomId = ServerManagement.getInstance().CreateRoom();
                    clientManagement.sendMessage(MessageWriter.getInstance().getMessageResponseCreateRoom(roomId));
                    break;
                case MessageCode.FIND_ROOM_CODE:
                    roomId = ServerManagement.getInstance().GetRoomAlready();
                    clientManagement.sendMessage(MessageWriter.getInstance().getMessageResponseFindRoom(roomId != -1 ? (byte) 1 : (byte) 0, roomId));
                    break;
                case MessageCode.JOIN_ROOM_CODE:
                    int idRoom = data.readShort();
                    clientManagement.sendMessage(MessageWriter.getInstance().getMessageJoinRoom(ServerManagement.getInstance().JoinRoom(idRoom, clientManagement)));
                    break;
                default:
                    currentRoom = clientManagement.getCurrentRoom();
                    for (ClientManagement cl : currentRoom.getClientManagements()) {
                        if (cl.getUser().getUuid() != clientManagement.getUser().getUuid()) {
                            cl.sendMessage(message);
                        }
                    }
                    break;
            }
            message.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
            message.cleanup();
        }
    }

    @Override
    public void onConnectionFail() {

    }

    @Override
    public void onDisconnected() {
        System.out.println("Client disconnected");
        clientManagement.getCurrentRoom().leftRoom(clientManagement);
    }

    @Override
    public void onConnectOK() {

    }
}
