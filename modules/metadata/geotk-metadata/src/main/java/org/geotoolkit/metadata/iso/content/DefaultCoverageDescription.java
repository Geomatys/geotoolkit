/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.content;

import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.content.CoverageContentType;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.RangeElementDescription;
import org.opengis.util.RecordType;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.xml.Namespaces;


/**
 * Information about the content of a grid data cell.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.07
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "MD_CoverageDescription_Type", propOrder={
    "attributeDescription",
    "contentType",
    "dimensions",
    "rangeElementDescriptions"
})
@XmlRootElement(name = "MD_CoverageDescription")
@XmlSeeAlso({
    DefaultImageDescription.class,
    org.geotoolkit.internal.jaxb.gmi.MI_CoverageDescription.class
})
public class DefaultCoverageDescription extends AbstractContentInformation implements CoverageDescription {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5943716957630930520L;

    /**
     * Description of the attribute described by the measurement value.
     */
    private RecordType attributeDescription;

    /**
     * Type of information represented by the cell value.
     */
    private CoverageContentType contentType;

    /**
     * Information on the dimensions of the cell measurement value.
     */
    private Collection<RangeDimension> dimensions;

    /**
     * Provides the description of the specific range elements of a coverage.
     */
    private Collection<RangeElementDescription> rangeElementDescriptions;

    /**
     * Constructs an empty coverage description.
     */
    public DefaultCoverageDescription() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     * @since 2.4
     */
    public DefaultCoverageDescription(final CoverageDescription source) {
        super(source);
    }

    /**
     * Returns the description of the attribute described by the measurement value.
     */
    @Override
    @XmlElement(name = "attributeDescription", required = true)
    public synchronized RecordType getAttributeDescription() {
        return attributeDescription;
    }

    /**
     * Sets the description of the attribute described by the measurement value.
     *
     * @param newValue The new attribute description.
     */
    public synchronized void setAttributeDescription(final RecordType newValue) {
        checkWritePermission();
        attributeDescription = newValue;
    }

    /**
     * Returns the type of information represented by the cell value.
     */
    @Override
    @XmlElement(name = "contentType", required = true)
    public synchronized CoverageContentType getContentType() {
        return contentType;
    }

    /**
     * Sets the type of information represented by the cell value.
     *
     * @param newValue The new content type.
     */
    public synchronized void setContentType(final CoverageContentType newValue) {
        checkWritePermission();
        contentType = newValue;
    }

    /**
     * Returns the information on the dimensions of the cell measurement value.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "dimension")
    public synchronized Collection<RangeDimension> getDimensions() {
        return xmlOptional(dimensions = nonNullCollection(dimensions, RangeDimension.class));
    }

    /**
     * Sets the information on the dimensions of the cell measurement value.
     *
     * @param newValues The new dimensions.
     *
     * @since 2.4
     */
    public synchronized void setDimensions(final Collection<? extends RangeDimension> newValues) {
        dimensions = copyCollection(newValues, dimensions, RangeDimension.class);
    }

    /**
     * Provides the description of the specific range elements of a coverage.
     *
     * @return Description of the specific range elements of a coverage.
     *
     * @since 3.03
     */
    @Override
    @XmlElement(name = "rangeElementDescription", namespace = Namespaces.GMI)
    public synchronized Collection<RangeElementDescription> getRangeElementDescriptions() {
        return xmlOptional(rangeElementDescriptions =
                nonNullCollection(rangeElementDescriptions, RangeElementDescription.class));
    }

    /**
     * Sets the description of the specific range elements of a coverage.
     *
     * @param newValues The new range element description.
     *
     * @since 3.03
     */
    public synchronized void setRangeElementDescriptions(
            final Collection<? extends RangeElementDescription> newValues)
    {
        rangeElementDescriptions = copyCollection(newValues, rangeElementDescriptions,
                RangeElementDescription.class);
    }
}
