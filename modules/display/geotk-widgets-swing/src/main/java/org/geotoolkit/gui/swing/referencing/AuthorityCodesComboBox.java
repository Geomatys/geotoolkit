/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.referencing;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ComboBoxModel;
import javax.swing.JInternalFrame;
import javax.swing.DefaultComboBoxModel;

import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.Citations;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.referencing.factory.FallbackAuthorityFactory;
import org.geotoolkit.gui.swing.IconFactory;


/**
 * A combox box for selecting a coordinate reference system from a list. This component also
 * provides a search button (for filtering the CRS name that contain the specified keywords)
 * and a info button displaying the CRS {@linkplain PropertiesSheet properties sheet}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class AuthorityCodesComboBox extends JComponent {
    /**
     * The authority factory responsible for creating objects from a list of codes.
     */
    private final AuthorityFactory factory;

    /**
     * The list of authority codes, as a combo box model.
     */
    private final AuthorityCodeList codeList;

    /**
     * The list of CRS objects.
     */
    private final JComboBox list;

    /**
     * The text field for searching item.
     */
    private final JTextField search;

    /**
     * The {@link #search} or {@link #list} field.
     */
    private final JPanel searchOrList;

    /**
     * The card layout showing either {@link #list} or {@link #search}.
     */
    private final CardLayout cards;

    /**
     * The button to press for showing properties.
     */
    private final JButton showProperties;

    /**
     * Info about the currently selected item.
     */
    private PropertiesSheet properties;

    /**
     * The window that contains {@link #properties}.
     */
    private Component propertiesWindow;

    /**
     * Creates a CRS chooser backed by the EPSG authority factory.
     *
     * @throws FactoryRegistryException if no EPSG authority factory has been found.
     * @throws FactoryException if the factory can't provide CRS codes.
     */
    public AuthorityCodesComboBox() throws FactoryRegistryException, FactoryException {
        this("EPSG");
    }

    /**
     * Creates a CRS chooser backed by the specified authority factory.
     *
     * @param  authority The authority identifier (e.g. {@code "EPSG"}).
     * @throws FactoryRegistryException if no authority factory has been found.
     * @throws FactoryException if the factory can't provide CRS codes.
     *
     * @since 2.4
     */
    public AuthorityCodesComboBox(final String authority)
            throws FactoryRegistryException, FactoryException
    {
        this(FallbackAuthorityFactory.create(CRSAuthorityFactory.class,
             filter(AuthorityFactoryFinder.getCRSAuthorityFactories(null), authority)));
    }

    /**
     * Returns a collection containing only the factories of the specified authority.
     */
    private static Collection<CRSAuthorityFactory> filter(
            final Collection<? extends CRSAuthorityFactory> factories, final String authority)
    {
        final List<CRSAuthorityFactory> filtered = new ArrayList<CRSAuthorityFactory>();
        for (final CRSAuthorityFactory factory : factories) {
            if (Citations.identifierMatches(factory.getAuthority(), authority)) {
                filtered.add(factory);
            }
        }
        return filtered;
    }

    /**
     * Creates a CRS chooser backed by the specified authority factory.
     *
     * @param  factory The authority factory responsible for creating objects from a list of codes.
     * @throws FactoryException if the factory can't provide CRS codes.
     */
    @SuppressWarnings("unchecked")
    public AuthorityCodesComboBox(final AuthorityFactory factory) throws FactoryException {
        this(factory, CoordinateReferenceSystem.class);
    }

    /**
     * Creates a CRS chooser backed by the specified authority factory.
     *
     * @param  factory The authority factory responsible for creating objects from a list of codes.
     * @param  types The types of CRS object to includes in the list.
     * @throws FactoryException if the factory can't provide CRS codes.
     */
    public AuthorityCodesComboBox(final AuthorityFactory factory,
            Class<? extends CoordinateReferenceSystem>... types) throws FactoryException
    {
        this.factory = factory;
        final Locale locale = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);

        setLayout(new BorderLayout());
        cards        = new CardLayout();
        searchOrList = new JPanel(cards);
        codeList     = new AuthorityCodeList(locale, factory, types);
        list         = new JComboBox(codeList);
        search       = new JTextField();
        search.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                search(false);
            }
        });
        list.setRenderer(new AuthorityCodeRenderer(list.getRenderer()));
        list.setPrototypeDisplayValue(new AuthorityCode());
        searchOrList.add(list,   "List");
        searchOrList.add(search, "Search");
        add(searchOrList, BorderLayout.CENTER);
        /*
         * Adds the "Info" button.
         */
        JButton button;
        final Dimension size = new Dimension(24, 20);
        final IconFactory icons = IconFactory.DEFAULT;
        String label = resources.getString(Vocabulary.Keys.INFORMATIONS);
        button = icons.getButton("toolbarButtonGraphics/general/Information16.gif", label, label);
        button.setFocusable(false);
        button.setPreferredSize(size);
        button.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                showProperties();
            }
        });
        showProperties = button;
        /*
         * Adds the "Search" button.
         */
        label = resources.getString(Vocabulary.Keys.SEARCH);
        button = icons.getButton("toolbarButtonGraphics/general/Find16.gif", label, label);
        button.setFocusable(false);
        button.setPreferredSize(size);
        button.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
                search(true);
            }
        });
        /*
         * Add the two buttons after the combo box.
         */
        final Box box = Box.createHorizontalBox();
        box.add(button);
        box.add(showProperties);
        add(box, BorderLayout.LINE_END);
    }

    /**
     * Returns the authority name. Useful for providing a window title for example.
     *
     * @return The current authority name.
     */
    public String getAuthority() {
        return factory.getAuthority().getTitle().toString(getLocale());
    }

    /**
     * Returns the code for the selected object, or {@code null} if none.
     *
     * @return The code of the currently selected object.
     */
    public String getSelectedCode() {
        final AuthorityCode code = (AuthorityCode) list.getModel().getSelectedItem();
        return (code != null) ? code.code : null;
    }

    /**
     * Returns the selected object, usually as a {@link CoordinateReferenceSystem}.
     *
     * @return The currently selected object.
     * @throws FactoryException if the factory can't create the selected object.
     */
    public IdentifiedObject getSelectedItem() throws FactoryException {
        final String code = getSelectedCode();
        return (code != null) ? factory.createObject(code) : null;
    }

    /**
     * Display information about the currently selected item in a separated window.
     * The default implementation show the <cite>Well Know Text</cite>.
     */
    public void showProperties() {
        if (properties == null) {
            properties = new PropertiesSheet();
        }
        IdentifiedObject item;
        try {
            item = getSelectedItem();
        } catch (FactoryException e) {
            String message = e.getLocalizedMessage();
            if (message == null) {
                message = Classes.getShortClassName(e);
            }
            properties.setErrorMessage(message);
            return;
        }
        final String title = item.getName().getCode();
        if (propertiesWindow == null) {
            propertiesWindow = SwingUtilities.toFrame(this, properties, title, null);
            if (propertiesWindow instanceof JFrame) {
                ((JFrame) propertiesWindow).setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            } else if (propertiesWindow instanceof JInternalFrame) {
                ((JInternalFrame) propertiesWindow).setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
            }
            propertiesWindow.setSize(600, 500);
        } else {
            SwingUtilities.setTitle(propertiesWindow, title);
        }
        properties.setIdentifiedObject(item);
        propertiesWindow.setVisible(true);
    }

    /**
     * Enables or disables the search field.
     */
    private void search(final boolean enable) {
        final JComponent component;
        final String name;
        if (enable) {
            component = search;
            name = "Search";
        } else {
            component = list;
            name = "List";
            filter(search.getText());
        }
        showProperties.setEnabled(!enable);
        cards.show(searchOrList, name);
        component.requestFocus();
    }

    /**
     * Displays only the CRS name that contains the specified keywords. The {@code keywords}
     * argument is a space-separated list, usually provided by the user after he pressed the
     * "Search" button.
     *
     * @param keywords space-separated list of keywords to look for.
     */
    public void filter(String keywords) {
        ComboBoxModel model = codeList;
        if (keywords != null && ((keywords = keywords.trim()).length()) != 0) {
            /*
             * Quotes the keywords, except the spaces. Set the 'if' value to
             * 'false' if the user already provided a regular expression.
             */
            if (true) {
                final StringBuilder buffer = new StringBuilder(".*");
                for (final String token : keywords.split("\\s+")) {
                    buffer.append(Pattern.quote(token)).append(".*");
                }
                keywords = buffer.toString();
            }
            Matcher matcher = null;
            final DefaultComboBoxModel filtered = new DefaultComboBoxModel();
            final int size = codeList.getSize();
            for (int i=0; i<size; i++) {
                final AuthorityCode code = codeList.getElementAt(i);
                final String name = code.toString();
                if (matcher == null) {
                    matcher = Pattern.compile(keywords, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(name);
                } else {
                    matcher.reset(name);
                }
                if (matcher.matches()) {
                    filtered.addElement(code);
                } else {
                    matcher.reset(code.code);
                    if (matcher.matches()) {
                        filtered.addElement(code);
                    }
                }
            }
            model = filtered;
        }
        list.setModel(model);
    }
}
