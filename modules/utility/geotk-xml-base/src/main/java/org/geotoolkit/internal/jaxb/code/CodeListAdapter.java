/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.internal.jaxb.code;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.opengis.util.CodeList;
import org.apache.sis.util.iso.Types;


/**
 * An adapter for {@link CodeList}, in order to implement the ISO-19139 standard. This object
 * wraps a {@link CodeListProxy}, which contains {@link CodeListProxy#codeList codeList} and
 * {@link CodeListProxy#codeListValue codeListValue} attributes. The result looks like below:
 *
 * {@preformat xml
 *   <dateType>
 *     <CI_DateTypeCode codeList="../Codelist/ML_gmxCodelists.xml#CI_DateTypeCode" codeListValue="revision" codeSpace="fra">
 *       révision
 *     </CI_DateTypeCode>
 *   </dateType>
 * }
 *
 * A subclass must exist for each code list, with a {@link #getElement()} method having a
 * {@code @XmlElement} annotation.
 *
 * @param <ValueType> The subclass implementing this adapter.
 * @param <BoundType> The code list being adapted.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see CodeListLocaleAdapter
 *
 * @since 2.5
 * @module
 */
public abstract class CodeListAdapter<ValueType extends CodeListAdapter<ValueType,BoundType>,
        BoundType extends CodeList<BoundType>> extends XmlAdapter<ValueType,BoundType>
{
    /**
     * A proxy form of the {@link CodeList}.
     */
    protected CodeListProxy proxy;

    /**
     * Empty constructor for subclasses only.
     */
    protected CodeListAdapter() {
    }

    /**
     * Creates a wrapper for a {@link CodeList}, in order to handle the format specified
     * in ISO-19139.
     *
     * @param proxy The proxy version of {@link CodeList} to be marshalled.
     */
    protected CodeListAdapter(final CodeListProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Forces the initialization of the given code list class, since some
     * calls to {@link CodeList#valueOf} are done whereas the constructor
     * has not already been called.
     *
     * @param <T>  The code list type.
     * @param type The code list class to initialize.
     */
    protected static <T extends CodeList<T>> void ensureClassLoaded(final Class<T> type) {
        final String name = type.getName();
        try {
            Class.forName(name, true, type.getClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new TypeNotPresentException(name, ex); // Should never happen.
        }
    }

    /**
     * Wraps the proxy value into an adapter.
     *
     * @param proxy The proxy version of {@link CodeList}, to be marshalled.
     * @return The adapter that wraps the proxy value.
     */
    protected abstract ValueType wrap(final CodeListProxy proxy);

    /**
     * Returns the class of code list wrapped by this adapter.
     *
     * @return The code list class.
     */
    protected abstract Class<BoundType> getCodeListClass();

    /**
     * Substitutes the adapter value read from an XML stream by the object which will
     * contains the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param  adapter The adapter for this metadata value.
     * @return A code list which represents the metadata value.
     */
    @Override
    public final BoundType unmarshal(final ValueType adapter) {
        if (adapter == null) {
            return null;
        }
        return Types.forCodeName(getCodeListClass(), adapter.proxy.identifier(), true);
    }

    /**
     * Substitutes the code list by the adapter to be marshalled into an XML file
     * or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param  value The code list value.
     * @return The adapter for the given code list.
     */
    @Override
    public final ValueType marshal(final BoundType value) {
        if (value == null) {
            return null;
        }
        return wrap(isEnum() ? new CodeListProxy(Types.getCodeName(value)) : new CodeListProxy(value));
    }

    /**
     * Returns {@code true} if this code list is actually an enum. The default implementation
     * returns {@code false} in every cases, since there is very few enums in ISO 19115.
     *
     * @return {@code true} if this code list is actually an enum.
     *
     * @since 3.18
     */
    protected boolean isEnum() {
        return false;
    }

    /**
     * Invoked by JAXB on marshalling. Subclasses must override this
     * method with the appropriate {@code @XmlElement} annotation.
     *
     * @return The {@code CodeList} value to be marshalled.
     */
    public abstract CodeListProxy getElement();

    /*
     * We do not define setter method (even abstract) since it seems to confuse JAXB.
     * It is subclasses responsibility to define the setter method. The existence of
     * this setter will be tested by MetadataAnnotationsTest.
     */
}
