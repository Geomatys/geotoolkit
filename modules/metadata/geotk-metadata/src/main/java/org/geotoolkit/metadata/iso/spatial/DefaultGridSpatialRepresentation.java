/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.metadata.iso.spatial;

import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.Georeferenceable;
import org.opengis.metadata.spatial.GridSpatialRepresentation;

import org.geotoolkit.lang.ValueRange;


/**
 * Basic information required to uniquely identify a resource or resources.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(name = "MD_GridSpatialRepresentation_Type", propOrder={
    "numberOfDimensions",
    "axisDimensionProperties",
    "cellGeometry",
    "transformationParameterAvailable"
})
@XmlSeeAlso({
    DefaultGeorectified.class,
    DefaultGeoreferenceable.class
})
@XmlRootElement(name = "MD_GridSpatialRepresentation")
public class DefaultGridSpatialRepresentation extends AbstractSpatialRepresentation
        implements GridSpatialRepresentation
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8400572307442433979L;

    /**
     * Number of independent spatial-temporal axes.
     */
    private Integer numberOfDimensions;

    /**
     * Information about spatial-temporal axis properties.
     */
    private List<Dimension> axisDimensionProperties;

    /**
     * Identification of grid data as point or cell.
     */
    private CellGeometry cellGeometry;

    /**
     * Indication of whether or not parameters for transformation exists.
     */
    private boolean transformationParameterAvailable;

    /**
     * Constructs an initially empty grid spatial representation.
     */
    public DefaultGridSpatialRepresentation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source
     * @since 2.4
     */
    public DefaultGridSpatialRepresentation(final GridSpatialRepresentation source) {
        super(source);
    }

    /**
     * Creates a grid spatial representation initialized to the given values.
     * This is a convenience constructor. The argument types don't need to
     * match exactly the types expected by getters and setters.
     *
     * @param numberOfDimensions The number of independent spatial-temporal axes.
     * @param axisDimensionsProperties Information about spatial-temporal axis properties.
     * @param cellGeometry Identification of grid data as point or cell.
     * @param transformationParameterAvailable Indication of whether or not parameters for
     *          transformation exists.
     *
     * @deprecated This constructor is somewhat arbitrary as it does not include all attributes
     *             (especially because of revisions in new ISO standards), not so convenient and
     *             not used in practice.
     */
    @Deprecated
    public DefaultGridSpatialRepresentation(final int numberOfDimensions,
                                            final List<? extends Dimension> axisDimensionsProperties,
                                            final CellGeometry cellGeometry,
                                            final boolean transformationParameterAvailable)
    {
        setNumberOfDimensions(numberOfDimensions);
        setAxisDimensionProperties(axisDimensionsProperties);
        setCellGeometry(cellGeometry);
        setTransformationParameterAvailable(transformationParameterAvailable);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     * <p>
     * This method checks for the {@link Georectified} and {@link Georeferenceable}
     * sub-interfaces. If one of those interfaces is found, then this method delegates to
     * the corresponding {@code castOrCopy} static method. If the given object implements more
     * than one of the above-cited interfaces, then the {@code castOrCopy} method to be used is
     * unspecified.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultGridSpatialRepresentation castOrCopy(final GridSpatialRepresentation object) {
        if (object instanceof Georectified) {
            return DefaultGeorectified.castOrCopy((Georectified) object);
        }
        if (object instanceof Georeferenceable) {
            return DefaultGeoreferenceable.castOrCopy((Georeferenceable) object);
        }
        return (object == null) || (object instanceof DefaultGridSpatialRepresentation)
                ? (DefaultGridSpatialRepresentation) object : new DefaultGridSpatialRepresentation(object);
    }

    /**
     * Returns the number of independent spatial-temporal axes.
     */
    @Override
    @ValueRange(minimum=0)
    @XmlElement(name = "numberOfDimensions", required = true)
    public synchronized Integer getNumberOfDimensions() {
        return numberOfDimensions;
    }

    /**
     * Sets the number of independent spatial-temporal axes.
     *
     * @param newValue The new number of dimension.
     */
    public synchronized void setNumberOfDimensions(final Integer newValue) {
        checkWritePermission();
        numberOfDimensions = newValue;
    }

    /**
     * Returns information about spatial-temporal axis properties.
     */
    @Override
    @XmlElement(name = "axisDimensionProperties", required = true)
    public synchronized List<Dimension> getAxisDimensionProperties() {
        return axisDimensionProperties = nonNullList(axisDimensionProperties, Dimension.class);
    }

    /**
     * Sets the information about spatial-temporal axis properties.
     *
     * @param newValues The new axis dimension properties.
     */
    public synchronized void setAxisDimensionProperties(final List<? extends Dimension> newValues) {
        checkWritePermission();
        axisDimensionProperties = (List<Dimension>)
                copyCollection(newValues, axisDimensionProperties, Dimension.class);
    }

    /**
     * Returns the identification of grid data as point or cell.
     */
    @Override
    @XmlElement(name = "cellGeometry", required = true)
    public synchronized CellGeometry getCellGeometry() {
        return cellGeometry;
    }

    /**
     * Sets identification of grid data as point or cell.
     *
     * @param newValue The new cell geometry.
     */
    public synchronized void setCellGeometry(final CellGeometry newValue) {
        checkWritePermission();
        cellGeometry = newValue;
    }

    /**
     * Returns indication of whether or not parameters for transformation exists.
     */
    @Override
    @XmlElement(name = "transformationParameterAvailability", required = true)
    public synchronized boolean isTransformationParameterAvailable() {
        return transformationParameterAvailable;
    }

    /**
     * Sets indication of whether or not parameters for transformation exists.
     *
     * @param newValue {@code true} if the transformation parameters are available.
     */
    public synchronized void setTransformationParameterAvailable(final boolean newValue) {
        checkWritePermission();
        transformationParameterAvailable = newValue;
    }
}
