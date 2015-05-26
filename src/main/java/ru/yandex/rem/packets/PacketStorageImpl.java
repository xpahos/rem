package ru.yandex.rem.packets;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.yandex.rem.packets.JobPacket.JobPacket;

import java.util.HashMap;
import java.util.Map;

public class PacketStorageImpl implements PacketStorage {
    private Log log = LogFactory.getLog(PacketStorageImpl.class);

    Map<String, JobPacket> jobPackets = new HashMap<>();

    @Override
    public void addPacket(JobPacket jobPacket)  {
        jobPackets.put(jobPacket.getId(), jobPacket);
    }

    @Override
    public JobPacket getPacket(String id) {
        return jobPackets.get(id);
    }


}
