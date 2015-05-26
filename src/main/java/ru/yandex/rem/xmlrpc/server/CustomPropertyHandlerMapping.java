package ru.yandex.rem.xmlrpc.server;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomPropertyHandlerMapping extends PropertyHandlerMapping {
    @Override
    protected void registerPublicMethods(String pKey,
                                         Class pType) throws XmlRpcException {
        Map map = new HashMap();
        Method[] methods = pType.getMethods();
        for (int i = 0;  i < methods.length;  i++) {
            final Method method = methods[i];
            if (!isHandlerMethod(method)) {
                continue;
            }

            final String name;
            if(pKey.isEmpty())
                name = method.getName();
            else
                name = pKey + "." + method.getName();
            Method[] mArray;
            Method[] oldMArray = (Method[]) map.get(name);
            if (oldMArray == null) {
                mArray = new Method[]{method};
            } else {
                mArray = new Method[oldMArray.length+1];
                System.arraycopy(oldMArray, 0, mArray, 0, oldMArray.length);
                mArray[oldMArray.length] = method;
            }
            map.put(name, mArray);
        }

        for (Iterator iter = map.entrySet().iterator();  iter.hasNext();  ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            Method[] mArray = (Method[]) entry.getValue();
            handlerMap.put(name, newXmlRpcHandler(pType, mArray));
        }
    }
}
