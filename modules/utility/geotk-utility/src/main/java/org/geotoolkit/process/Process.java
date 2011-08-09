/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
 * @author Johann Sorel (Geomatys)
 * @version 3.19
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
     * Returns the input values given to the {@link ProcessDescriptor#createProcess(ParameterValueGroup)}
     * method. The {@linkplain ParameterValueGroup#getDescriptor() descriptor} of those parameters is the
     * instance returned by {@link ProcessDescriptor#getInputDescriptor()}.
     *
     * @return The input parameter values (never {@code null}).
     *
     * @see ProcessDescriptor#getInputDescriptor()
     * @see ProcessStep#getSources()
     */
    ParameterValueGroup getInput();

    /**
     * Executes the process and returns the output in a new {@link ParameterValueGroup}. The
     * {@linkplain ParameterValueGroup#getDescriptor() descriptor} of those parameters is the
     * instance returned by {@link ProcessDescriptor#getOutputDescriptor()}.
     *
     * @return The computation results as an parameter value groups.
     * @throws ProcessException if the process failed.
     *
     * @see ProcessDescriptor#getOutputDescriptor()
     * @see ProcessStep#getOutputs()
     */
    @Override
    ProcessResult call() throws ProcessException;

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
