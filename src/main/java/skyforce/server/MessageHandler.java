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
        try {
            DataInputStream data = message.reader();
            int roomId;
            RoomAreaManagement currentRoom;
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            switch (message.getCommand()) {
                case MessageCode.HANSAKE_CODE:
                    clientManagement.hansakeMessage();
                    break;
                case MessageCode.REGISTER_PLAYER:
                    String name = data.readUTF().trim();
                    int idUser = User.UserCount;
                    if (ServerManagement.getInstance().RegisterPlayer(name)) {
                        clientManagement.setUser(new User(idUser, name));
                        System.out.printf("User %d %s register\n", idUser, name);
                        clientManagement.sendMessage(MessageWriter.getInstance().getMessageResponseRegisterPlayer((byte) 1, idUser));
                    } else {
                        clientManagement.sendMessage(MessageWriter.getInstance().getMessageResponseRegisterPlayer((byte) 0, 0));
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
                    roomId = ServerManagement.getInstance().CreateRoom(clientManagement);
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
                            Message msg = new Message(message.getCommand());
                            try {
                                while (true) {
                                    msg.writer().write(data.readByte());
                                }
                            } catch (Exception ex) {
                                cl.sendMessage(msg);
                            }
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
        if (clientManagement.getCurrentRoom() != null) {
            clientManagement.getCurrentRoom().leftRoom(clientManagement);
        }
        ServerManagement.getInstance().RemoveClient(clientManagement);
    }

    @Override
    public void onConnectOK() {

    }
}
