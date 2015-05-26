package ru.yandex.rem.packets;

import java.util.List;
import java.util.Random;

public class Job {
    private final String shell;
    private final List<Integer> parents;
    private final List<Integer> pipeParents;
    private final String setTag;
    private final Integer tries;
    private final Integer maxErrLen;
    private final Integer retryDelay;
    private final Boolean pipeFail;
    private final String description;
    private final Integer notifyTimeout;
    private final Integer maxWorkingTime;

    private final Integer id;

    public Job(String shell, List<Integer> parents, List<Integer> pipeParents, String setTag, Integer tries,
               Integer maxErrLen, Integer retryDelay, Boolean pipeFail, String description, Integer notifyTimeout, Integer maxWorkingTime) {
        final Random random = new Random();

        this.shell = shell;
        this.parents = parents;
        this.pipeParents = pipeParents;
        this.setTag = setTag;
        this.tries = tries;
        this.maxErrLen = maxErrLen;
        this.retryDelay = retryDelay;
        this.pipeFail = pipeFail;
        this.description = description;
        this.notifyTimeout = notifyTimeout;
        this.maxWorkingTime = maxWorkingTime;

        // For compatibility with old REM packets
        this.id = random.nextInt(999999999) + 1000000000;
    }

    public Integer getId() {
        return id;
    }
}
