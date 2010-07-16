/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.internal.jaxb.metadata;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opengis.util.InternationalString;

import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.internal.jaxb.text.CharacterString;
import org.geotoolkit.internal.jaxb.text.AnchorType;



/**
 * JAXB adapter for XML {@code <CharacterString>} element mapped to {@link InternationalString}.
 * This base class is suitable for strings localized in a single, usually unknown, language.
 * Strings localized in many languages are handled by the {@link FreeText} subclass.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.14
 *
 * @since 2.5
 * @module
 */
public class InternationalStringAdapter extends XmlAdapter<CharacterString, InternationalString> {
    /**
     * Empty constructor for JAXB.
     */
    public InternationalStringAdapter() {
    }

    /**
     * Converts an object read from a XML stream to an {@link InternationalString}
     * implementation. JAXB invokes automatically this method at unmarshalling time.
     *
     * @param value The adapter for the string value.
     * @return An {@link InternationalString} for the string value.
     */
    @Override
    public final InternationalString unmarshal(final CharacterString value) {
        if (value != null) {
            if (value instanceof FreeText) {
                final FreeText freeText = (FreeText) value;
                String defaultValue = freeText.toString();
                final TextGroup textGroup = freeText.textGroup;
                if (textGroup == null) {
                    if (defaultValue == null) {
                        return null;
                    }
                } else if (defaultValue != null) {
                    /*
                     * If the <gco:CharacterString> value is repeated in one of the
                     * <gmd:LocalisedCharacterString> elements, keep only the localized
                     * version  (because it specifies the locale, while the unlocalized
                     * string saids nothing on that matter).
                     */
                    for (final LocalisedCharacterString localized : textGroup.localised) {
                        if (defaultValue.equals(localized.text)) {
                            defaultValue = null;
                            break;
                        }
                    }
                }
                /*
                 * Create the international string with all locales found in the
                 * <gml:textGroup> element.
                 */
                final DefaultInternationalString ist = new DefaultInternationalString(defaultValue);
                if (textGroup != null) {
                    for (final LocalisedCharacterString localized : textGroup.localised) {
                        ist.add(localized.locale, localized.text);
                    }
                }
                return ist;
            }
            final AnchorType anchor = value.getAnchor();
            if (anchor != null) {
                return anchor;
            }
            String text = value.toString();
            if (text != null) {
                text = text.trim();
                if (text.length() != 0) {
                    return new SimpleInternationalString(text);
                }
            }
        }
        return null;
    }

    /**
     * Converts an {@link InternationalString} to an object to format into a
     * XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param  value The string value.
     * @return The adapter for the string.
     */
    @Override
    public CharacterString marshal(final InternationalString value) {
        if (value != null) {
            if (value instanceof AnchorType) {
                return new CharacterString((AnchorType) value);
            }
            if (value instanceof DefaultInternationalString) {
                return new FreeText((DefaultInternationalString) value);
            }
            String text = value.toString();
            if (text != null) {
                text = text.trim();
                if (text.length() != 0) {
                    return new CharacterString(text);
                }
            }
        }
        return null;
    }
}
