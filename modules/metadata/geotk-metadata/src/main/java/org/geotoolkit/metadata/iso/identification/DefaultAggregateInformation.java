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
package org.geotoolkit.metadata.iso.identification;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.AggregateInformation;
import org.opengis.metadata.identification.AssociationType;
import org.opengis.metadata.identification.InitiativeType;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Aggregate dataset information.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MD_AggregateInformation_Type", propOrder={
    "aggregateDataSetName", "aggregateDataSetIdentifier", "associationType", "initiativeType"
})
@XmlRootElement(name = "MD_AggregateInformation")
public class DefaultAggregateInformation extends MetadataEntity implements AggregateInformation {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 5520234916010871192L;

    /**
     * Citation information about the aggregate dataset.
     */
    private Citation aggregateDataSetName;

    /**
     * Identification information about aggregate dataset.
     */
    private Identifier aggregateDataSetIdentifier;

    /**
     * Association type of the aggregate dataset.
     */
    private  AssociationType associationType;

    /**
     * Type of initiative under which the aggregate dataset was produced.
     */
    private InitiativeType initiativeType;

    /**
     * Constructs an initially empty Aggregate dataset information.
     */
    public DefaultAggregateInformation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultAggregateInformation(final AggregateInformation source) {
        super(source);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultAggregateInformation castOrCopy(final AggregateInformation object) {
        return (object == null) || (object instanceof DefaultAggregateInformation)
                ? (DefaultAggregateInformation) object : new DefaultAggregateInformation(object);
    }

    /**
     * Citation information about the aggregate dataset.
     *
     * @return Citation information about the aggregate dataset, or {@code null}.
     */
    @Override
    @XmlElement(name = "aggregateDataSetName")
    public synchronized Citation getAggregateDataSetName() {
        return aggregateDataSetName;
    }

    /**
     * Sets the citation information about the aggregate dataset.
     *
     * @param newValue The new citation.
     */
    public synchronized void setAggregateDataSetName(final Citation newValue) {
        checkWritePermission();
        aggregateDataSetName = newValue;
    }

    /**
     * Identification information about aggregate dataset.
     *
     * @return Identification information about aggregate dataset, or {@code null}.
     */
    @Override
    @XmlElement(name = "aggregateDataSetIdentifier")
    public synchronized Identifier getAggregateDataSetIdentifier() {
        return aggregateDataSetIdentifier;
    }

    /**
     * Sets the identification information about aggregate dataset.
     *
     * @param newValue The new identifier.
     */
    public synchronized void setAggregateDataSetIdentifier(final Identifier newValue) {
        aggregateDataSetIdentifier = newValue;
    }

    /**
     * Association type of the aggregate dataset.
     *
     * @return Association type of the aggregate dataset.
     */
    @Override
    @XmlElement(name = "associationType", required = true)
    public synchronized AssociationType getAssociationType() {
        return associationType;
    }

    /**
     * Sets the association type of the aggregate dataset.
     *
     * @param newValue The new association type.
     */
    public synchronized void setAssociationType(final AssociationType newValue) {
        associationType = newValue;
    }

    /**
     * Type of initiative under which the aggregate dataset was produced.
     *
     * @return Type of initiative under which the aggregate dataset was produced, or {@code null}.
     */
    @Override
    @XmlElement(name = "initiativeType")
    public synchronized InitiativeType getInitiativeType() {
        return initiativeType;
    }

    /**
     * Sets the type of initiative under which the aggregate dataset was produced.
     *
     * @param newValue The new initiative.
     */
    public synchronized void setInitiativeType(final InitiativeType newValue) {
        initiativeType = newValue;
    }
}
