/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.management;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Base class for a JMX Client.
 * This class only avoid the boil plate code of creating the connection.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultJMXClient {

    private final JMXConnector connector;
    private final MBeanServerConnection mbsc;

    /**
     *
     * @param jmxURL : jmx url, example : service:jmx:rmi://172.16.30.88:8686/jndi/rmi://172.16.30.88:8686/jmxrmi
     * @param user
     * @param password
     * @throws MalformedURLException
     * @throws IOException
     */
    public DefaultJMXClient(final String jmxURL, final String user, final String password) throws MalformedURLException, IOException {
        this(new JMXServiceURL(jmxURL),user,password);
    }

    public DefaultJMXClient(final JMXServiceURL jmxURL, final String user, final String password) throws IOException {
        final Map<String,Object> env = new HashMap<String, Object>();
        final String[] credentials = new String[]{user, password};
        env.put(JMXConnector.CREDENTIALS, credentials);

        this.connector = JMXConnectorFactory.connect(jmxURL, env);
        this.mbsc = connector.getMBeanServerConnection();
    }

    public JMXConnector getJMXConnector() {
        return connector;
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return mbsc;
    }

    /**
     * Create an MBean proxy of T class.
     *
     * @param <T>
     * @param name example : org.samples:type=Manager
     * @param beanClass : expected bean class
     * @return T MBean proxy instance
     * @throws MalformedObjectNameException
     */
    public <T> T getMBean(final String name, final Class<T> beanClass) throws MalformedObjectNameException{
        final ObjectName mbeanName = new ObjectName(name);
        final T mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, beanClass, true);
        return mbeanProxy;
    }

    /**
     * Release jmx connection.
     * @throws IOException
     */
    public void dispose() throws IOException{
        connector.close();
    }

}
