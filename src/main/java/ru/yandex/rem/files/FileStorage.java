package ru.yandex.rem.files;

import java.io.IOException;

public interface FileStorage {
    public boolean hasBinary(String checkSum);

    public void saveBinary(byte[] bytes) throws IOException;
}
