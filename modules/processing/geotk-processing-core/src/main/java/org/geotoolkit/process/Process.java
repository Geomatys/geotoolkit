/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import org.opengis.parameter.ParameterValueGroup;

/**
 * A process is an operation applied to some given datas to achieve whatever
 * analyze or model transformation. This interface is abstract enough to handle
 * more than GIS datas. anything can be used here for whatever purpose that can be
 * seen as a process.
 *
 * @author johann Sorel (Geomatys)
 * @module pending
 */
public interface Process extends Runnable {

    /**
     * Description of a process with it's input and output parameters.
     * @return ProcessDescriptor
     */
    ProcessDescriptor getDescriptor();

    /**
     * Input values of the process. Thoses are described in the
     * process descriptor.
     * Those must be set before calling the run method.
     * @param parameter, must not be null.
     */
    void setInput(ParameterValueGroup parameter);

    /**
     * Output values of the process. Thoses are described in the
     * process descriptor.
     * Those can be aquiered after the monitor has been informed of the end
     * of the process.
     * @param parameter, must not be null.
     */
    ParameterValueGroup getOutput();

    /**
     * Set the monitor. The process progression will be send
     * to it.
     * @param monitor ProcessMonitor
     */
    void setMonitor(ProcessMonitor monitor);

    /**
     * Get the current monitor.
     * @return ProcessMonitor
     */
    ProcessMonitor getMonitor();

}
