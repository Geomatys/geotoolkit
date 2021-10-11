/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.outputs.complex;

import java.util.Map;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSDefaultConverter;
import org.geotoolkit.wps.xml.v200.Data;

/**
 *
 * @author Quentin Boileau (Geometry).
 */
public abstract class AbstractComplexOutputConverter<S> extends WPSDefaultConverter<S, Data> {


    @Override
    public abstract Class<S> getSourceClass();

    @Override
    public Class<Data> getTargetClass() {
        return Data.class;
    }

    /**
     * Convert the data from source Map into {@link Data}.
     * The {@code params} Map contain :
     * <ul>
     *      <li>outData : the object to convert into {@link Data}.</li>
     *      <li>outMime : the requested mime type for the output.</li>
     *      <li>outEncoding : the requested encoding for the output</li>
     *      <li>outSchema : the schema of the complex output</li>
     *      <li>outTempDirectoryPath : the absolute path to the output storage like schemas.</li>
     *      <li>outTempDirectoryUrl : the URL path to the web accessible storage folder.</li>
     * </ul>
     * @param source
     * @param params
     * @return the converted outData into {@link Data}.
     * @throws UnconvertibleObjectException if an error occurs durring the convertion processing.
     */
    @Override
    public abstract Data convert(final S source, final Map<String, Object> params) throws UnconvertibleObjectException;

}
