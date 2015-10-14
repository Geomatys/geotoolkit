/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.referencing;

import java.util.Locale;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXLabel;

import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.metadata.Identifier;

import org.geotoolkit.io.X364;
import org.apache.sis.io.wkt.Colors;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.io.wkt.Warnings;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.StringBuilders;


/**
 * Displays informations about an {@linkplain IdentifiedObject Identified Object}.
 * This widget displays the following tabs:
 * <p>
 * <ul>
 *   <li>An information tab with the object {@linkplain IdentifiedObject#getName() name} and
 *       {@linkplain IdentifiedObject#getIdentifiers() identifiers}.</li>
 *   <li>A <cite>Well Known Text</cite> (WKT) tab.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class PropertiesSheet extends JComponent {
    /**
     * The object name, identifier.
     */
    private final JTextField name, authority, identifier, type;

    /**
     * The remarks.
     */
    private final JXLabel remarks;

    /**
     * The <cite>Well Known Text</cite> area.
     */
    private final JEditorPane wktArea;

    /**
     * The formatter to use for formatting WKT objects.
     */
    private final WKTFormat formatter;

    /**
     * Creates a new, initially empty, property sheet.
     */
    public PropertiesSheet() {
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        final JPanel info = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridy=0; c.fill=GridBagConstraints.HORIZONTAL;
        name       = addField(info, resources.getLabel(Vocabulary.Keys.Name),       c);
        authority  = addField(info, resources.getLabel(Vocabulary.Keys.Authority),  c);
        identifier = addField(info, resources.getLabel(Vocabulary.Keys.Identifier), c);
        type       = addField(info, resources.getLabel(Vocabulary.Keys.Type),       c);
        info.setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.Identification)));
        info.setOpaque(false);

        remarks = new JXLabel();
        remarks.setLineWrap(true);
        remarks.setVerticalAlignment(JLabel.TOP);
        remarks.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        final Box rem = Box.createVerticalBox();
        rem.add(remarks);
        rem.setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.Remarks)));

        final JPanel general = new JPanel(new BorderLayout(0, 6));
        general.add(info, BorderLayout.BEFORE_FIRST_LINE);
        general.add(rem,  BorderLayout.CENTER);
        general.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        general.setOpaque(false);
        /*
         * Build the WKT tab.
         */
        wktArea = new JEditorPane();
        wktArea.setEditable(false);
        wktArea.setContentType("text/html");
        formatter = new WKTFormat();
        formatter.setColors(Colors.DEFAULT);
        /*
         * Add the tabs.
         */
        final JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(resources.getString(Vocabulary.Keys.General), general);
        tabs.addTab("WKT", new JScrollPane(wktArea));
        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    /**
     * Adds a field with the given label in front of it.
     */
    private static JTextField addField(final JPanel panel, final String text, final GridBagConstraints c) {
        final JLabel label = new JLabel(text);
        final JTextField value = new JTextField();
        value.setEditable(false);
        label.setLabelFor(value);
        c.gridx=0; c.weightx=0; c.insets.left=18; c.insets.right=3; panel.add(label, c);
        c.gridx++; c.weightx=1; c.insets.left= 0; c.insets.right=0; panel.add(value, c);
        c.gridy++;
        return value;
    }

    /**
     * Gets the name of the GeoAPI interface implemented by the given object, or
     * {@code null} if none. This is used for providing a value to {@link #type}.
     */
    private static String getTypeName(Class<?> classe) {
        while (classe != null) {
            for (final Class<?> inter : classe.getInterfaces()) {
                if (IdentifiedObject.class.isAssignableFrom(inter)) {
                    return CharSequences.camelCaseToSentence(inter.getSimpleName()).toString();
                }
            }
            classe = classe.getSuperclass();
        }
        return null;
    }

    /**
     * Sets the object to display in this property sheet.
     *
     * @param item The object to display info about.
     */
    public void setIdentifiedObject(final IdentifiedObject item) {
        final Locale locale = getLocale();
        final Identifier oid = item.getName();
        name.setText(oid.getCode());
        final Citation authorityCitation = oid.getAuthority();
        String authorityText = null;
        if (authorityCitation != null) {
            final InternationalString title = authorityCitation.getTitle();
            if (title != null) {
                authorityText = title.toString(locale);
            }
        }
        authority.setText(authorityText);
        final StringBuilder buffer = new StringBuilder();
        for (final Identifier id : item.getIdentifiers()) {
            if (buffer.length() != 0) {
                buffer.append(", ");
            }
            final String codespace = id.getCodeSpace();
            if (codespace != null) {
                buffer.append(codespace).append(':');
            }
            buffer.append(id.getCode());
        }
        identifier.setText(buffer.toString());
        type.setText(getTypeName(item.getClass()));
        InternationalString i18n = item.getRemarks();
        remarks.setText(i18n != null ? i18n.toString(locale) : null);
        /*
         * Set the Well Known Text (WKT) panel using the following steps:
         *
         *  1) Write the warning if there is one.
         *  2) Replace the X3.64 escape sequences by HTML colors.
         *  3) Turn quoted WKT names ("foo") in italic characters.
         */
        buffer.setLength(0);
        buffer.append("<html>");
        String text, warning;
        try {
            text = formatter.format(item);
            Warnings w = formatter.getWarnings();
            warning = (w != null) ? w.toString() : null;
        } catch (RuntimeException e) {
            text = String.valueOf(item.getName());
            warning = e.getLocalizedMessage();
        }
        if (warning != null) {
            buffer.append("<p><b>").append(Vocabulary.getResources(locale).getString(Vocabulary.Keys.Warning))
                    .append(":</b> ").append(warning).append("</p><hr>\n");
        }
        buffer.append("<pre>");
        // '\u001A' is the SUBSTITUTE character. We use it as a temporary replacement for avoiding
        // confusion between WKT quotes and HTML quotes while we search for text to make italic.
        makeItalic(X364.toHTML(text.replace('"', '\u001A')), buffer, '\u001A');
        wktArea.setText(buffer.append("</pre></html>").toString());
    }

    /**
     * Copies the given text in the given buffer, while putting the quoted text in italic.
     * The quote character is given by the {@code quote} argument and will be replaced by
     * the usual {@code "} character.
     */
    private static void makeItalic(final String text, final StringBuilder buffer, final char quote) {
        boolean isQuoting = false;
        int last = 0;
        for (int i=text.indexOf(quote); i>=0; i=text.indexOf(quote, last)) {
            buffer.append(text, last, i).append(isQuoting ? "</cite>\"" : "\"<cite>");
            isQuoting = !isQuoting;
            last = i+1;
        }
        buffer.append(text, last, text.length());
    }

    /**
     * Sets an error message to display instead of the current identified object.
     *
     * @param message The error message.
     */
    public void setErrorMessage(String message) {
        name      .setText(null);
        authority .setText(null);
        identifier.setText(null);
        type      .setText(null);
        remarks   .setText(null);
        final StringBuilder buffer = new StringBuilder(message);
        StringBuilders.replace(buffer, "&", "&amp;");
        StringBuilders.replace(buffer, "<", "&lt;");
        StringBuilders.replace(buffer, ">", "&gt;");
        message = buffer.toString();
        buffer.setLength(0);
        buffer.append("<html><p><b>")
              .append(Vocabulary.getResources(getLocale()).getString(Vocabulary.Keys.Error))
              .append(":</b> ");
        makeItalic(message, buffer, '"');
        wktArea.setText(buffer.append("</p></html>").toString());
    }
}
