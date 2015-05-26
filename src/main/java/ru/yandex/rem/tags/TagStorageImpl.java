package ru.yandex.rem.tags;


import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TagStorageImpl implements TagStorage {

    private Map<String, Boolean> tagsMap = new HashMap<>();

    private SortedMap tagsExpirationMap = new TreeMap<>();

    private static final Logger logger = Logger.getLogger(TagStorageImpl.class);

    private ScheduledExecutorService executor;

    public synchronized boolean setTag(String tagName) {
        tagsMap.put(tagName, true);

        return true;
    }

    public synchronized boolean unsetTag(String tagName) {
        tagsMap.put(tagName, false);

        return true;
    }

    public synchronized Map getTags(String nameRegex, String namePrefix) {
        if(nameRegex == null && namePrefix == null)
            return tagsMap;

        Map<String, Boolean> filteredTagsMap = tagsMap;
        if(namePrefix != null) {
             filteredTagsMap = filteredTagsMap.entrySet().stream()
                    .filter(p -> p.getKey().startsWith(namePrefix))
                    .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        } else if (nameRegex != null) {
            final Pattern pattern = Pattern.compile(nameRegex);
            filteredTagsMap = filteredTagsMap.entrySet().stream()
                    .filter(p -> pattern.matcher(p.getKey()).matches())
                    .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        }
        return filteredTagsMap;
    }

    public synchronized boolean checkTag(String tagName) {
        try {
            return tagsMap.get(tagName);
        } catch (NullPointerException e) {
            return false;
        }

    }


    public void start() {
        executor = Executors.newScheduledThreadPool(1);

        DateTime now = new DateTime();
        int secondsTillNextMinute = 60 - now.getSecondOfMinute() + 1;

        executor.scheduleWithFixedDelay(() -> {
            try {
                logger.warn("Starting backup");
            } catch (Exception e) {
                logger.error("Backup error: " + e, e);
            }
        }, secondsTillNextMinute, 600, TimeUnit.SECONDS);

    }

    public void stop() {
        executor.shutdown();
    }
}
