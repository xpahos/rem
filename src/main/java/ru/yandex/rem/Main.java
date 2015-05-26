
package ru.yandex.rem;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import ru.yandex.rem.modules.FileStorageModule;
import ru.yandex.rem.modules.PacketStorageModule;
import ru.yandex.rem.modules.TagStorageModule;
import ru.yandex.rem.util.TagStorageService;
import ru.yandex.rem.util.XmlRpcApiService;
import ru.yandex.rem.modules.NettyXmlRpcModule;

class Main {
    public static void main(String[] argv) throws Exception {
        Injector injector = Guice.createInjector(modules());

        startServices(injector);
    }

    protected static void startServices(Injector injector) throws Exception {
        injector.getInstance(TagStorageService.class).start();
        injector.getInstance(XmlRpcApiService.class).start();
    }

    protected static void stopServices(Injector injector) throws Exception {
        injector.getInstance(XmlRpcApiService.class).stop();
        injector.getInstance(TagStorageService.class).stop();

    }

    public static Module[] modules() {
        return new Module[] {
                new TagStorageModule(),
                new NettyXmlRpcModule(),
                new PacketStorageModule(),
                new FileStorageModule(),
        };
    }
}