/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.adaptor;

import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.InputType;
import org.geotoolkit.wps.xml.v100.OutputDataType;
import org.geotoolkit.wps.xml.v200.DataInputType;
import org.geotoolkit.wps.xml.v200.DataOutputType;

/**
 * Define a mapping between WPS complexe data type and java type.
 *
 * TODO : improve this API to replace old converter API.
 *
 * @author Johann Sorel (Geomatys)
 * @param <T> Java type
 */
public interface DataAdaptor<T> {

    /**
     * A key for {@link ExtendedParameterDescriptor} user data map. Specify the {WPSDataAdaptor} object.
     */
    public static final String USE_ADAPTOR = "adaptor";

    /**
     *
     * @return java class
     */
    Class<T> getValueClass();

    /**
     * Convert java object to WPS-1 input.
     *
     * @param candidate
     * @return
     */
    InputType toWPS1Input(T candidate) throws UnconvertibleObjectException;

    /**
     * Convert java object to WPS-2 input.
     *
     * @param candidate
     * @return
     */
    DataInputType toWPS2Input(T candidate) throws UnconvertibleObjectException;

    /**
     * Convert WPS-1 data to java.
     *
     * @param candidate
     * @return
     */
    T fromWPS1Input(OutputDataType candidate) throws UnconvertibleObjectException;

    /**
     * Convert WPS-2 data to java.
     *
     * @param candidate
     * @return
     */
    T fromWPS2Input(DataOutputType candidate) throws UnconvertibleObjectException;

}
