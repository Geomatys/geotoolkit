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

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.ExecutionException;
import java.util.Objects;

import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.resources.Errors;


/**
 * Static methods related to RMI usage in Geotk.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class RMI extends Static {
    /**
     * The name of a master node where to delegate the tasks, or {@code null} if none.
     * If null (which is the default), then all tasks will be run locally.
     */
    private static String master;

    /**
     * The executor. Will be created only when first needed, in order to give a chance to the
     * caller to configure his system first, and to propagate the {@link RemoteException} to
     * the caller in case of failure to locate the RMI registry.
     */
    private static TaskExecutor executor;

    /**
     * The temporary directory visible to all nodes, or {@code null} if none.
     */
    private static volatile File sharedTemporaryDirectory;

    /**
     * Do not allow instantiation of this class.
     */
    private RMI() {
    }

    /**
     * Sets the name of a master node where to delegate the tasks, or {@code null} if none.
     * If null (which is the default), then all tasks will be run locally.
     * <p>
     * If a task is already running on the current master, then its execution will be completed
     * normally on its current master and this method will applies only to the next tasks to be
     * submitted.
     *
     * @param master The name of a master node, or {@code null}.
     */
    @Configuration
    public static synchronized void setMaster(final String master) {
        if (!Objects.equals(master, RMI.master)) {
            RMI.master = master;
            executor = null;
        }
    }

    /**
     * Sets a temporary directory visible to all nodes.
     * By default there is no such shared directory.
     *
     * @param directory The new shared temporary directory, or {@code null} if none.
     * @throws IllegalArgumentException if the argument is not a valid directory.
     */
    @Configuration
    public static void setSharedTemporaryDirectory(final File directory) {
        if (directory != null && !directory.isDirectory()) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.NOT_A_DIRECTORY_1, directory));
        }
        sharedTemporaryDirectory = directory;
    }

    /**
     * Returns the temporary directory, preferably visible to every nodes. By default this
     * method returns the value of {@code System.getProperty("java.io.tmpdir")}, which is usually
     * <strong>not</strong> a directory shared by every nodes. If a shared directory exists, it
     * should have been declared by a call to {@link #setSharedTemporaryDirectory(File)}
     * before the call to this method.
     *
     * @return The temporary directory to use.
     */
    public static File getSharedTemporaryDirectory() {
        File directory = sharedTemporaryDirectory;
        if (directory == null) {
            directory = new File(System.getProperty("java.io.tmpdir", "/tmp"), "Geotoolkit.org");
            if (!directory.isDirectory()) {
                if (!directory.mkdir()) {
                    // If we can't create the Geotoolkit subdirectory,
                    // stay in the usual tmp directory.
                    directory = directory.getParentFile();
                }
            }
        }
        return directory;
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
                if (master == null) {
                    executor = new LocalExecutor(false);
                } else try {
                    executor = (TaskExecutor) LocateRegistry.getRegistry(master).lookup(RemoteExecutor.NAME);
                } catch (NotBoundException exception) {
                    RemoteService.logger().warning(exception.toString());
                    executor = new LocalExecutor(false);
                }
                RMI.executor = executor;
            }
        }
        final TaskFuture<Output> result = executor.submit(task);
        boolean success = false;
        try {
            final Output output = result.get();
            success = true;
            return output;
        } catch (ExecutionException exception) {
            final Throwable cause = exception.getCause();
            /*
             * Unwrap IOException only if it was executed on the same thread,
             * in order to make the stack trace shorter.
             */
            if (result instanceof LocalFuture<?> && !((LocalFuture<?>) result).isThreaded()) {
                if (cause instanceof IOException) {
                    throw (IOException) cause;
                }
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
        } finally {
            if (!success) {
                result.rollback();
            }
        }
    }
}
