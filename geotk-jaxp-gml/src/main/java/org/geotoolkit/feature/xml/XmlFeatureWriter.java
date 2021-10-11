/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.feature.xml;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.DataStoreException;

/**
 * A interface to serialize feature or featureCollection in XML.
 *
 * @module
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public interface XmlFeatureWriter extends Configurable {

    /**
     * Write an XML representation of the specified Feature, FeatureCollection or list of FeatureCollection to write
     * into the output.
     *
     * @param candidate Feature or FeatureCollection to write
     * @param output where to write the candidate
     */
    void write(final Object candidate, final Object output) throws IOException, XMLStreamException, DataStoreException;

    /**
     * Write an XML representation of the specified feature collection or feature
     * into the output.
     *
     * @param candidate Feature, FeatureCollection or list of FeatureCollection to write
     * @param output where to write the candidate
     * @param nbMatched total number of feature matching (feature collection can be limited)
     */
    void write(final Object candidate, final Object output, final Integer nbMatched) throws IOException, XMLStreamException, DataStoreException;

    /**
     * Free the resources.
     */
    void dispose() throws IOException, XMLStreamException;

}
