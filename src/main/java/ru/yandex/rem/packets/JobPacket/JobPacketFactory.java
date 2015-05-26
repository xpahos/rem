package ru.yandex.rem.packets.JobPacket;

import com.google.inject.assistedinject.Assisted;

import java.util.List;

public interface JobPacketFactory {
    JobPacket create(
            @Assisted("name") String name,
            @Assisted("priority") Integer priority,
            @Assisted("notifyEmails") List<String> notifyEmails,
            @Assisted("waitTags") List<String> waitTags,
            @Assisted("setTag") String setTag,
            @Assisted("killAllJobsOnError") Boolean killAllJobsOnError);
}
