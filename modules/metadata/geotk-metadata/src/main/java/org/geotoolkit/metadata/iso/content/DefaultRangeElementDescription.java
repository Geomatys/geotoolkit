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
package org.geotoolkit.metadata.iso.content;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.content.RangeElementDescription;
import org.opengis.util.InternationalString;
import org.opengis.util.Record;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.xml.Namespaces;


/**
 * Description of specific range elements.
 *
 * @author Cédric Briançon (Geomatys)
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
@XmlType(name = "MI_RangeElementDescription_Type", propOrder={
    "name",
    "definition"/*,
    "rangeElements"*/
})
@XmlRootElement(name = "MI_RangeElementDescription", namespace = Namespaces.GMI)
public class DefaultRangeElementDescription extends MetadataEntity implements RangeElementDescription {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2869953851390143207L;

    /**
     * Designation associated with a set of range elements.
     */
    private InternationalString name;

    /**
     * Description of a set of specific range elements.
     */
    private InternationalString definition;

    /**
     * Specific range elements, i.e. range elements associated with a name and their definition.
     */
    private Collection<Record> rangeElements;

    /**
     * Constructs an initially empty range element description.
     */
    public DefaultRangeElementDescription() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     */
    public DefaultRangeElementDescription(final RangeElementDescription source) {
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
    public static DefaultRangeElementDescription castOrCopy(final RangeElementDescription object) {
        return (object == null) || (object instanceof DefaultRangeElementDescription)
                ? (DefaultRangeElementDescription) object : new DefaultRangeElementDescription(object);
    }

    /**
     * Returns the designation associated with a set of range elements.
     */
    @Override
    @XmlElement(name = "name", namespace = Namespaces.GMI, required = true)
    public synchronized InternationalString getName() {
        return name;
    }

    /**
     * Sets the designation associated with a set of range elements.
     *
     * @param newValue The new name value.
     */
    public synchronized void setName(final InternationalString newValue) {
        checkWritePermission();
        name = newValue;
    }

    /**
     * Returns the description of a set of specific range elements.
     */
    @Override
    @XmlElement(name = "definition", namespace = Namespaces.GMI, required = true)
    public synchronized InternationalString getDefinition() {
        return definition;
    }

    /**
     * Sets the description of a set of specific range elements.
     *
     * @param newValue The new definition value.
     */
    public synchronized void setDefinition(final InternationalString newValue) {
        checkWritePermission();
        definition = newValue;
    }

    /**
     * Returns the specific range elements, i.e. range elements associated with a name
     * and their definition.
     *
     * @todo implements {@link Record} in order to use the annotation.
     */
    @Override
    //@XmlElement(name = "rangeElement", namespace = Namespaces.GMI, required = true)
    public synchronized Collection<Record> getRangeElements() {
        return rangeElements = nonNullCollection(rangeElements, Record.class);
    }

    /**
     * Sets the specific range elements, i.e. range elements associated with a name and
     * their definition.
     *
     * @param newValues The new range element values.
     */
    public synchronized void setRangeElements(final Collection<? extends Record> newValues) {
        rangeElements = copyCollection(newValues, rangeElements, Record.class);
    }
}
