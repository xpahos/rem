package ru.yandex.rem.packets.JobPacket;


import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.yandex.rem.packets.Job;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JobPacketImpl implements JobPacket {
    private Log log = LogFactory.getLog(JobPacket.class);

    private String packetsPath;
    private String binariesPath;

    private final String name;
    private final Integer priority;
    private final List<String> notifyEmails;
    private final List<String> waitTags;
    private final String setTag;
    private final Boolean killAllJobsOnError;

    private final String id;
    private PacketState state;

    private Map<Integer, Job> jobs = new HashMap<>();
    private Map<Integer, String> history = new HashMap<>();
    private Map<String, String> files = new HashMap<>();

    public static enum PacketState {
        CREATED,
        WORKABLE,
        PENDING,
        SUSPENDED,
        ERROR,
        SUCCESSFULL,
        HISTORIED,
        WAITING,
        NONINITIALIZED
    }

    @Inject
    public JobPacketImpl(
            @Named("packets.path") String packetsPath,
            @Named("binaries.path") String binariesPath,

            @Assisted("name") String name,
            @Assisted("priority") Integer priority,
            @Assisted("notifyEmails") List<String> notifyEmails,
            @Assisted("waitTags") List<String> waitTags,
            @Assisted("setTag") String setTag,
            @Assisted("killAllJobsOnError") Boolean killAllJobsOnError) throws IOException {
        this.packetsPath = packetsPath;
        this.binariesPath = binariesPath;

        this.name = name;
        this.priority = priority;
        this.notifyEmails = notifyEmails;
        this.waitTags = waitTags;
        this.setTag = setTag;
        this.killAllJobsOnError = killAllJobsOnError;

        this.state = PacketState.NONINITIALIZED;

        this.id = generatePacketId();

        log.info(String.format("Packet initialization: id = %s, name = %s, state = %s", this.id, this.name, this.state.toString()));

        createPlace();
    }

    private String generatePacketId() {
        final Random random = new Random();
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append("pck-");

        char ch;
        while (idBuilder.length() < 10){
            ch = (char) (random.nextInt(74)+48);

            if(Character.isLetter(ch) || Character.isDigit(ch)) {
                idBuilder.append(ch);
            }
        }

        return idBuilder.toString();
    }

    private void createPlace() throws IOException {
        String packetPath = String.format("%s/%s", packetsPath, this.id);

        Files.createDirectories(Paths.get(packetPath));
        setState(PacketState.CREATED);
    }

    public String getId() {
        return id;
    }

    public PacketState getState() {
        return state;
    }

    public void setState(PacketState newState) {
        log.info(String.format("Packet status changed: id = %s, name = %s, old state = %s, new state = %s", this.id, this.name, this.state.toString(), newState.toString()));

        this.state = newState;
    }

    @Override
    public void addJob(Job job) {
        jobs.put(job.getId(), job);
    }

    @Override
    public void addBinary(String binaryName, String checkSum) throws IOException {
        String srcPath = String.format("%s/%s", binariesPath, checkSum);
        String dstPath = String.format("%s/%s/%s", packetsPath, this.id, binaryName);

        try {
            Files.createSymbolicLink(Paths.get(dstPath), Paths.get(srcPath));
            files.put(checkSum, binaryName);

            log.info(String.format("Packet binary added: id = %s, name = %s, binary name = %s, checksum = %s", this.id, this.name, binaryName, checkSum));
        } catch (FileAlreadyExistsException e) {

        }
    }
}
