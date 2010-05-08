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
import java.util.Locale;
import javax.swing.ComboBoxModel;
import javax.swing.AbstractListModel;

import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;


/**
 * A list of {@link AuthorityCode}s. This implementation will try to fetch the codes only
 * when first needed. Keep in mind that the collection provided to the constructor may be
 * database backed (not a usual implementation from {@code java.util} package), so it is
 * worth to do lazy loading here.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial") // Actually not serializable because AuthorityCode is not.
final class AuthorityCodeList extends AbstractListModel implements ComboBoxModel {
    /**
     * The authority factory, or {@code null} if disposed. The disposal occurs after
     * the first iteration has been completed. All other iterations will use the
     * {@link #extracted} cached values.
     */
    private AuthorityFactory factory;

    /**
     * The collection of authority codes, or {@code null} if disposed. The disposal
     * occurs after the first iteration has been completed. All other iterations will
     * use the {@link #extracted} cached values.
     */
    private List<Collection<String>> codes;

    /**
     * An iterator over the codes, or {@code null} if not yet created or disposed.
     */
    private Iterator<String> iterator;

    /**
     * The authority codes as {@link AuthorityCode} objects.
     */
    private final List<AuthorityCode> extracted = new ArrayList<AuthorityCode>();

    /**
     * The locale for formatting the code descriptions.
     */
    private final Locale locale;

    /**
     * The selected item, or {@code null} if none.
     */
    private AuthorityCode selected;

    /**
     * The number of elements in this list. This count may be incomplete if there
     * is some remaining elements in the {@link #codes} list.
     */
    private int size;

    /**
     * Creates a list for the given codes.
     *
     * @param The locale for formatting the code descriptions.
     * @param factory The factory to use for fetching the codes.
     * @param type Base classes of CRS objects to extract. Must have at least one element.
     * @param dimension Dimension of CRS objects, or 0 if no restriction.
     */
    public AuthorityCodeList(final Locale locale, final AuthorityFactory factory,
            final Class<? extends IdentifiedObject>... types) throws FactoryException
    {
        this.locale = locale;
        this.factory = factory;
        codes = new ArrayList<Collection<String>>(types.length);
        for (final Class<? extends IdentifiedObject> type : types) {
            codes.add(factory.getAuthorityCodes(type));
        }
    }

    /**
     * Returns the length of the list. If list of extracted codes is complete, we return
     * the length of that list. Otherwise we need to compute the length of each pending
     * collection.
     */
    @Override
    public int getSize() {
        int n = size;
        if (codes != null) {
            for (final Collection<String> ci : codes) {
                n += ci.size();
            }
        }
        return n;
    }

    /**
     * Returns the value at the specified index. This method will extract only the minimal
     * amount of elements from the {@link #codes} collection when first needed, and caches
     * the extracted elements for future reuse.
     */
    @Override
    public AuthorityCode getElementAt(final int index) {
        if (codes != null) {
            int n;
            while (index >= (n = extracted.size())) {
                if (iterator == null) {
                    iterator = codes.get(0).iterator();
                }
                if (iterator.hasNext()) {
                    extracted.add(new AuthorityCode(factory, iterator.next(), n, locale));
                } else {
                    size = n;
                    iterator = null;
                    codes.remove(0);
                    if (codes.isEmpty()) {
                        codes   = null;
                        factory = null;
                        break;
                    }
                }
            }
        }
        return (index >= 0 && index < extracted.size()) ? extracted.get(index) : null;
    }

    /**
     * Returns the selected item.
     */
    @Override
    public AuthorityCode getSelectedItem() {
        return selected;
    }

    /**
     * Sets the selected item.
     */
    @Override
    public void setSelectedItem(final Object code) {
        selected = (AuthorityCode) code;
        int index = selected.index;
        fireContentsChanged(this, index, index);
    }
}
