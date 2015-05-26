package ru.yandex.rem.files;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class FileStorageImpl implements FileStorage {
    @Inject @Named("binaries.path")
    private String binariesPath;

    private Map<String, String> files = new HashMap<>();

    @Override
    public boolean hasBinary(String checkSum) {
        if(!files.containsKey(checkSum))
            return false;

        String binaryFilePath = String.format("%s/%s", binariesPath, checkSum);

        return Files.exists(Paths.get(binaryFilePath));
    }

    public void saveBinary(byte[] bytes) throws IOException {
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            byte[] byteCheckSum = md5Digest.digest(bytes);

            String checkSum = new String(HexBin.encode(byteCheckSum)).toLowerCase();
            String binaryFilePath = String.format("%s/%s", binariesPath, checkSum);

            if(Files.notExists(Paths.get(binaryFilePath))) {
                Files.write(Paths.get(binaryFilePath), bytes);
                Files.setPosixFilePermissions(Paths.get(binaryFilePath), PosixFilePermissions.fromString("r-xr-xr-x"));
            }
        } catch (NoSuchAlgorithmException e) {
        }
    }
}
