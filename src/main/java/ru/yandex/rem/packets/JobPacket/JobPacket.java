package ru.yandex.rem.packets.JobPacket;


import javafx.util.StringConverter;
import ru.yandex.rem.packets.Job;

import java.io.IOException;

public interface JobPacket {

    public String getId();

    public void addJob(Job job);

    public void addBinary(String binaryName, String checkSum) throws IOException;

    public JobPacketImpl.PacketState getState();
}
