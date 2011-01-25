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
package org.geotoolkit.internal.jaxb.code;

import java.util.Locale;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.opengis.annotation.UML;
import org.opengis.util.CodeList;


/**
 * Stores information about {@link CodeList}, in order to handle format defined in ISO-19139
 * about the {@code CodeList} tags. This object is wrapped by {@link CodeListAdapter} or, in
 * the spacial case of {@link Locale} type, by {@link CodeListLocaleAdapter}. It provides the
 * {@link #codeList} and {@link #codeListValue} attribute to be marshalled.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @see CodeListAdapter
 * @see CodeListLocaleAdapter
 *
 * @since 2.5
 * @module
 *
 * @todo JAXB BUG: Properties must be declared in reverse order. This is fixed in a JAXB version
 *       more recent than the one provided in the JDK. The test failure will fail when the bug
 *       will be fixed, which will remind us to restore the correct order.
 */
@XmlType(name = "CodeList", propOrder = { "codeSpace", "codeListValue", "codeList" })
public final class CodeListProxy {
    /**
     * The base directory of code lists.
     *
     * @since 3.17
     */
    public static final String BASE_URL = "http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/";

    /**
     * Default common URL path for the {@code codeList} attribute value.
     */
    private static final String URL = BASE_URL + "gmxCodelists.xml#";

    /**
     * The {@code codeList} attribute in the XML element.
     */
    @XmlAttribute(required = true)
    public String codeList;

    /**
     * The {@code codeListValue} attribute in the XML element.
     */
    @XmlAttribute(required = true)
    public String codeListValue;

    /**
     * The optional {@code codeSpace} attribute in the XML element. The default value is
     * {@code null}. If a value is provided in this field, then {@link #value} should be
     * set as well.
     * <p>
     * This attribute is set to the 3 letters language code of the {@link #value} attribute,
     * as returned by {@link Locale#getISO3Language()}.
     *
     * @since 3.17
     */
    @XmlAttribute
    public String codeSpace;

    /**
     * The optional value to write in the XML element. The default value is {@code null}.
     * If a value is provided in this field, then {@link #codeSpace} is the language code
     * of this field or {@code null} for English.
     *
     * @since 3.17
     */
    @XmlValue
    public String value;

    /**
     * Default empty constructor for JAXB.
     */
    public CodeListProxy() {
    }

    /**
     * Builds a {@link CodeList} as defined in ISO-19139 standard.
     *
     * @param codeList The {@code codeList} attribute, <strong>including</strong> the URL path.
     * @param codeListValue The {@code codeListValue} attribute.
     * @param value The value to provide, in English language (because this
     *        constructor does not set the {@link #codeSpace} attribute).
     */
    CodeListProxy(final String codeList, final String codeListValue, final String value) {
        this.codeList      = codeList;
        this.codeListValue = codeListValue;
        this.value         = value;
    }

    /**
     * Builds a proxy instance of {@link CodeList}. It stores the values that will be
     * used for marshalling.
     *
     * @param code The code list to wrap.
     */
    CodeListProxy(final CodeList<?> code) {
        // Get the class identifier.
        final Class<?> type = code.getClass();
        final UML uml = type.getAnnotation(UML.class);
        String identifier = null;
        if (uml != null) {
            identifier = uml.identifier();
        }
        if (identifier == null || (identifier = identifier.trim()).length() == 0) {
            identifier = type.getSimpleName();
        }
        codeList = URL.concat(identifier);

        // Get the field identifier.
        identifier = code.identifier();
        codeListValue = (identifier != null &&
                (identifier = identifier.trim()).length() != 0) ? identifier : code.name();
    }
}
