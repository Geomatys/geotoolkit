/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.rmi;

import java.util.logging.Logger;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import net.jcip.annotations.ThreadSafe;

import org.apache.sis.util.logging.Logging;


/**
 * Base class of remote objects to be binded to the RMI {@linkplain Registry registry}.
 * In the context of Geotk, those objects are typically services made available to
 * other machines.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
class RemoteService extends UnicastRemoteObject {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -824997613540569870L;

    /**
     * The name of the matching hosting the RMI registry.
     * <ul>
     *   <li>{@code null}: No server - start the registry ourself.</li>
     *   <li>{@code "localhost"}: Do not start the registry.</li>
     *   <li>Other: Use the registry available on the named server.</li>
     * </ul>
     */
    private static String registryHost = "localhost";

    /**
     * {@code true} if the RMI registry has been created or fetched.
     */
    private static boolean registryStarted;

    /**
     * If this object has been bind to a RMI registry, that registry.
     * Otherwise {@code null}.
     */
    private Registry registry;

    /**
     * The name of the object to lookup from the RMI registry.
     */
    private final String name;

    /**
     * Creates a new remote service.
     *
     * @param name The name of the object to lookup from the RMI registry.
     * @throws RemoteException If an error occurred while exporting this service.
     */
    protected RemoteService(final String name) throws RemoteException {
        this.name = name;
    }

    /**
     * Sets the name of the registry server. The default value is {@code "localhost"},
     * which means that the registry must be already running (typically by invocation
     * of {@code rmiregistry}, which is a command-line tool bundled in the JDK).
     * <p>
     * {@code null} is a special value meaning that no server is currently hosting a
     * RMI registry, and consequently a new one will need to be started automatically
     * when first needed.
     *
     * @param  The name of the maching hosting the RMI registry, or {@code null} if none.
     * @throws IllegalStateException If a RMI registry has already been created of fetched
     *         prior this method call.
     */
    public static synchronized void setRegistryHost(final String host) throws IllegalStateException {
        if (registryStarted) {
            throw new IllegalStateException();
        }
        registryHost = host;
    }

    /**
     * Returns the RMI registry. If there is no host running a registry (i.e. {@link #registryHost}
     * is null), a new one will be created in the current JVM.
     *
     * @return The RMI registry.
     * @throws RemoteException If an error occurred while fething or creating the registry.
     */
    public static synchronized Registry getRegistry() throws RemoteException {
        final Registry registry;
        final String registryHost = RemoteService.registryHost;
        if (registryHost == null) {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } else if (registryHost.equalsIgnoreCase("localhost")) {
            registry = LocateRegistry.getRegistry();
        } else {
            registry = LocateRegistry.getRegistry(registryHost);
        }
        registryStarted = true;
        return registry;
    }

    /**
     * Binds this object to the RMI registry, if not already done.
     *
     * @throws RemoteException If an error occurred while binding this object.
     * @throws AlreadyBoundException If an object of the same name is already bound.
     */
    public synchronized void bind() throws RemoteException, AlreadyBoundException {
        if (registry == null) {
            registry = getRegistry();
        }
        registry.bind(name, this);
    }

    /**
     * Shutdown this service.
     *
     * @throws RemoteException If a RMI error occurred.
     */
    public synchronized void shutdown() throws RemoteException {
        if (registry != null) try {
            registry.unbind(name);
        } catch (NotBoundException exception) {
            // Someone else did the unbinding. This is unexpected, but not fatal.
            Logging.unexpectedException(logger(), RemoteService.class, "shutdown", exception);
        }
        registry = null;
    }

    /**
     * Returns the logger. We omit the {@code "internal"} part in the package name since
     * we don't want to expose the internal packages to the user.
     */
    static Logger logger() {
        return Logging.getLogger("org.geotoolkit.rmi");
    }
}
