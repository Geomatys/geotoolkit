/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.gmi;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import org.opengis.metadata.content.CoverageDescription;
import org.geotoolkit.metadata.iso.content.DefaultCoverageDescription;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * A wrapper for a metadata using the {@code "gmi"} namespace.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.17
 * @module
 */
@XmlType(name = "MI_CoverageDescription_Type")
@XmlRootElement(name = "MI_CoverageDescription")
public class MI_CoverageDescription extends DefaultCoverageDescription {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8679911383066193615L;

    /**
     * Creates an initially empty metadata.
     * This is also the default constructor used by JAXB.
     */
    public MI_CoverageDescription() {
    }

    /**
     * Creates a new metadata as a copy of the given one.
     * This is a shallow copy constructor.
     *
     * @param original The original metadata to copy.
     */
    public MI_CoverageDescription(final CoverageDescription original) {
        super(original);
    }

    /**
     * Wraps the given metadata into a Geotk implementation that can be marshalled,
     * using the {@code "gmi"} namespace if necessary.
     *
     * @param  original The original metadata provided by the user.
     * @return The metadata to marshall.
     */
    public static DefaultCoverageDescription castOrCopy(final CoverageDescription original) {
        if (original != null && !(original instanceof MI_CoverageDescription)) {
            if (!isNullOrEmpty(original.getRangeElementDescriptions())) {
                return new MI_CoverageDescription(original);
            }
        }
        return DefaultCoverageDescription.castOrCopy(original);
    }
}
