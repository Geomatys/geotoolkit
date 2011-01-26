/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.XmlElementRef;
import org.opengis.metadata.quality.*;
import org.geotoolkit.metadata.iso.quality.*;


/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.02
 * @module
 */
public final class DQ_Element extends MetadataAdapter<DQ_Element, Element> {
    /**
     * Empty constructor for JAXB only.
     */
    public DQ_Element() {
    }

    /**
     * Wraps an Element value with a {@code DQ_Element} element at marshalling time.
     *
     * @param metadata The metadata value to marshall.
     */
    private DQ_Element(final Element metadata) {
        super(metadata);
    }

    /**
     * Returns the Element value wrapped by a {@code DQ_Element} element.
     *
     * @param value The value to marshall.
     * @return The adapter which wraps the metadata value.
     */
    @Override
    protected DQ_Element wrap(final Element value) {
        return new DQ_Element(value);
    }

    /**
     * Returns the {@link AbstractElement} generated from the metadata value.
     * This method is systematically called at marshalling time by JAXB.
     *
     * @return The metadata to be marshalled.
     */
    @Override
    @XmlElementRef
    public AbstractElement getElement() {
        final Element metadata = this.metadata;
        if (metadata instanceof AbstractElement) {
            return (AbstractElement) metadata;
        }
        if (metadata instanceof PositionalAccuracy) {
            if (metadata instanceof AbsoluteExternalPositionalAccuracy) {
                return new DefaultAbsoluteExternalPositionalAccuracy((AbsoluteExternalPositionalAccuracy) metadata);
            }
            if (metadata instanceof GriddedDataPositionalAccuracy) {
                return new DefaultGriddedDataPositionalAccuracy((GriddedDataPositionalAccuracy) metadata);
            }
            if (metadata instanceof RelativeInternalPositionalAccuracy) {
                return new DefaultRelativeInternalPositionalAccuracy((RelativeInternalPositionalAccuracy) metadata);
            }
            return new AbstractPositionalAccuracy((PositionalAccuracy) metadata);
        }
        if (metadata instanceof TemporalAccuracy) {
            if (metadata instanceof AccuracyOfATimeMeasurement) {
                return new DefaultAccuracyOfATimeMeasurement((AccuracyOfATimeMeasurement) metadata);
            }
            if (metadata instanceof TemporalConsistency) {
                return new DefaultTemporalConsistency((TemporalConsistency) metadata);
            }
            if (metadata instanceof TemporalValidity) {
                return new DefaultTemporalValidity((TemporalValidity) metadata);
            }
            return new AbstractTemporalAccuracy((TemporalAccuracy) metadata);
        }
        if (metadata instanceof ThematicAccuracy) {
            if (metadata instanceof QuantitativeAttributeAccuracy) {
                return new DefaultQuantitativeAttributeAccuracy((QuantitativeAttributeAccuracy) metadata);
            }
            if (metadata instanceof NonQuantitativeAttributeAccuracy) {
                return new DefaultNonQuantitativeAttributeAccuracy((NonQuantitativeAttributeAccuracy) metadata);
            }
            if (metadata instanceof ThematicClassificationCorrectness) {
                return new DefaultThematicClassificationCorrectness((ThematicClassificationCorrectness) metadata);
            }
            return new AbstractThematicAccuracy((ThematicAccuracy) metadata);
        }
        if (metadata instanceof LogicalConsistency) {
            if (metadata instanceof ConceptualConsistency) {
                return new DefaultConceptualConsistency((ConceptualConsistency) metadata);
            }
            if (metadata instanceof DomainConsistency) {
                return new DefaultDomainConsistency((DomainConsistency) metadata);
            }
            if (metadata instanceof FormatConsistency) {
                return new DefaultFormatConsistency((FormatConsistency) metadata);
            }
            if (metadata instanceof TopologicalConsistency) {
                return new DefaultTopologicalConsistency((TopologicalConsistency) metadata);
            }
            return new AbstractLogicalConsistency((LogicalConsistency) metadata);
        }
        if (metadata instanceof Completeness) {
            if (metadata instanceof CompletenessCommission) {
                return new DefaultCompletenessCommission((CompletenessCommission) metadata);
            }
            if (metadata instanceof CompletenessOmission) {
                return new DefaultCompletenessOmission((CompletenessOmission) metadata);
            }
            return new AbstractCompleteness((Completeness) metadata);
        }
        if (metadata instanceof Usability) {
            return new DefaultUsability((Usability) metadata);
        }
        return new AbstractElement(metadata);
    }

    /**
     * Sets the value for the {@link AbstractElement}. This method is systematically
     * called at unmarshalling time by JAXB.
     *
     * @param metadata The unmarshalled metadata.
     */
    public void setElement(final AbstractElement metadata) {
        this.metadata = metadata;
    }
}
