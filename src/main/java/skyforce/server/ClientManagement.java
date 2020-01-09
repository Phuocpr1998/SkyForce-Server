package skyforce.server;

import skyforce.entry.User;
import skyforce.server.network.IMessageHandler;
import skyforce.server.network.ISession;
import skyforce.server.network.Message;
import skyforce.server.network.MessageWriter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientManagement implements ISession {

    private Socket socketClient;
    private User user;
    private RoomAreaManagement currentRoom;
    private IMessageHandler messageHandler;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private MessageSender messageSender;

    private Thread threadSender;
    private Thread threadReceiver;

    private boolean connected;

    private byte[] keyHansake = "skyforce".getBytes();

    public ClientManagement(Socket socketClient) throws IOException {
        this.socketClient = socketClient;
        this.dataInputStream = new DataInputStream(socketClient.getInputStream());
        this.dataOutputStream = new DataOutputStream(socketClient.getOutputStream());
        this.messageSender = new MessageSender();
        this.messageHandler = new MessageHandler(this);

        this.threadSender = new Thread(this.messageSender);
        this.threadReceiver = new Thread(new MessageReceiver());
        this.threadReceiver.start();
        this.user = null;

        this.connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void setHandler(IMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public RoomAreaManagement getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(RoomAreaManagement currentRoom) {
        this.currentRoom = currentRoom;
        this.currentRoom.joinRoom(this);
    }

    @Override
    public void sendMessage(Message message) {
        this.messageSender.AddMessage(message);
    }

    @Override
    public void close() {
        try {
            this.dataOutputStream.close();
            this.dataInputStream.close();
            this.socketClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                while (socketClient.isConnected()) {
                    Message message = readMessage();
                    messageHandler.onMessage(message);
                    message.cleanup();
                }
            } catch (Exception ex) {
                if (messageHandler != null) {
                    messageHandler.onDisconnected();
                }
                close();
                ex.printStackTrace();
            }
        }

        private Message readMessage() throws Exception {
            byte cmd = dataInputStream.readByte();
            int size1 = dataInputStream.readByte();
            int size2 = dataInputStream.readByte();
            int size = size1 * 256 + size2;
            byte[] data = new byte[size];
            int len = 0;
            int byteRead = 0;
            while (len != -1 && byteRead < size) {
                len = dataInputStream.read(data, byteRead, size - byteRead);
                if (len > 0)
                    byteRead += len;
            }
            return new Message(cmd, data);
        }
    }

    class MessageSender implements Runnable {

        private List<Message> messages;

        public MessageSender() {
            messages = new ArrayList<Message>();
        }

        @Override
        public void run() {
            while (connected && socketClient.isConnected()) {
                while (messages.size() > 0) {
                    Message ms = messages.get(0);
                    doSendMessage(ms);
                    messages.remove(0);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void AddMessage(Message message) {
            messages.add(message);
        }
    }

    protected synchronized void doSendMessage(Message m) {
        byte[] data = m.getData();
        try {
            dataOutputStream.writeByte(m.getCommand());
            if (data != null) {
                int size = data.length;
                if (size > 0) {
                    dataOutputStream.writeByte(size / 256);
                    dataOutputStream.writeByte(size % 256);
                    dataOutputStream.write(data);
                } else {
                    dataOutputStream.writeByte(0);
                    dataOutputStream.writeByte(0);
                }
            } else {
                dataOutputStream.writeByte(0);
                dataOutputStream.writeByte(0);
            }
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        m.cleanup();
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public void hansakeMessage() throws IOException {
        System.out.println("hansakeMessage");
        Message ms = MessageWriter.getInstance().getMessageHansake(keyHansake);
        doSendMessage(ms);
        connected = true;
        if (!threadSender.isAlive()) {
            threadSender.start();
        }
    }
}
