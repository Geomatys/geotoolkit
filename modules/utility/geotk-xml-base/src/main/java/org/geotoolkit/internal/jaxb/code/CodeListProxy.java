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

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.opengis.util.CodeList;

import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.iso.Types;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.resources.Locales;


/**
 * Stores information about {@link CodeList}, in order to handle format defined in ISO-19139
 * about the {@code CodeList} tags. This object is wrapped by {@link CodeListAdapter} or, in
 * the spacial case of {@link Locale} type, by {@link CodeListLocaleAdapter}. It provides the
 * {@link #codeList} and {@link #codeListValue} attribute to be marshalled.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @see CodeListAdapter
 * @see CodeListLocaleAdapter
 *
 * @since 2.5
 * @module
 *
 * @todo JAXB BUG: Properties must be declared in reverse order. This is fixed in a JAXB version
 *       more recent than the one provided in the JDK. A Geotk test case should fail when the bug
 *       will be fixed, which will remind us to restore the correct order.
 */
@XmlType(name = "CodeList", propOrder = { "codeSpace", "codeListValue", "codeList" })
public final class CodeListProxy {
    /**
     * Returns the URL to given code list in the given XML file.
     *
     * @param  file The XML file, either {@code "gmxCodelists.xml"} or {@code "ML_gmxCodelists.xml"}.
     * @param  identifier The UML identifier of the code list.
     * @return The URL to the given code list in the given schema.
     *
     * @since 3.17
     */
    private static String schemaURL(final String file, final String identifier) {
        return MarshalContext.schema("gmd", "resources/Codelist", file, identifier);
    }

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
     * Creates a new code list for the given enum.
     *
     * @param value The ISO 19115 identifier of the enum.
     *
     * @todo Replace the argument type by {@link Enum} if we fix the type of ISO 19115
     *       code lists which are supposed to be enum.
     *
     * @see <a href="http://jira.codehaus.org/browse/GEO-199">GEO-199</a>
     *
     * @since 3.18
     */
    public CodeListProxy(final String value) {
        this.value = value;
    }

    /**
     * Builds a {@link CodeList} as defined in ISO-19139 standard.
     *
     * @param catalog The file which defines the code list (for example {@code "ML_gmxCodelists.xml"}), without its path.
     * @param codeList The {@code codeList} attribute, to be concatenated after the catalog name and the {@code "#"} symbol.
     * @param codeListValue The {@code codeListValue} attribute, to be declared in the attribute.
     * @param value The value in English language (because this constructor does not set the {@link #codeSpace} attribute).
     */
    public CodeListProxy(final String catalog, final String codeList, final String codeListValue, final String value) {
        this.codeList      = schemaURL(catalog, codeList);
        this.codeListValue = codeListValue;
        this.value         = value;
    }

    /**
     * Builds a proxy instance of {@link CodeList}. This constructors stores
     * the values that will be used for marshalling.
     *
     * @param code The code list to wrap.
     */
    public CodeListProxy(final CodeList<?> code) {
        final String classID = Types.getListName(code);
        final String fieldID = Types.getCodeName(code);
        codeList = schemaURL("gmxCodelists.xml", classID);

        // Get the localized name of the field identifier, if possible.
        // This code partially duplicates the CodeLists.localize(code) method.
        final Locale locale = MarshalContext.getLocale();
        if (locale != null) {
            final String key = classID + '.' + fieldID;
            try {
                value = ResourceBundle.getBundle("org.opengis.metadata.CodeLists", locale).getString(key);
            } catch (MissingResourceException e) {
                Logging.recoverableException(CodeListAdapter.class, "marshal", e);
            }
        }
        if (value != null) {
            codeSpace = Locales.getLanguage(locale);
        } else {
            // Fallback when no value is defined for the code list. Build a value from the
            // most descriptive name (excluding the field name), which is usually the UML
            // name except for CharacterSet in which case it is a string like "UTF-8".
            value = Types.getCodeLabel(code);
        }
        codeListValue = fieldID;
    }

    /**
     * Returns the identifier to use for fetching a {@link CodeList} instance.
     * This is normally the {@link #codeListValue} attribute. However if the
     * code list is actually used as an enumeration, then the above attribute
     * is null and we have to use directly the {@linkplain #value} instead.
     *
     * @return The identifier to be given to the {@code CodeList.valueOf(...)} method.
     *
     * @since 3.18
     */
    public String identifier() {
        String id = codeListValue;
        if (id == null) {
            id = value;
        }
        return id;
    }
}
