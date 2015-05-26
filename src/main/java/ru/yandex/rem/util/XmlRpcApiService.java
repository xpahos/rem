package ru.yandex.rem.util;

import com.google.inject.Inject;

import ru.yandex.rem.xmlrpc.api.RemMethodsHandler;
import ru.yandex.rem.xmlrpc.server.NettyXmlRpcWebServer;

import java.net.UnknownHostException;

public class XmlRpcApiService {
    private Thread nettyProcess;

    @Inject
    private NettyXmlRpcWebServer xmlRpcServer;

    public XmlRpcApiService() throws UnknownHostException {

        Runnable nettyRunnable = () -> {
            xmlRpcServer.start(RemMethodsHandler.class);
        };

        nettyProcess = new Thread(nettyRunnable);
    }

    public final synchronized void start() {
        nettyProcess.start();
    }

    public final synchronized void stop() {
        xmlRpcServer.shutdown();
    }
}
