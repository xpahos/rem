package ru.yandex.rem.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ru.yandex.rem.tags.TagStorage;
import ru.yandex.rem.tags.TagStorageImpl;
import ru.yandex.rem.xmlrpc.server.XmlRpcServerClientRequestProcessorFactoryFactory;

public class TagStorageModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TagStorage.class).to(TagStorageImpl.class).in(Singleton.class);
    }


}
