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

package org.geotoolkit.feature.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import org.opengis.feature.type.FeatureType;

/**
 *  An interface for feature type XML parsing.
 *
 * @module pending
 * @author Guilhem Legal (Geomatys)
 */
public interface XmlFeatureTypeReader {

    /**
     * Read a list feature type from the specified String XML (XSD) representation.
     *
     * @param xml An Xml representation of the feature types.
     *
     * @return A Feature type or {@code null}
     */
    public List<FeatureType> read(String xml);

    /**
     * Read a list feature type from the specified XML (XSD) stream.
     *
     * @param xml An Xml representation of the feature type.
     *
     * @return A Feature type or {@code null}
     */
    public List<FeatureType> read(InputStream in);

    /**
     * Read a feature type from the specified XML  (XSD) reader.
     *
     * @param xml An Xml representation of the feature type.
     *
     * @return A Feature type or {@code null}
     */
    public List<FeatureType> read(Reader reader);

    /**
     * Read a feature type from the specified XML  (XSD) reader.
     *
     * @param xml An Xml representation of the feature type.
     *
     * @return A Feature type or {@code null}
     */
    public FeatureType read(String xml, String name);

    /**
     * Read a feature type from the specified XML (XSD) stream.
     *
     * @param xml An Xml representation of the feature type.
     *
     * @return A Feature type or {@code null}
     */
    public FeatureType read(InputStream in, String name);

    /**
     * Read a feature type from the specified XML  (XSD) reader.
     *
     * @param xml An Xml representation of the feature type.
     *
     * @return A Feature type or {@code null}
     */
    public FeatureType read(Reader reader, String name);
}
