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

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.ObjectStream;

import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * A task which can be given to many processes. The same {@code ShareableTask} instance
 * (or a copy of it through serialization-deserialization) can be given to an arbitrary
 * amount of processes, either on the local machine or on many remote machines. It is the
 * task responsibility to ensure that each process can pickup a unique portion of the task.
 * <p>
 * A convenient approach is to extract sub-portions of the task from a thread-safe, remotely
 * available object stream (typically backed by an iterator). The stream or iterator is given
 * to the constructor. In case of serialization, the object stream is exported as a remote
 * object for Remote Method Invocation (RMI).
 *
 * @param <Input>  The value given by the stream used as input to the task.
 * @param <Output> The return value of the task.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @see Callable
 *
 * @since 3.00
 * @module
 */
public abstract class ShareableTask<Input,Output> implements Callable<Output>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7371908706836123493L;

    /**
     * The stream of input objects, initially for use on the local machine only. If this task is
     * serialized, then this stream will be replaced by an RMI object connected to the machine
     * that initially created the task.
     */
    private ObjectStream<Input> stream;

    /**
     * Creates a new task.
     *
     * @param input The input values, or {@code null} if none.
     */
    protected ShareableTask(final Iterable<Input> input) {
        if (input != null) {
            stream = new IteratorWrapper<>(input.iterator());
        }
    }

    /**
     * Returns the stream of input objects, or {@code null} if none. If this task is run
     * on the machine that created it, then the stream is backed by the collection given
     * at construction time. If this task is run on a distant machine, then this is a stub
     * fetching the objects from the original machine using <cite>Remote Method Invocation</cite>
     * (RMI).
     *
     * @return The stream of input objects, or {@code null} if none.
     */
    protected final synchronized ObjectStream<Input> inputs() {
        return stream;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     * This method will typically be invoked on the remote machine,
     * so the result should be serializable.
     *
     * @return The computed result
     * @throws Exception if unable to compute a result.
     */
    @Override
    public abstract Output call() throws Exception;

    /**
     * Invoked for aggregating the results after the execution is finished. If the task has
     * been divised in many sub-tasks, then the collection may contain more than one element.
     * It is subclass responsibility to aggregate sub-tasks result in a single result.
     *
     * {@section Default implementation}
     * If the given collection contains at most one non-null element, than that element is
     * returned. Otherwise an exception is thrown. This default implementation is suitable
     * when the task do not return any result, otherwise subclasses should override this
     * method.
     *
     * @param  outputs The outputs from every sub-tasks.
     * @return The aggregated result.
     */
    public Output aggregate(final Collection<Output> outputs) {
        Output output = null;
        for (final Output candidate : outputs) {
            if (candidate != null) {
                if (output == null) {
                    output = candidate;
                } else {
                    throw new UnsupportedOperationException(
                            "Needs an implementation of ShareableTask.aggregate(Collection<Output>).");
                }
            }
        }
        return output;
    }

    /**
     * Convenience method which returns a map containing all elements of all given maps.
     * This is used for implementation of {@link #aggregate(Collection)} by subclasses.
     *
     * @param  <K> The type of keys in the map.
     * @param  <V> The type of values in the map.
     * @param  outputs The collection of maps to aggregate.
     * @return The aggregated map.
     * @throws IllegalArgumentException If at least one key is defined in more than one map.
     */
    protected static <K,V> Map<K,V> aggregateMap(final Collection<Map<K,V>> outputs)
            throws IllegalArgumentException
    {
        int size = 0;
        for (final Map<K,V> output : outputs) {
            size += output.size();
        }
        final Map<K,V> aggregate = new HashMap<>(hashMapCapacity(size));
        for (final Map<K,V> output : outputs) {
            aggregate.putAll(output);
        }
        size -= aggregate.size();
        if (size != 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.DUPLICATED_VALUES_COUNT_1, size));
        }
        return aggregate;
    }

    /**
     * Invoked in case of failures for deleting the files that the task may have created.
     * The default implementation does nothing.
     */
    public void rollback() {
    }

    /**
     * Invoked before serialization. This method replace the stream instance by a serializable
     * one if it was not already serializable.
     */
    private synchronized void writeObject(final ObjectOutputStream out) throws IOException {
        final ObjectStream<Input> stream = this.stream;
        if (stream != null && !(stream instanceof Serializable)) {
            this.stream = new RemoteStream<>(stream);
        }
        out.defaultWriteObject();
    }
}
