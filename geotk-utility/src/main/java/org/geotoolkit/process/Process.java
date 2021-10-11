/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.process;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.parameter.ParameterValueGroup;


/**
 * An operation applied to some given data to achieve whatever analyze or model transformation.
 * {@code Process} instances are created by {@link ProcessDescriptor} and can be executed in an
 * {@link ExecutorService}.
 *
 * {@section Event notifications}
 * All {@code Process} implementations <strong>must</strong> notify all their {@link ProcessListener}s
 * when the {@link #call()} method is starting and when it is terminating, either successfully or on
 * failure.
 *
 * @author Johann Sorel (Geomatys)
 * @version 4.0
 *
 * @see ExecutorService
 *
 * @since 3.19
 * @module
 */
public interface Process extends Callable<ParameterValueGroup> {
    /**
     * Description of the process algorithm and its input/output parameters.
     * The returned value is the descriptor that created this {@code Process} instance.
     *
     * @return The process descriptor.
     */
    ProcessDescriptor getDescriptor();

    /**
     * Returns the input values given to the {@link ProcessDescriptor#createProcess(ParameterValueGroup)} method.
     * The {@linkplain ParameterValueGroup#getDescriptor() descriptor} of those parameters is the instance returned by
     * <code>{@linkplain #getDescriptor()}.{@linkplain ProcessDescriptor#getInputDescriptor() getInputDescriptor()}</code>.
     *
     * @return The input parameter values (never {@code null}).
     *
     * @see ProcessDescriptor#getInputDescriptor()
     * @see ProcessStep#getSources()
     */
    ParameterValueGroup getInput();

    /**
     * Executes the process and returns the output in a new {@link ParameterValueGroup}.
     *
     * {@note Returning a parameter object may sound strange, since parameters are usually for
     *        input values rather than output values. Note however that ISO 19115 do the same,
     *        since the <code>ProcessStep</code> outputs is a collection of <code>Source</code>
     *        objects. In both cases, the outputs may be used as inputs in the next step of a
     *        process chain.}
     *
     * The following relations shall hold:
     * <p>
     * <ul>
     *   <li>The {@linkplain ParameterValueGroup#getDescriptor() descriptor} of the returned parameters is the same instance than
     *   <code>{@linkplain #getDescriptor()}.{@linkplain ProcessDescriptor#getOutputDescriptor() getOutputDescriptor()}</code>.</li>
     *   <li>When the process is {@linkplain ProcessListener#completed completed}, the {@link ProcessEvent#getOutput()} value shall
     *       be the same than the return value of this {@code call()} method.</li>
     * </ul>
     *
     * {@section Event notifications}
     * For any {@linkplain #addListener registered listeners}, this method shall invoke the
     * following methods. Note that all notification events except {@code progressing} are
     * mandatory for all {@code Process} implementations.
     * <p>
     * <ul>
     *   <li>{@link ProcessListener#started(ProcessEvent) started} (<em>mandatory</em>) at the beginning of this {@code call()} method;</li>
     *   <li>{@link ProcessListener#progressing(ProcessEvent) progressing} (<em>optional</em>) during the process execution;</li>
     *   <li>When this {@code call()} method is about to exit, exactly <strong>one</strong> of the following:<ul>
     *     <li>{@link ProcessListener#completed(ProcessEvent) completed} on success, or</li>
     *     <li>{@link ProcessListener#failed(ProcessEvent) failed} if an error occurred.</li></ul>
     *   </li>
     * </ul>
     *
     * @return The computation results as an parameter value groups.
     * @throws ProcessException if the process failed.
     *
     * @see ProcessDescriptor#getOutputDescriptor()
     * @see ProcessStep#getOutputs()
     */
    @Override
    ParameterValueGroup call() throws ProcessException;

    /**
     * Returns a description of the process, the geographic inputs and outputs and other metadata.
     * Those metadata are suitable to processes on geographic data and may not be applicable to every kind of processes.
     * If this parameter is provided, then:
     * <p>
     * <ul>
     *   <li>{@link ProcessStep#getDate()} is the execution date and time of the process.</li>
     *   <li>{@link ProcessStep#getSources()} are the geographic {@linkplain Process#getInput() process inputs}.</li>
     *   <li>{@link ProcessStep#getOutputs()} are the geographic {@linkplain Process#call() process outputs}.</li>
     *   <li>{@link ProcessStep#getProcessingInformation()} is the {@linkplain Process#getDescriptor() process descriptor}.</li>
     * </ul>
     *
     * @return A description of the process, the geographic inputs and outputs and other metadata.
     *
     * @since 4.0
     */
    ProcessStep getMetadata();

    /**
     * Adds a listener to the list of objects to inform about the process progress.
     *
     * @param listener The listener to add.
     */
    void addListener(ProcessListener listener);

    /**
     * Removes a listener from the list of objects to inform about the process progress.
     *
     * @param listener The listener to remove.
     */
    void removeListener(ProcessListener listener);

    /**
     * Returns all registered listeners, or an empty array if none.
     *
     * @return The registered listeners.
     */
    ProcessListener[] getListeners();
}
