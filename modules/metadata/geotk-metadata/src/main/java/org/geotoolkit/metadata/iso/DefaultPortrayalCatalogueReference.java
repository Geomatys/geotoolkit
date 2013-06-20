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
package org.geotoolkit.metadata.iso;

import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.PortrayalCatalogueReference;


/**
 * Information identifying the portrayal catalogue used.
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
@XmlType(name = "MD_PortrayalCatalogueReference_Type")
@XmlRootElement(name = "MD_PortrayalCatalogueReference")
public class DefaultPortrayalCatalogueReference extends MetadataEntity
        implements PortrayalCatalogueReference
{
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -3095277682987563157L;

    /**
     * Bibliographic reference to the portrayal catalogue cited.
     */
    private Collection<Citation> portrayalCatalogueCitations;

    /**
     * Construct an initially empty portrayal catalogue reference.
     */
    public DefaultPortrayalCatalogueReference() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultPortrayalCatalogueReference(final PortrayalCatalogueReference source) {
        super(source);
    }

    /**
     * Creates a portrayal catalogue reference initialized to the given values.
     * @param portrayalCatalogueCitations
     */
    public DefaultPortrayalCatalogueReference(final Collection<Citation> portrayalCatalogueCitations) {
        setPortrayalCatalogueCitations(portrayalCatalogueCitations);
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
    public static DefaultPortrayalCatalogueReference castOrCopy(final PortrayalCatalogueReference object) {
        return (object == null) || (object instanceof DefaultPortrayalCatalogueReference)
                ? (DefaultPortrayalCatalogueReference) object
                : new DefaultPortrayalCatalogueReference(object);
    }

    /**
     * Bibliographic reference to the portrayal catalogue cited.
     */
    @Override
    @XmlElement(name = "portrayalCatalogueCitation", required = true)
    public synchronized Collection<Citation> getPortrayalCatalogueCitations() {
        return portrayalCatalogueCitations = nonNullCollection(portrayalCatalogueCitations, Citation.class);
    }

    /**
     * Sets bibliographic reference to the portrayal catalogue cited.
     *
     * @param newValues The new portrayal catalogue citations.
     */
    public synchronized void setPortrayalCatalogueCitations(Collection<? extends Citation> newValues) {
        portrayalCatalogueCitations = copyCollection(newValues, portrayalCatalogueCitations, Citation.class);
    }
}
