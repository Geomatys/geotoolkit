/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.lineage.Algorithm;
import org.opengis.metadata.lineage.Processing;
import org.opengis.util.InternationalString;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.NonMarshalledAuthority;


/**
 * Comprehensive information about the procedure(s), process(es) and algorithm(s) applied
 * in the process step.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.03
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "LE_Processing_Type", propOrder={
    "identifier",
    "softwareReferences",
    "procedureDescription",
    "documentations",
    "runTimeParameters",
    "algorithms"
})
@XmlRootElement(name = "LE_Processing", namespace = Namespaces.GMI)
public class DefaultProcessing extends MetadataEntity implements Processing {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -8032712379901591272L;

    /**
     * Reference to document describing processing software.
     */
    private Collection<Citation> softwareReferences;

    /**
     * Additional details about the processing procedures.
     */
    private InternationalString procedureDescription;

    /**
     * Reference to documentation describing the processing.
     */
    private Collection<Citation> documentations;

    /**
     * Parameters to control the processing operations, entered at run time.
     */
    private InternationalString runTimeParameters;

    /**
     * Details of the methodology by which geographic information was derived from the
     * instrument readings.
     */
    private Collection<Algorithm> algorithms;

    /**
     * Constructs an initially empty range element description.
     */
    public DefaultProcessing() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultProcessing(final Processing source) {
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
    public static DefaultProcessing castOrCopy(final Processing object) {
        return (object == null) || (object instanceof DefaultProcessing)
                ? (DefaultProcessing) object : new DefaultProcessing(object);
    }

    /**
     * Returns the information to identify the processing package that produced the data.
     */
    @Override
    @XmlElement(name = "identifier", namespace = Namespaces.GMI, required = true)
    public Identifier getIdentifier() {
        return super.getIdentifier();
    }

    /**
     * Sets the information to identify the processing package that produced the data.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        NonMarshalledAuthority.setMarshallable(super.getIdentifiers(), newValue);
    }

    /**
     * Returns the reference to document describing processing software.
     */
    @Override
    @XmlElement(name = "softwareReference", namespace = Namespaces.GMI)
    public synchronized Collection<Citation> getSoftwareReferences() {
        return softwareReferences = nonNullCollection(softwareReferences, Citation.class);
    }

    /**
     * Sets the reference to document describing processing software.
     *
     * @param newValues The new software references values.
     */
    public synchronized void setSoftwareReferences(final Collection<? extends Citation> newValues) {
        softwareReferences = copyCollection(newValues, softwareReferences, Citation.class);
    }

    /**
     * Returns the additional details about the processing procedures. {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "procedureDescription", namespace = Namespaces.GMI)
    public synchronized InternationalString getProcedureDescription() {
        return procedureDescription;
    }

    /**
     * Sets the additional details about the processing procedures.
     *
     * @param newValue The new procedure description value.
     */
    public synchronized void setProcedureDescription(final InternationalString newValue) {
        checkWritePermission();
        procedureDescription = newValue;
    }

    /**
     * Returns the reference to documentation describing the processing.
     */
    @Override
    @XmlElement(name = "documentation", namespace = Namespaces.GMI)
    public synchronized Collection<Citation> getDocumentations() {
        return documentations = nonNullCollection(documentations, Citation.class);
    }

    /**
     * Sets the reference to documentation describing the processing.
     *
     * @param newValues The new documentations values.
     */
    public synchronized void setDocumentations(final Collection<? extends Citation> newValues) {
        documentations = copyCollection(newValues, documentations, Citation.class);
    }

    /**
     * Returns the parameters to control the processing operations, entered at run time.
     * {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "runTimeParameters", namespace = Namespaces.GMI)
    public synchronized InternationalString getRunTimeParameters() {
        return runTimeParameters;
    }

    /**
     * Sets the parameters to control the processing operations, entered at run time.
     *
     * @param newValue The new runtime parameter value.
     */
    public synchronized void setRunTimeParameters(final InternationalString newValue) {
        checkWritePermission();
        runTimeParameters = newValue;
    }

    /**
     * Returns the details of the methodology by which geographic information was derived from the
     * instrument readings.
     */
    @Override
    @XmlElement(name = "algorithm", namespace = Namespaces.GMI)
    public synchronized Collection<Algorithm> getAlgorithms() {
        return algorithms = nonNullCollection(algorithms, Algorithm.class);
    }

    /**
     * Sets the details of the methodology by which geographic information was derived from the
     * instrument readings.
     *
     * @param newValues The new algorithms values.
     */
    public synchronized void setAlgorithms(final Collection<? extends Algorithm> newValues) {
        algorithms = copyCollection(newValues, algorithms, Algorithm.class);
    }
}
