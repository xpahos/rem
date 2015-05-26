package ru.yandex.rem.xmlrpc.server;

import org.apache.ws.commons.util.NamespaceContextImpl;
import org.apache.xmlrpc.common.TypeFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.parser.NullParser;
import org.apache.xmlrpc.parser.TypeParser;


public class NilTypeFactory extends TypeFactoryImpl {

    public NilTypeFactory(XmlRpcController pController) {
        super(pController);
    }

    /** Creates a parser for a parameter or result object compatible with python xmlrpclib(it uses nil instead of xsi:nil).
     * @param pConfig The request configuration.
     * @param pContext A namespace context, for looking up prefix mappings.
     * @param pURI The namespace URI of the element containing the parameter or result.
     * @param pLocalName The local name of the element containing the parameter or result.
     * @return The created parser.
     */
    @Override
    public TypeParser getParser(XmlRpcStreamConfig pConfig, NamespaceContextImpl pContext, String pURI, String pLocalName) {
        if(pLocalName.equals("nil")) {
            return new NullParser();
        }
        return super.getParser(pConfig, pContext, pURI, pLocalName);
    }
}
