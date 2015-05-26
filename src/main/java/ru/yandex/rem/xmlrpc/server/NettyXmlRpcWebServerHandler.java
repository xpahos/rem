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

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_IMPLEMENTED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Web server handler for the Netty XMLRPC handler
 *
 * @author Keith M. Hughes
 */
@ChannelHandler.Sharable
public class NettyXmlRpcWebServerHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * The web server this handler is attached to
     */
    private NettyXmlRpcWebServer webServer;

    public NettyXmlRpcWebServerHandler(NettyXmlRpcWebServer webServer) {
        this.webServer = webServer;
    }

    public void messageReceived(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else {
            webServer.getLog().warn(
                    String.format("Web server received unknown frame %s", msg
                            .getClass().getName()));
        }

    }

    /**
     * This method will be replaced by messageReceived
     **/
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        messageReceived(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //webServer.channelOpened(e.getChannel());
    }

    /**
     * Handle an HTTP request coming into the server.
     *
     * @param ctx
     *            The channel context for the request.
     * @param req
     *            The HTTP request.
     *
     * @throws Exception
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req)
            throws Exception {


        if (handleWebRequest(ctx, req)) {
            // The method handled the request if the return value was true.
        } else {
            // Nothing we handle.
            webServer.getLog().warn(
                    String.format("Web server has no handlers for request %s",
                            req.getUri()));

            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1,
                    NOT_IMPLEMENTED));
        }
    }

    /**
     * Attempt to handle an HTTP request by scanning through all registered
     * handlers.
     *
     * @param ctx
     *            The context for the request.
     * @param req
     *            The request.
     * @return True if the request was handled, false otherwise.
     */
    private boolean handleWebRequest(ChannelHandlerContext ctx,
                                     FullHttpRequest req) throws IOException {
        if (req.getMethod() != POST) {
            return false;
        }

        XmlRpcServerClientConnection connection = new XmlRpcServerClientConnection(ctx, req, webServer.getXmlRpcServer(), this);
        connection.process();

        return true;
    }

    /**
     * Send an HTTP response to the client.
     *
     * @param ctx
     *            the channel event context
     * @param req
     *            the request which has come in
     * @param res
     *            the response which is being written
     */
    public void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req,
                                 FullHttpResponse res) {
        // Generate an error page if response status code is not OK (200).
        if (res.getStatus().code() != HttpResponseStatus.OK.code()) {
            String status = res.getStatus().toString();

            ByteBuf buffer = Unpooled.copiedBuffer(status, CharsetUtil.UTF_8);
            res.content().writeBytes(buffer);
            buffer.release();

            setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);

        if (!isKeepAlive(req)
                || res.getStatus().code() != HttpResponseStatus.OK.code()
                || req.getMethod() == POST) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Send a success response to the client.
     *
     * @param ctx
     *            the channel event context
     * @param req
     *            the request which has come in
     */
    private void sendSuccessHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req) {
        DefaultFullHttpResponse res = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        sendHttpResponse(ctx, req, res);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        webServer.getLog().error("Exception caught in the web server",
                cause.getCause());
        ctx.close();
    }

    /**
     * Send an error to the remote machine.
     *
     * @param ctx
     *            handler context
     * @param status
     *            the status to send
     */
    public void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ByteBuf buffer = Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();

        // Close the connection as soon as the error message is sent.

        ctx.channel().writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Get the webserver for the handler.
     *
     * @return the webserver for the handler
     */
    public NettyXmlRpcWebServer getWebServer() {
        return webServer;
    }

}