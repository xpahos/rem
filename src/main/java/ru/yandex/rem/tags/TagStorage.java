package ru.yandex.rem.tags;

import java.util.Map;

public interface TagStorage {
    boolean setTag(String tagName);

    boolean unsetTag(String tagName);

    boolean checkTag(String tagName);

    Map getTags(String nameRegex, String namePrefix);

    void start();

    void stop();
}
