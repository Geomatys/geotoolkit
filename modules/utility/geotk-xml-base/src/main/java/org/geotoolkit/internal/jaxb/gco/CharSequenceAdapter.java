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
package org.geotoolkit.internal.jaxb.gco;

import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opengis.util.InternationalString;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.jaxb.gmx.Anchor;
import org.geotoolkit.internal.jaxb.gmd.PT_FreeText;


/**
 * JAXB adapter in order to wrap the string value with a {@code <gco:CharacterString>} element,
 * for ISO-19139 compliance. A {@link CharSequenceAdapter} can also substitute text by anchors.
 * At the difference of most adapters provided in {@code org.geotoolkit.internal.jaxb} packages,
 * this adapter is <em>configurable</em>. It must be created explicitly with a map of bindings
 * between labels and URNs, and the configured adapter must be given to the mashaller as below:
 *
 * {@preformat java
 *     CharSequenceAdapter adapter = new CharSequenceAdapter();
 *     adapter.addLinkage(...);
 *     marshaller.setAdapter(adapter);
 *     marshaller.setAdapter(new StringAdapter(adapter));
 *     marshaller.setAdapter(new InternationalStringAdapter(adapter));
 * }
 *
 * This class can also handles {@link InternationalString}, which will be mapped to
 * {@link PT_FreeText} elements.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @see StringAdapter
 * @see InternationalStringAdapter
 *
 * @since 3.00
 * @module
 */
public final class CharSequenceAdapter extends XmlAdapter<GO_CharacterString, CharSequence> {
    /**
     * Binds string labels with URNs or anchors. Values can be either {@link URI} or
     * {@link Anchor} instances. The map is initially null and will be created
     * when first needed.
     *
     * @see #addLinkage(String, URI)
     * @see #addLinkage(Anchor)
     */
    private Map<String,Object> anchors;

    /**
     * Creates a uninitialized adapter.
     */
    public CharSequenceAdapter() {
    }

    /**
     * Adds a label associated to the given URN.
     *
     * @param  label The label associated to the URN.
     * @param  linkage The URN.
     * @throws IllegalStateException If a URN is already associated to the given label.
     */
    public void addLinkage(final String label, final URI linkage) throws IllegalStateException {
        add(label, linkage);
    }

    /**
     * Adds an anchor (label associated to an URN).
     *
     * @param  anchor The anchor to add.
     * @throws IllegalStateException If a URN is already associated to the anchor value.
     *
     * @since 3.14
     */
    public void addLinkage(final Anchor anchor) throws IllegalStateException {
        add(anchor.toString(), anchor);
    }

    /**
     * Implementation of {@code addLinkage} methods.
     */
    private synchronized void add(final String label, final Object linkage) throws IllegalStateException {
        if (anchors == null) {
            anchors = new HashMap<String,Object>();
        }
        final Object old = anchors.put(label, linkage);
        if (old != null) {
            anchors.put(label, old);
            throw new IllegalStateException(Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_1, label));
        }
    }

    /**
     * Converts a string read from a XML stream to the object containing
     * the value. JAXB calls automatically this method at unmarshalling time.
     *
     * @param value The adapter for this metadata value.
     * @return A {@link CharSequence} which represents the metadata value.
     */
    @Override
    public CharSequence unmarshal(final GO_CharacterString value) {
        if (value != null) {
            if (value instanceof PT_FreeText) {
                final PT_FreeText freeText = (PT_FreeText) value;
                String defaultValue = freeText.toString(); // May be null.
                if (defaultValue != null && freeText.contains(defaultValue)) {
                    /*
                     * If the <gco:CharacterString> value is repeated in one of the
                     * <gmd:LocalisedCharacterString> elements, keep only the localized
                     * version  (because it specifies the locale, while the unlocalized
                     * string saids nothing on that matter).
                     */
                    defaultValue = null;
                }
                /*
                 * Create the international string with all locales found in the <gml:textGroup>
                 * element. If the <gml:textGroup> element is missing or empty, then we will use
                 * an instance of SimpleInternationalString instead than the more heavy
                 * DefaultInternationalString.
                 */
                return freeText.toInternationalString(defaultValue);
            }
            /*
             * Case where the value is an ordinary GO_CharacterString (not a PT_FreeText).
             */
            CharSequence text = value.text;
            if (text != null) {
                if (text instanceof String) {
                    text = ((String) text).trim();
                }
                if (text.length() != 0 || text instanceof Anchor) { // Anchor may contain attributes.
                    return text;
                }
            }
        }
        return null;
    }

    /**
     * Converts a {@linkplain CharSequence character sequence} to the object to be marshalled
     * in a XML file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The string value.
     * @return The wrapper for the given string.
     */
    @Override
    public GO_CharacterString marshal(CharSequence value) {
        if (value instanceof String) {
            value = ((String) value).trim();
        }
        if (value == null || value.length() == 0) {
            return null;
        }
        /*
         * <gmd:someElement xsi:type="gmd:PT_FreeText_PropertyType">
         *   <gco:CharacterString>...</gco:CharacterString>
         *   <gmd:PT_FreeText>
         *     ... see PT_FreeText ...
         *   </gmd:PT_FreeText>
         * </gmd:someElement>
         */
        if (value instanceof InternationalString) {
            final PT_FreeText ft = PT_FreeText.create((InternationalString) value);
            if (ft != null) {
                return ft;
            }
        }
        /*
         * Substitute <gco:CharacterString> by <gmx:Anchor> if a linkage is found.
         */
        if (!(value instanceof Anchor)) {
            synchronized (this) {
                if (anchors != null) {
                    String key = value.toString();
                    if (key != null) {
                        key = key.trim();
                        if (!key.isEmpty()) {
                            final Object linkage = anchors.get(key);
                            if (linkage != null) {
                                if (linkage instanceof URI) {
                                    value = new Anchor((URI) linkage, key);
                                } else {
                                    value = (Anchor) linkage;
                                }
                            }
                        }
                    }
                }
            }
        }
        /*
         * At this stage, the value (typically a String or InternationalString) may
         * have been replaced by an Anchor. The output will be one of the following:
         *
         * ┌──────────────────────────────────────────────────┬────────────────────────────────┐
         * │ <gmd:someElement>                                │ <gmd:someElement>              │
         * │   <gco:CharacterString>...</gco:CharacterString> │   <gmx:Anchor>...</gmx:Anchor> │
         * │ </gmd:someElement>                               │ </gmd:someElement>             │
         * └──────────────────────────────────────────────────┴────────────────────────────────┘
         */
        return new GO_CharacterString(value);
    }
}
