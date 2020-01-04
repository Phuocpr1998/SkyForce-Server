package skyforce.server;

import skyforce.server.network.IMessageHandler;
import skyforce.server.network.Message;
import skyforce.server.network.MessageCode;

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
            switch (message.getCommand()) {
                case MessageCode.HANSAKE_CODE:
                    clientManagement.hansakeMessage();
                    break;
                default:
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
