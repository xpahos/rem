package ru.yandex.rem.util;

import com.google.inject.Inject;
import ru.yandex.rem.tags.TagStorage;

public class TagStorageService {
    @Inject
    private TagStorage tagStorge;

    public final synchronized void start() {
        tagStorge.start();
    }

    public final synchronized void stop() {
        tagStorge.stop();
    }
}
