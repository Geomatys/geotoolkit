/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.AlreadyBoundException;

import org.geotoolkit.console.Action;
import org.geotoolkit.console.Option;
import org.geotoolkit.console.CommandLine;


/**
 * To be launch from the command line for starting or stopping a server waiting for tasks.
 * The server can declare itself as a slave of an other server (i.e. wait for tasks to be
 * submitted by that other server, also named "master").
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class ClusterCommands extends CommandLine {
    /**
     * The name of the machine hosting the RMI registry. If a registry is already running
     * on the local machine (typically by running {@code rmiregistry} on the command-line),
     * then the value for this argument should be {@code "localhost"}.
     * <p>
     * If this argument is omitted, a RMI registry will be automatically started.
     */
    @Option
    protected String registry;

    /**
     * Creates a new {@code ClusterCommands} for the given command-line arguments.
     */
    private ClusterCommands(final String[] arguments) {
        super(null, arguments);
    }

    /**
     * Creates a new instance of {@code ClusterCommands} with the given arguments
     * and {@linkplain #run() run} it.
     *
     * @param arguments Command line arguments.
     */
    public static void main(final String[] arguments) {
        final ClusterCommands console = new ClusterCommands(arguments);
        console.run();
    }

    /**
     * Starts the server as a master waiting for slaves. If no {@code --registry} argument
     * is provided, then a RMI registry will be automatically started on the local host.
     * <p>
     * Note that if a master get no slaves, it will execute the tasks itself.
     */
    @Action(maximalArgumentCount=0)
    protected void master() {
        RemoteService.setRegistryHost(registry);
        try {
            new FinalExecutor(null).bind();
        } catch (RemoteException exception) {
            printException(exception);
            exit(IO_EXCEPTION_EXIT_CODE);
        } catch (AlreadyBoundException exception) {
            printException(exception);
            exit(ILLEGAL_STATE_EXIT_CODE);
        }
        out.println("Ready"); // TODO: localize
    }

    /**
     * Starts the server as a slave available for the given master.
     */
    @Action(minimalArgumentCount=1, maximalArgumentCount=1)
    protected void slave() {
        RemoteService.setRegistryHost(arguments[0]);
        try {
            final TaskExecutor master = (TaskExecutor) RemoteService.getRegistry().lookup(RemoteExecutor.NAME);
            new FinalExecutor(master); // Will register itself as a slave.
        } catch (RemoteException exception) {
            printException(exception);
            exit(IO_EXCEPTION_EXIT_CODE);
        } catch (NotBoundException exception) {
            printException(exception);
            exit(ILLEGAL_STATE_EXIT_CODE);
        }
        out.println("Ready"); // TODO: localize
    }

    /**
     * Shutdown the master or an individual slave. If the server to be shutdown is a master,
     * then every slaves will be shutdown in that process.
     * <p>
     * This action can be followed by an optional argument, which is the host of the server
     * to shutdown. If this argument is omitted, then {@code "localhost"} is assumed.
     */
    @Action(maximalArgumentCount=1)
    protected void shutdown() {
        if (arguments.length != 0) {
            registry = arguments[0];
        }
        if (registry != null) { // Do not create a new registry.
            RemoteService.setRegistryHost(registry);
        }
        try {
            TaskExecutor executor = (TaskExecutor) RemoteService.getRegistry().lookup(RemoteExecutor.NAME);
            executor.shutdown();
        } catch (RemoteException exception) {
            printException(exception);
            exit(IO_EXCEPTION_EXIT_CODE);
        } catch (NotBoundException exception) {
            printException(exception);
            exit(ILLEGAL_STATE_EXIT_CODE);
        }
    }
}
