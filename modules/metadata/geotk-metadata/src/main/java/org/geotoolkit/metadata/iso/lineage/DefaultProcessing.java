/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.lineage;

import java.util.Collection;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.lineage.Algorithm;
import org.opengis.metadata.lineage.Processing;
import org.opengis.util.InternationalString;


/**
 * Comprehensive information about the procedure(s), process(es) and algorithm(s) applied
 * in the process step.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@XmlType(propOrder={
    "identifier",
    "softwareReferences",
    "procedureDescription",
    "documentations",
    "runTimeParameter",
    "algorithms"
})
@XmlRootElement(name = "LE_Processing")
public class DefaultProcessing extends MetadataEntity implements Processing {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -8032712379901591271L;

    /**
     * Information to identify the processing package that produced the data.
     */
    private Identifier identifier;

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
    private InternationalString runTimeParameter;

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
     * @param source The metadata to copy.
     */
    public DefaultProcessing(final Processing source) {
        super(source);
    }

    /**
     * Returns the information to identify the processing package that produced the data.
     */
    @Override
    @XmlElement(name = "identifier")
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the information to identify the processing package that produced the data.
     *
     * @param newValue The new identifier value.
     */
    public synchronized void setIdentifier(final Identifier newValue) {
        checkWritePermission();
        identifier = newValue;
    }

    /**
     * Returns the reference to document describing processing software.
     */
    @Override
    @XmlElement(name = "softwareReference")
    public synchronized Collection<Citation> getSoftwareReferences() {
        return xmlOptional(softwareReferences = nonNullCollection(softwareReferences, Citation.class));
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
    @XmlElement(name = "procedureDescription")
    public InternationalString getProcedureDescription() {
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
    @XmlElement(name = "documentation")
    public synchronized Collection<Citation> getDocumentations() {
        return xmlOptional(documentations = nonNullCollection(documentations, Citation.class));
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
    @XmlElement(name = "runTimeParameter")
    public InternationalString getRunTimeParameter() {
        return runTimeParameter;
    }

    /**
     * Sets the parameters to control the processing operations, entered at run time.
     *
     * @param newValue The new runtime parameter value.
     */
    public synchronized void setRunTimeParameter(final InternationalString newValue) {
        checkWritePermission();
        runTimeParameter = newValue;
    }

    /**
     * Returns the details of the methodology by which geographic information was derived from the
     * instrument readings.
     */
    @Override
    @XmlElement(name = "algorithm")
    public synchronized Collection<Algorithm> getAlgorithms() {
        return xmlOptional(algorithms = nonNullCollection(algorithms, Algorithm.class));
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

    /**
     * Sets the {@code xmlMarshalling} flag to {@code true}, since the marshalling
     * process is going to be done. This method is automatically called by JAXB when
     * the marshalling begins.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
        xmlMarshalling(true);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code false}, since the marshalling
     * process is finished. This method is automatically called by JAXB when the
     * marshalling ends.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void afterMarshal(Marshaller marshaller) {
        xmlMarshalling(false);
    }
}
