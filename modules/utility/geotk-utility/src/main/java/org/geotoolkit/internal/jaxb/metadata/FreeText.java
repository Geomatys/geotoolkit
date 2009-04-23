/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.internal.jaxb.text.CharacterString;
import org.geotoolkit.util.DefaultInternationalString;


/**
 * JAXB adapter for ISO-19139 {@code <PT_FreeText>} element mapped to {@link InternationalString}.
 * It will be used in order to marshall and unmarshall international strings localized in several
 * language, using the {@link DefaultInternationalString} implementation class.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 2.5
 * @module
 */
@XmlType(name = "PT_FreeText_PropertyType")
public final class FreeText extends CharacterString {
    /**
     * A set of {@link LocalisedCharacterString}, representing the {@code <textGroup>} element.
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
     * which could contains several localised strings.
     *
     * @param text An international string which could have several translations embedded for the
     *             same text.
     */
    public FreeText(final DefaultInternationalString text) {
        this.textGroup = new TextGroup(text);
        if (text != null) {
            this.text = text;
        }
    }
}
