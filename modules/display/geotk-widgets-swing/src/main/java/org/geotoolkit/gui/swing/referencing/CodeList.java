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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.ComboBoxModel;
import javax.swing.AbstractListModel;

import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;


/**
 * A list of {@link Code}s. This implementation will try to fetch the codes only when
 * first needed. Keep in mind that the collection provided to the constructor may be
 * database backed (not a usual implementation from {@code java.util} package), so it
 * is worth to do lazy loading here.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial") // Actually not serializable because Code is not.
final class CodeList extends AbstractListModel implements ComboBoxModel {
    /**
     * The authority factory, or {@code null} if disposed.
     */
    private AuthorityFactory factory;

    /**
     * The collection of authority codes, or {@code null} if disposed.
     */
    private Collection<String> codes;

    /**
     * An iterator over the codes, or {@code null} if not yet created or disposed.
     */
    private Iterator<String> iterator;

    /**
     * The authority codes as {@link Code} objects.
     */
    private final List<Code> extracted = new ArrayList<Code>();

    /**
     * The selected item, or {@code null} if none.
     */
    private Code selected;

    /**
     * Creates a list for the given codes.
     */
    public CodeList(final AuthorityFactory factory, final Class<? extends IdentifiedObject> type)
            throws FactoryException
    {
        this.factory  = factory;
        this.codes    = factory.getAuthorityCodes(type);
        if (!codes.isEmpty()) {
            // Note: testing if (!isEmpty()) is much facter than if (size() > 0)
            selected = getElementAt(0);
        }
    }

    /**
     * Returns the length of the list.
     */
    @Override
    public int getSize() {
        return (codes != null) ? codes.size() : extracted.size();
    }

    /**
     * Returns the value at the specified index.
     */
    @Override
    public Code getElementAt(final int index) {
        if (codes != null) {
            if (iterator == null) {
                iterator = codes.iterator();
            }
            int size;
            while (index >= (size = extracted.size())) {
                if (!iterator.hasNext()) {
                    assert extracted.size() == codes.size();
                    iterator = null;
                    codes    = null;
                    factory  = null;
                    break;
                }
                extracted.add(new Code(factory, iterator.next(), size));
            }
        }
        return (index >= 0 && index < extracted.size()) ? extracted.get(index) : null;
    }

    /**
     * Returns the selected item.
     */
    @Override
    public Code getSelectedItem() {
        return selected;
    }

    /**
     * Sets the selected item.
     */
    @Override
    public void setSelectedItem(final Object code) {
        selected = (Code) code;
        int index = selected.index;
        fireContentsChanged(this, index, index);
    }

    /**
     * Returns an item that can be used as a prototype in a combo box. This operation is
     * expansive and should be run only once.
     *
     * @see javax.swing.JComboBox#setPrototypeDisplayValue
     */
    public String getPrototypeItem() {
        final int size = getSize();
        String prototype = "";
        for (int i=0; i<size; i++) {
            final String candidate = getElementAt(i).toString();
            if (candidate.length() > prototype.length()) {
                prototype = candidate;
            }
        }
        return prototype;
    }
}
