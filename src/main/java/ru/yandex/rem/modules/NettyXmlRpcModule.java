package ru.yandex.rem.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.yandex.rem.xmlrpc.server.NettyXmlRpcWebServer;
import ru.yandex.rem.xmlrpc.server.XmlRpcServerClientRequestProcessorFactoryFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class NettyXmlRpcModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NettyXmlRpcModule.class);
        bind(XmlRpcServerClientRequestProcessorFactoryFactory.class).in(Singleton.class);
        bind(Integer.TYPE).annotatedWith(Names.named("xmlrpc.port")).toInstance(3133);
    }


    @Provides
    @Singleton
    Log providesLog(){
        return LogFactory.getLog(NettyXmlRpcWebServer.class);
    }

    @Provides
    InetAddress providesInetAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
}
