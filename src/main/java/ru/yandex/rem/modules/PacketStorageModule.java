package ru.yandex.rem.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import ru.yandex.rem.packets.JobPacket.JobPacket;
import ru.yandex.rem.packets.JobPacket.JobPacketFactory;
import ru.yandex.rem.packets.JobPacket.JobPacketImpl;
import ru.yandex.rem.packets.PacketStorage;
import ru.yandex.rem.packets.PacketStorageImpl;


public class PacketStorageModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PacketStorage.class).to(PacketStorageImpl.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("packets.path")).toInstance("/tmp/rem/packets");
        bind(String.class).annotatedWith(Names.named("binaries.path")).toInstance("/tmp/rem/bin");

        install(new FactoryModuleBuilder()
                .implement(JobPacket.class, JobPacketImpl.class)
                .build(JobPacketFactory.class));
    }


}
