package ru.yandex.rem.xmlrpc.api;

import com.google.inject.Inject;
import ru.yandex.rem.files.FileStorage;
import ru.yandex.rem.packets.Job;
import ru.yandex.rem.packets.JobPacket.JobPacket;
import ru.yandex.rem.packets.JobPacket.JobPacketFactory;
import ru.yandex.rem.packets.PacketStorage;
import ru.yandex.rem.tags.TagStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class RemMethodsHandler {
    @Inject
    private TagStorage tagStorage;
    @Inject
    private PacketStorage packetStorage;
    @Inject
    private FileStorage fileStorage;
    @Inject
    JobPacketFactory jobPacketFactory;

    public String version() {
        return "0.0.1";
    }

    public Map list_tags(String nameRegex, String namePrefix, Boolean memoryOnly) {
        return tagStorage.getTags(nameRegex, namePrefix);
    }

    public boolean set_tag(String tagName) {
        return tagStorage.setTag(tagName);
    }

    public boolean unset_tag(String tagName) {
        return tagStorage.unsetTag(tagName);
    }

    public boolean check_tag(String tagName) {
        return tagStorage.checkTag(tagName);
    }

    public String create_packet(String name, Integer priority, List<Object> notifyEmails,
                                List<Object> waitTags, String setTag,
                                Boolean killAllJobsOnError, Integer packetNamePolicy) throws IOException {

        List<String> stringNotifyEmails = notifyEmails.stream().map(o -> o.toString()).collect(Collectors.toList());
        List<String> stringWaitTags = waitTags.stream().map(o -> o.toString()).collect(Collectors.toList());

        JobPacket jobPacket = jobPacketFactory.create(name, priority, stringNotifyEmails, stringWaitTags, setTag, killAllJobsOnError);
        packetStorage.addPacket(jobPacket);

        return jobPacket.getId();
    }

    public Integer pck_add_job(String packetId, String shell, List<Object> parents, List<Object> pipeParents, String setTag, Integer tries,
                              Integer maxErrLen, Integer retryDelay, Boolean pipeFail, String description, Integer notifyTimeout, Integer maxWorkingTime) {

        List<Integer> integerParents = parents.stream().map(o -> (Integer) o).collect(Collectors.toList());
        List<Integer> integerPipeParents = pipeParents.stream().map(o -> (Integer) o).collect(Collectors.toList());

        Job job = new Job(shell, integerParents, integerPipeParents, setTag, tries, maxErrLen, retryDelay, pipeFail, description, notifyTimeout, maxWorkingTime);

        JobPacket jobPacket = packetStorage.getPacket(packetId);
        jobPacket.addJob(job);

        return job.getId();
    }

    public Map<String, String> pck_status(String packetId) {
        Map<String, String> status = new HashMap<>();
        JobPacket jobPacket = packetStorage.getPacket(packetId);

        //TODO: not all fields are implemented
        status.put("pck_id", jobPacket.getId());
        status.put("state", jobPacket.getState().toString());

        return status;
    }

    public boolean save_binary(byte[] bytes) throws IOException {
        fileStorage.saveBinary(bytes);
        return true;
    }

    public boolean check_binary_and_lock(String checkSum, String localPath) {
        return fileStorage.hasBinary(checkSum);
    }

    public boolean pck_add_binary(String packetId, String binaryName, String checkSum) throws IOException {
        packetStorage.getPacket(packetId).addBinary(binaryName, checkSum);

        return true;
    }
}
