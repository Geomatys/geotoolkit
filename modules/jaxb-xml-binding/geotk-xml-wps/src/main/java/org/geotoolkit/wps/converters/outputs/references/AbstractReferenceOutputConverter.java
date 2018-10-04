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
package org.geotoolkit.wps.converters.outputs.references;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.converters.WPSDefaultConverter;
import org.geotoolkit.wps.xml.v200.Reference;
import org.geotoolkit.wps.xml.v200.ComplexData;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public abstract class AbstractReferenceOutputConverter<S> extends WPSDefaultConverter<S, Reference> {

    @Override
    public abstract Class<S> getSourceClass();

    @Override
    public Class<Reference> getTargetClass() {
        return Reference.class;
    }

    /**
     * Convert the data from source Map into {@link ComplexData}.
     * The {@code source} Map contain :
     * <ul>
     *      <li>outData : the object to convert into {@link ComplexData}.</li>
     *      <li>outMime : the requested mime type for the output.</li>
     *      <li>outEncoding : the requested encoding for the output</li>
     *      <li>outSchema : the schema of the complex output</li>
     *      <li>outTempDirectoryPath : the absolute path to the output storage like schemas.</li>
     *      <li>outTempDirectoryUrl : the URL path to the web accessible storage folder.</li>
     * </ul>
     * @param source
     * @return the converted outData into {@link ComplexData}.
     * @throws UnconvertibleObjectException if an error occurs durring the convertion processing.
     */
    @Override
    public abstract Reference convert(S source, Map<String, Object> params) throws UnconvertibleObjectException;

    protected void mapParameters(final Reference reference, Map<String, Object> params) {
        Object value = params.get(SCHEMA);
        if (value != null)
            reference.setSchema(value.toString());

        value = params.get(MIME);
        if (value != null)
            reference.setMimeType(value.toString());

        value = params.get(ENCODING);
        if (value != null)
            reference.setEncoding(value.toString());
    }

    protected Path buildPath(Map<String, Object> params, final String randomFileName) {
        final Path dir;
        final Object tmpDirValue = params.get(TMP_DIR_PATH);
        if (tmpDirValue instanceof String) {
            dir =  Paths.get((String) params.get(TMP_DIR_PATH));
        } else if (tmpDirValue instanceof URI) {
            dir =  Paths.get((URI) params.get(TMP_DIR_PATH));
        } else {
            throw new UnconvertibleObjectException("Unexpected type for " + TMP_DIR_PATH + " parameter.");
        }
        if (randomFileName == null) {
            return dir;
        }
        return dir.resolve(randomFileName);
    }
}
