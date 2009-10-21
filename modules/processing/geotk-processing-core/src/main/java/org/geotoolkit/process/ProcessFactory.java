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

import org.opengis.metadata.identification.Identification;
import org.opengis.referencing.Factory;

/**
 * A process factory may provide several different process type.
 * Each process might be reusable or not depending on the implementation.
 *
 * @author johann Sorel (Geomatys)
 * @module pending
 */
public interface ProcessFactory extends Factory{

    /**
     * Hold the general information about this factory.
     * The Citation hold in this object shall match the authority
     * defined in all process descriptor identifier.
     */
    Identification getIdentification();

    /**
     * Return an array of all process descriptors available in this factory.
     * @return ProcessDescriptor[] never null but can be empty
     */
    ProcessDescriptor[] getProcessDescriptors();

    /**
     * Return the name of all available processes. This name is only available
     * within this factory.
     */
    String[] getProcessNames();

    /**
     * Create a process.
     *
     * @param name : process name
     * @return Process
     * @throws IllegalArgumentException if name is not part of this factory
     */
    Process createProcess(String name) throws IllegalArgumentException;

}
