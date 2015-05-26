package ru.yandex.rem.packets;


import ru.yandex.rem.packets.JobPacket.JobPacket;

public interface PacketStorage {
    public void addPacket(JobPacket jobPacket);
    public JobPacket getPacket(String name);
}
