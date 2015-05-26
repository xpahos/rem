/*
 * Copyright (C) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.yandex.rem.xmlrpc.server;


import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.server.XmlRpcStreamServer;
import io.netty.bootstrap.ServerBootstrap;

/**
 * An Apache XMLRPC webserver based on Netty.
 *
 * @author Keith M. Hughes
 */
public class NettyXmlRpcWebServer {

    @Inject
    private XmlRpcServerClientRequestProcessorFactoryFactory xmlRpcServerClientRequestProcessorFactoryFactory;

    /**
     * Port for the web server.
     */
    private int port;

    /**
     * Address for the web server.
     */
    private InetAddress address;

    /**
     * Server handler for web sockets.
     */

    private ChannelFuture serverChannel;

    /**
     * Factory for all channels coming into the server.
     */
    private EventLoopGroup parentGroup;
    private EventLoopGroup childGroup;

    /**
     * Logger for the web server.
     */
    private Log log;

    /**
     * Bootstrap for the server.
     */
    private ServerBootstrap bootstrap;

    /**
     * Handler for any requests coming into the server.
     */
    private NettyXmlRpcWebServerHandler serverHandler;

    /**
     * The bridge to the Apache XML RPC code.
     */
    private XmlRpcStreamServer xmlRpcServer = new XmlRpcServerClientConnectionServer();

    @Inject
    public NettyXmlRpcWebServer(@Named("xmlrpc.port") int port, InetAddress address, Log log) {
        this.address = address;
        this.port = port;
        this.log = log;

        serverHandler = new NettyXmlRpcWebServerHandler(this);
    }

    /**
     * Start the web server up.
     */
    public void start(Class methodsHandler) {
        InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());

        PropertyHandlerMapping phm = new CustomPropertyHandlerMapping();
        phm.setRequestProcessorFactoryFactory(xmlRpcServerClientRequestProcessorFactoryFactory);

        phm.setVoidMethodEnabled(true);
        try {
            phm.addHandler("", methodsHandler);
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        xmlRpcServer.setHandlerMapping(phm);

        XmlRpcServerConfigImpl serverConfig =
                (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        xmlRpcServer.setTypeFactory(new NilTypeFactory(xmlRpcServer));

        parentGroup = new NioEventLoopGroup(1);
        childGroup = new NioEventLoopGroup();

        bootstrap = new ServerBootstrap();

        bootstrap.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // Create a default pipeline implementation.
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(serverHandler);

                    }
                });


        serverChannel = bootstrap.bind(new InetSocketAddress(port));
        try {
            serverChannel.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shut the web server down.
     */
    public void shutdown() {
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }

    /**
     * Get the Apache XML RPC server associated with this web server.
     *
     * @return the Apache XML RPC server associated with this web server
     */
    public XmlRpcStreamServer getXmlRpcServer() {
        return xmlRpcServer;
    }


    /**
     * Get the socket port for the web server.
     *
     * @return the socket port
     */
    public int getPort() {
        if (serverChannel != null) {
            return port;
        } else {
            throw new RuntimeException(
                    "XMLRPC server not started up yet, port not available");
        }
    }

    /**
     * Return the log for the web server.
     *
     * @return the log
     */
    public Log getLog() {
        return log;
    }
}