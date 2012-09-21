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

import org.opengis.metadata.Identifier;
import org.opengis.metadata.lineage.Processing;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;

import org.geotoolkit.internal.simple.SimpleParameterDescriptor;


/**
 * Description of a {@linkplain Process process} algorithm and its input/output parameters.
 * This interface extends the ISO 19115 {@link Processing} interface, which provide human-readable
 * information about the process. In addition to the ISO/OGC interface, {@code ProcessDescriptor}
 * provides:
 * <p>
 * <ul>
 *   <li>A more machine-usable description of the input and output parameters
 *       (including source data and output data).</li>
 *   <li>A method for creating new {@link Process} instances which can be run in
 *       an {@link ExecutorService}.</li>
 * </ul>
 * <p>
 * {@code ProcessDescriptor} instances are provided by {@link ProcessingRegistry}.
 * See the {@linkplain org.geotoolkit.process package javadoc} for usage example.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @version 3.20
 *
 * @since 3.19
 * @module
 */
public interface ProcessDescriptor extends Processing {
    /**
     * An optional parameter which should be part of the {@linkplain #getOutputDescriptor() output
     * descriptor}. If presents, this parameter shall contains a description of the process, the
     * geographic inputs and outputs and other metadata. This parameter is suitable to processes
     * on geographic data and may not be applicable to every kind of processes.
     * <p>
     * If this parameter is provided, then:
     * <p>
     * <ul>
     *   <li>{@link ProcessStep#getDate()} is the execution date and time of the process.</li>
     *   <li>{@link ProcessStep#getSources()} are the geographic {@linkplain Process#getInput() process inputs}.</li>
     *   <li>{@link ProcessStep#getOutputs()} are the geographic {@linkplain Process#call() process outputs}.</li>
     *   <li>{@link ProcessStep#getProcessingInformation()} is the {@linkplain Process#getDescriptor() process descriptor}.</li>
     * </ul>
     */
    ParameterDescriptor<ProcessStep> PROCESS_STEP = new SimpleParameterDescriptor<>(ProcessStep.class, "Geotk", "ProcessStep");

    /**
     * Information to identify the processing package that run the process.
     * The identifier {@linkplain Identifier#getAuthority() authority} shall matches the
     * {@linkplain ProcessingRegistry#getIdentification() registry identification} citation.
     *
     * @return Identifier of the processing package that run the process.
     */
    @Override
    Identifier getIdentifier();

    /**
     * Process name to display in user interfaces.
     * This name is not intended to identify the process;
     * identification shall use the process {@linkplain #getIdentifier() identifier} instead.
     *
     * @return The processing name to display in user interfaces, or {@code null} if none.
     *
     * @since 3.20
     */
    InternationalString getDisplayName();

    /**
     * Additional details about the processing procedures.
     *
     * @return The processing procedures, or {@code null} if none.
     */
    @Override
    InternationalString getProcedureDescription();

    /**
     * Returns a description of the input parameters. The {@linkplain ParameterValueGroup parameter
     * values} include both the source data (for example the source images) and the parameters that
     * configure the operation applied on those data.
     * <p>
     * This is the descriptor of the parameter values returned by {@link Process#getInput()}.
     *
     * @return Description of the input parameters.
     *
     * @see Process#getInput()
     * @see ProcessStep#getSources()
     */
    ParameterDescriptorGroup getInputDescriptor();

    /**
     * Returns a description of the output parameters.
     * This is the descriptor of the parameter values returned by {@link Process#call()}.
     * Those {@linkplain ParameterValueGroup parameter values} include output data
     * (for example the output images) and optionally some metadata related to those outputs
     * (for example statistics).
     * <p>
     * While not mandatory, it is recommended that the returned descriptor contains the
     * {@link #PROCESS_STEP} parameter.
     *
     * @return Description of the output parameters.
     *
     * @see Process#call()
     * @see ProcessStep#getOutputs()
     */
    ParameterDescriptorGroup getOutputDescriptor();

    /**
     * Creates a new process initialized with the given input parameter values. The returned
     * process is not yet started. To start the process, callers must give the process to an
     * {@link ExecutorService} or invoke {@link Process#call()} explicitely.
     * <p>
     * The input parameters are typically created by calls to
     * <code>{@linkplain #getInputDescriptor()}.{@linkplain ParameterDescriptorGroup#createValue()
     * createValue()}</code>. Then the parameter values shall be set before to be given to this
     * {@code createProcess} method.
     *
     * @param  input The input parameters.
     * @return A new un-started process which will use the given input parameter values.
     *
     * @see ExecutorService#submit(Callable)
     */
    Process createProcess(ParameterValueGroup input);
}
