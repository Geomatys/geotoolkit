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
package org.geotoolkit.metadata.iso.lineage;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.lineage.NominalResolution;
import org.opengis.metadata.lineage.Source;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.metadata.identification.RepresentativeFraction;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.util.InternationalString;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.xml.Namespaces;


/**
 * Information about the source data used in creating the data specified by the scope.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "LI_Source_Type", propOrder={
    "description",
    "scaleDenominator",
    "sourceCitation",
    "sourceExtents",
    "sourceSteps",
    "processedLevel",
    "resolution"
})
@XmlRootElement(name = "LI_Source")
@XmlSeeAlso(org.geotoolkit.internal.jaxb.gmi.LE_Source.class)
public class DefaultSource extends MetadataEntity implements Source {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6277132009549470021L;

    /**
     * Detailed description of the level of the source data.
     */
    private InternationalString description;

    /**
     * Denominator of the representative fraction on a source map.
     */
    private RepresentativeFraction scaleDenominator;

    /**
     * Spatial reference system used by the source data.
     */
    private ReferenceSystem sourceReferenceSystem;

    /**
     * Recommended reference to be used for the source data.
     */
    private Citation sourceCitation;

    /**
     * Information about the spatial, vertical and temporal extent of the source data.
     */
    private Collection<Extent> sourceExtents;

    /**
     * Information about an event in the creation process for the source data.
     */
    private Collection<ProcessStep> sourceSteps;

    /**
     * Processing level of the source data.
     */
    private Identifier processedLevel;

    /**
     * Distance between consistent parts (centre, left side, right side) of two adjacent
     * pixels.
     */
    private NominalResolution resolution;

    /**
     * Creates an initially empty source.
     */
    public DefaultSource() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultSource(final Source source) {
        super(source);
    }

    /**
     * Creates a source initialized with the given description.
     *
     * @param description A detailed description of the level of the source data.
     */
    public DefaultSource(final InternationalString description) {
        setDescription(description);
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
    public static DefaultSource castOrCopy(final Source object) {
        return (object == null) || (object instanceof DefaultSource)
                ? (DefaultSource) object : new DefaultSource(object);
    }

    /**
     * Returns a detailed description of the level of the source data.
     */
    @Override
    @XmlElement(name = "description")
    public synchronized InternationalString getDescription() {
        return description;
    }

    /**
     * Sets a detailed description of the level of the source data.
     *
     * @param newValue The new description.
     */
    public synchronized void setDescription(final InternationalString newValue) {
        checkWritePermission();
        description = newValue;
    }

    /**
     * Returns the denominator of the representative fraction on a source map.
     */
    @Override
    @XmlElement(name = "scaleDenominator")
    public synchronized RepresentativeFraction getScaleDenominator()  {
        return scaleDenominator;
    }

    /**
     * Sets the denominator of the representative fraction on a source map.
     *
     * @param newValue The new scale denominator.
     *
     * @since 2.4
     */
    public synchronized void setScaleDenominator(final RepresentativeFraction newValue)  {
        checkWritePermission();
        scaleDenominator = newValue;
    }

    /**
     * Returns the spatial reference system used by the source data.
     *
     * @todo needs to annotate the referencing module before.
     */
    @Override
    public synchronized ReferenceSystem getSourceReferenceSystem()  {
        return sourceReferenceSystem;
    }

    /**
     * Sets the spatial reference system used by the source data.
     *
     * @param newValue The new reference system.
     */
    public synchronized void setSourceReferenceSystem(final ReferenceSystem newValue) {
        checkWritePermission();
        sourceReferenceSystem = newValue;
    }

    /**
     * Returns the recommended reference to be used for the source data.
     */
    @Override
    @XmlElement(name = "sourceCitation")
    public synchronized Citation getSourceCitation() {
        return sourceCitation;
    }

    /**
     * Sets the recommended reference to be used for the source data.
     *
     * @param newValue The new source citation.
     */
    public synchronized void setSourceCitation(final Citation newValue) {
        checkWritePermission();
        sourceCitation = newValue;
    }

    /**
     * Returns tiInformation about the spatial, vertical and temporal extent
     * of the source data.
     */
    @Override
    @XmlElement(name = "sourceExtent")
    public synchronized Collection<Extent> getSourceExtents()  {
        return sourceExtents = nonNullCollection(sourceExtents, Extent.class);
    }

    /**
     * Information about the spatial, vertical and temporal extent of the source data.
     *
     * @param newValues The new source extents.
     */
    public synchronized void setSourceExtents(final Collection<? extends Extent> newValues) {
        sourceExtents = copyCollection(newValues, sourceExtents, Extent.class);
    }

    /**
     * Returns information about an event in the creation process for the source data.
     */
    @Override
    @XmlElement(name = "sourceStep")
    public synchronized Collection<ProcessStep> getSourceSteps() {
        return sourceSteps = nonNullCollection(sourceSteps, ProcessStep.class);
    }

    /**
     * Sets information about an event in the creation process for the source data.
     *
     * @param newValues The new source steps.
     */
    public synchronized void setSourceSteps(final Collection<? extends ProcessStep> newValues) {
        sourceSteps = copyCollection(newValues, sourceSteps, ProcessStep.class);
    }

    /**
     * Returns the processing level of the source data. {@code null} if unspecified.
     *
     * @since 3.03
     */
    @Override
    @XmlElement(name = "processedLevel", namespace = Namespaces.GMI)
    public synchronized Identifier getProcessedLevel() {
        return processedLevel;
    }

    /**
     * Sets the processing level of the source data.
     *
     * @param newValue The new processed level value.
     *
     * @since 3.03
     */
    public synchronized void setProcessedLevel(final Identifier newValue) {
        checkWritePermission();
        processedLevel = newValue;
    }

    /**
     * Returns the distance between consistent parts (centre, left side, right side) of
     * two adjacent pixels. {@code null} if unspecified.
     *
     * @since 3.03
     */
    @Override
    @XmlElement(name = "resolution", namespace = Namespaces.GMI)
    public synchronized NominalResolution getResolution() {
        return resolution;
    }

    /**
     * Sets the distance between consistent parts (centre, left side, right side) of
     * two adjacent pixels.
     *
     * @param newValue The new nominal resolution value.
     *
     * @since 3.03
     */
    public synchronized void setResolution(final NominalResolution newValue) {
        checkWritePermission();
        resolution = newValue;
    }
}
