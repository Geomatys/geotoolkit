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
     * @param parameter must not be null.
     */
    void setInput(ParameterValueGroup parameter);

    /**
     * Output values of the process. Thoses are described in the
     * process descriptor.
     * The output parameters can be accesed before the process is started,
     * this behavior allows process chains to be configured one after the other
     * before running them.
     * @param parameter must not be null.
     */
    ParameterValueGroup getOutput();

    /**
     * Add a listener. The process progression will be send to it.
     * @param listener ProcessListener
     */
    void addListener(ProcessListener listener);

    /**
     * Remove a listener.
     * @return ProcessListener
     */
    void removeListener(ProcessListener listener);

    /**
     * @return array of all ProcessListener
     */
    ProcessListener[] getListeners();
    
}
