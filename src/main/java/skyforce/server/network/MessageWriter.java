package skyforce.server.network;

import java.io.DataOutputStream;
import java.io.IOException;

public class MessageWriter {
    private static MessageWriter instance;

    private MessageWriter() {
    }

    public static MessageWriter getInstance() {
        if (instance == null) {
            instance = new MessageWriter();
        }
        return instance;
    }

    public Message getMessageHansake(byte[] keyHansake) throws IOException {
        Message ms = new Message(MessageCode.HANSAKE_CODE);
        DataOutputStream ds = ms.writer();
        ds.writeByte(keyHansake[0]);
        for (int i = 1; i < keyHansake.length; i++)
            ds.writeByte(keyHansake[i]);
        ds.flush();
        return ms;
    }

    public Message getMessageGetPlayerRoomArea(String info) {
        Message ms = new Message(MessageCode.GET_PLAYER_IN_ROOM);
        System.out.println("UserInfo " + info);
        try {
            DataOutputStream ds = ms.writer();
            ds.writeShort(info.length());
            ds.writeBytes(info.trim());
            ds.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ms;
    }

    public Message getMessagePlayerJoinRoomArea(String info) {
        Message ms = new Message(MessageCode.PLAYER_JOIN_AREROOM_CODE);
        System.out.println("UserInfo " + info);
        try {
            DataOutputStream ds = ms.writer();
            ds.writeShort(info.length());
            ds.writeBytes(info.trim());
            ds.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ms;
    }

    public Message getMessageJoinRoom(byte status) {
        Message ms = new Message(MessageCode.JOIN_ROOM_CODE);
        try {
            DataOutputStream ds = ms.writer();
            ds.writeByte(status);
            ds.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ms;
    }

    public Message getMessageResponseFindRoom(byte status, int roomId) {
        Message ms = new Message(MessageCode.FIND_ROOM_CODE);
        try {
            DataOutputStream ds = ms.writer();
            ds.writeByte(status);
            if (status == 1) {
                ds.writeShort(roomId);
            }
            ds.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ms;
    }

    public Message getMessageResponseCreateRoom(int roomId) {
        Message ms = new Message(MessageCode.CREATE_ROOM_CODE);
        try {
            DataOutputStream ds = ms.writer();
            ds.writeShort(roomId);
            ds.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ms;
    }

    public Message getMessageResponseRegisterPlayer(byte status) {
        Message ms = new Message(MessageCode.REGISTER_PLAYER);
        try {
            DataOutputStream ds = ms.writer();
            ds.writeByte(status);
            ds.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ms;
    }


    public Message getMessageLeftRoomArea() {
        Message ms = new Message(MessageCode.PLAYER_LEFT_AREROOM_CODE);
        return ms;
    }

}
