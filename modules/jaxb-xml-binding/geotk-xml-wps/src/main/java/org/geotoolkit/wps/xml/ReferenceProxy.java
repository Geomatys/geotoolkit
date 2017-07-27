/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.xml;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Class used to reflect a WPS reference.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ReferenceProxy {

    /**
     * Get reference.
     *
     * @return WPS reference.
     */
    Reference getReference();

    /**
     * Create a fake object.
     *
     * @param <T>
     * @param ref reference to real object.
     * @param clazz wanted class
     * @return reference proxy
     */
    public static <T> T create(final Reference ref, final Class<T> clazz) {

        final Class[] types = new Class[]{
            clazz,
            ReferenceProxy.class
        };

        final InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(method.getName().equals("getReference")) return ref;
                return method.invoke(ref, args);
            }
        };

        return (T) Proxy.newProxyInstance(Reference.class.getClassLoader(), types, handler);
    }

}
