/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.lineage;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.util.InternationalString;
import org.opengis.metadata.lineage.Source;
import org.opengis.metadata.lineage.Lineage;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.metadata.maintenance.ScopeCode;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.metadata.iso.quality.DefaultScope;
import org.geotoolkit.xml.Namespaces;


/**
 * Information about the events or source data used in constructing the data specified by
 * the scope or lack of knowledge about lineage.
 *
 * Only one of {@linkplain #getStatement statement}, {@linkplain #getProcessSteps process steps}
 * and {@link #getSources sources} should be provided.
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
@XmlType(propOrder={
    "statement",
    "processSteps",
    "sources"
})
@XmlRootElement(name = "LI_Lineage", namespace = Namespaces.GMI)
public class DefaultLineage extends MetadataEntity implements Lineage {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 3351230301999744987L;

    /**
     * General explanation of the data producer's knowledge about the lineage of a dataset.
     * Should be provided only if {@linkplain DefaultScope#getLevel scope level} is
     * {@linkplain ScopeCode#DATASET dataset} or {@linkplain ScopeCode#SERIES series}.
     */
    private InternationalString statement;

    /**
     * Information about an event in the creation process for the data specified by the scope.
     */
    private Collection<ProcessStep> processSteps;

    /**
     * Information about the source data used in creating the data specified by the scope.
     */
    private Collection<Source> sources;

    /**
     * Constructs an initially empty lineage.
     */
    public DefaultLineage() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultLineage(final Lineage source) {
        super(source);
    }

    /**
     * Returns the general explanation of the data producer's knowledge about the lineage
     * of a dataset. Should be provided only if {@linkplain DefaultScope#getLevel scope level}
     * is {@linkplain ScopeCode#DATASET dataset} or {@linkplain ScopeCode#SERIES series}.
     */
    @Override
    @XmlElement(name = "statement")
    public synchronized InternationalString getStatement() {
        return statement;
    }

    /**
     * Sets the general explanation of the data producers knowledge about the lineage
     * of a dataset.
     *
     * @param newValue The new statement.
     */
    public synchronized void setStatement(final InternationalString newValue) {
        checkWritePermission();
        statement = newValue;
    }

    /**
     * Returns the information about an event in the creation process for the data
     * specified by the scope.
     */
    @Override
    @XmlElement(name = "processStep")
    public synchronized Collection<ProcessStep> getProcessSteps() {
        return xmlOptional(processSteps = nonNullCollection(processSteps, ProcessStep.class));
    }

    /**
     * Sets information about an event in the creation process for the data specified
     * by the scope.
     *
     * @param newValues The new process steps.
     */
    public synchronized void setProcessSteps(final Collection<? extends ProcessStep> newValues)  {
        processSteps = copyCollection(newValues, processSteps, ProcessStep.class);
    }

    /**
     * Returns information about the source data used in creating the data specified by the scope.
     */
    @Override
    @XmlElement(name = "source")
    public synchronized Collection<Source> getSources() {
        return xmlOptional(sources = nonNullCollection(sources, Source.class));
    }

    /**
     * Sets information about the source data used in creating the data specified by the scope.
     *
     * @param newValues The new sources.
     */
    public synchronized void setSources(final Collection<? extends Source> newValues) {
        sources = copyCollection(newValues, sources, Source.class);
    }
}
