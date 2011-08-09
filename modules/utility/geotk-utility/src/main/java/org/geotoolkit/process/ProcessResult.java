/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.parameter.ParameterValueGroup;


/**
 * The result of a {@linkplain Process process}. The {@link ParameterValueGroup} interface
 * is used here as a container for multi-values. In addition, some pairs of processes may
 * be designed for allowing direct usage of this output parameters as input for an other
 * process in a chain of processes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public interface ProcessResult extends ParameterValueGroup {
    /**
     * Returns an optional description of the process, the geographic inputs and outputs
     * and other metadata. This method is suitable to processes on geographic data and
     * may not be applicable to every kind of processes.
     * <p>
     * If this information is provided, then:
     * <p>
     * <ul>
     *   <li>{@link ProcessStep#getDate()} is the execution date and time of the process.</li>
     *   <li>{@link ProcessStep#getSources()} are the geographic {@linkplain Process#getInput() process inputs}.</li>
     *   <li>{@link ProcessStep#getOutputs()} are the geographic process output.</li>
     *   <li>{@link ProcessStep#getProcessingInformation()} is the {@linkplain Process#getDescriptor() process descriptor}.</li>
     * </ul>
     *
     * @return A description of the geographic process result, or {@code null} if none.
     */
    ProcessStep getProcessStep();
}
