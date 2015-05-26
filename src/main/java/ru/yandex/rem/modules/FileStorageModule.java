package ru.yandex.rem.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import ru.yandex.rem.files.FileStorage;
import ru.yandex.rem.files.FileStorageImpl;
import ru.yandex.rem.packets.PacketStorage;
import ru.yandex.rem.packets.PacketStorageImpl;

public class FileStorageModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("binaries.path")).toInstance("/tmp/rem/bin");

        bind(FileStorage.class).to(FileStorageImpl.class).in(Singleton.class);
    }


}
