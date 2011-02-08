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

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import org.opengis.util.InternationalString;
import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.internal.jaxb.text.CharacterString;
import org.geotoolkit.util.DefaultInternationalString;


/**
 * JAXB adapter for ISO-19139 {@code <PT_FreeText>} element mapped to {@link InternationalString}.
 * It will be used in order to marshall and unmarshall international strings localized in several
 * language, using the {@link DefaultInternationalString} implementation class. Example:
 *
 * {@preformat xml
 *   <gmd:title xsi:type="gmd:PT_FreeText_PropertyType">
 *     <gco:CharacterString>Some title in english is present in this node</gco:CharacterString>
 *     <gmd:PT_FreeText>
 *       <gmd:textGroup>
 *         <gmd:LocalisedCharacterString locale="#locale-fra">Un titre en français</gmd:LocalisedCharacterString>
 *       </gmd:textGroup>
 *     </gmd:PT_FreeText>
 *   </gmd:title>
 * }
 *
 * The {@code <gco:CharacterString>} element is inherited from the {@link CharacterString}
 * parent class.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 2.5
 * @module
 */
@XmlType(name = "PT_FreeText_PropertyType")
public final class FreeText extends CharacterString {
    /**
     * A set of {@link LocalisedCharacterString}, representing the {@code <gmd:textGroup>} element.
     */
    @XmlElement(name = "PT_FreeText", required = true)
    TextGroup textGroup;

    /**
     * Empty constructor used only by JAXB.
     */
    public FreeText() {
    }

    /**
     * Constructs a {@linkplain TextGroup text group} from a {@link DefaultInternationalString}
     * which could contains several localized strings.
     * <p>
     * The {@code <gco:CharacterString> element will typically be set for the {@code null} locale,
     * which is the "unlocalized" string (not the same thing than the string in the default locale).
     * Note that the {@link TextGroup} constructor works better if the {@code <gco:CharacterString>}
     * have been set for the {@code null} locale (the default behavior). If a different locale were
     * set, the list of localized strings in {@code TextGroup} may contains an element which
     * duplicate the {@code <gco:CharacterString>} element, or the unlocalized string normally
     * written in {@code <gco:CharacterString>} may be missing.
     *
     * @param text An international string which could have several translations embedded for the
     *             same text.
     *
     * @see org.geotoolkit.xml.XML#LOCALE
     */
    public FreeText(final DefaultInternationalString text) {
        super(text.toString(MarshalContext.getLocale()));
        textGroup = new TextGroup(text);
    }
}
