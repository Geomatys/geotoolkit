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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.ExecutionException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.factory.Hints;


/**
 * Static methods related to RMI usage in Geotoolkit.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@Static
public final class RMI {
    /**
     * Key for the name of a machine where to delegate the work.
     * A value for this key can be given to the system default hints.
     */
    public static final Hints.Key REMOTE_EXECUTOR = new Hints.Key(String.class);

    /**
     * The executor. Will be created only when first needed, in order to give a chance to the
     * caller to configure his system first, and to propagate the {@link RemoteException} to
     * the caller in case of failure.
     */
    private static TaskExecutor executor;

    /**
     * Do not allow instantiation of this class.
     */
    private RMI() {
    }

    /**
     * Executes the given task, either locally or on remote machines. I/O exceptions will be
     * unwrapped as if the {@code task.call()} method had been invoked directly (as opposed
     * to having the exception wrapped in a {@link ExecutionException}). This is in order to
     * make the stack trace more straightforward in the common case where the task is run
     * locally.
     * <p>
     * This method is appropriate for tasks that are declared to thrown {@link IOException},
     * and no other checked exceptions. If nevertheless an other kind of checked exception
     * occurs, then it will be wrapped in a {@link ServerException}.
     *
     * @param  <Output> The type of the result.
     * @param  task The task to execute.
     * @return The result of the task execution.
     * @throws IOException If an I/O operation failed. It may be a {@link RemoteException}
     *         if the task was executed on a remote machine.
     */
    public static <Output> Output execute(final ShareableTask<?,Output> task) throws IOException {
        TaskExecutor executor;
        synchronized (RMI.class) {
            executor = RMI.executor;
            if (executor == null) {
                final Object server = Hints.getSystemDefault(REMOTE_EXECUTOR);
                if (server == null) {
                    executor = new LocalExecutor();
                } else try {
                    executor = (TaskExecutor) LocateRegistry.getRegistry(server.toString()).lookup(RemoteExecutor.NAME);
                } catch (NotBoundException exception) {
                    RemoteService.logger().warning(exception.toString());
                    executor = new LocalExecutor();
                }
                RMI.executor = executor;
            }
        }
        final TaskFuture<Output> result = executor.submit(task);
        try {
            return result.get();
        } catch (ExecutionException exception) {
            final Throwable cause = exception.getCause();
            /*
             * Unwrap Error only if the task was executed on the local machine. We do not unwrap
             * Error otherwise because it is closely tied to the configuration of the machine that
             * executed the code (classpath, available memory, etc.), so an error that occured on
             * a remote machine may be unrelevant to this local machine. This argument is less
             * strong for Runtime but still somewhat applicable.
             */
            if (executor instanceof LocalExecutor) {
                if (cause instanceof Error) {
                    throw (Error) cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            }
            /*
             * Unwrap IOException no matter if the task was run on the local or on a remote machine,
             * because our current design implies that every machine see the same files (typically
             * on a shared file system like NFS), so an error that occured on a remote machine
             * would in principle be applicable to the local machine as well.
             */
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            /*
             * Other kind of errors.
             */
            final String  message = exception.getLocalizedMessage();
            if (cause instanceof Exception) {
                throw new ServerException(message, (Exception) cause);
            } else if (cause instanceof Error) {
                throw new ServerError(message, (Error) cause);
            } else {
                throw new ServerException(message, exception);
            }
        } catch (InterruptedException exception) {
            final InterruptedIOException e = new InterruptedIOException(exception.getLocalizedMessage());
            e.initCause(exception);
            throw e;
        }
    }
}
