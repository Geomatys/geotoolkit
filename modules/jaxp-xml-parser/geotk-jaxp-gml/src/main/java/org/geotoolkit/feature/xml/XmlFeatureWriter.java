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
import org.geotoolkit.storage.DataStoreException;

/**
 * A interface to serialize feature or featureCollection in XML.
 *
 * @module pending
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public interface XmlFeatureWriter {

    /**
     * Write an XML representation of the specified feature collection or feature
     * into the output.
     *
     * @param candidate Feature or FeatureCollection to write
     * @param output where to write the candidate
     */
    void write(Object candidate, Object output) throws IOException, XMLStreamException, DataStoreException;

    /**
     * Free the resources.
     */
    void dispose();
    
}
