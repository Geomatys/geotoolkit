/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.referencing.rs;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import org.apache.sis.referencing.gazetteer.ReferencingByIdentifiers;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.SingleCRS;


/**
 * A <abbr>RS</abbr> describing the position of points through two or more independent <abbr>RS</abbr>s.
 * Two <abbr>RS</abbr>s are independent of each other if coordinate values in one cannot be converted or
 * transformed into coordinate values in the other.
 *
 * <p>This interface is a generalized version of CompoundCRS.</p>
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CompoundRS extends ReferenceSystem {
    /**
     * Returns the ordered list of <abbr>RS</abbr> components.
     * The returned list may contain nested compound <abbr>RS</abbr>.
     * For a list without nesting, as required by ISO 19111, see {@link #getSingleComponents()}.
     *
     * @return the ordered list of components of this compound <abbr>RS</abbr>.
     */
    List<ReferenceSystem> getComponents();

    /**
     * Returns the ordered list of <abbr>CRS</abbr> components, none of which itself compound.
     * If this compound <abbr>CRS</abbr> contains nested compound <abbr>CRS</abbr> components,
     * then those components are flattened recursively in a sequence of {@link SingleCRS} objects.
     *
     * @return the ordered list of components of this compound <abbr>CRS</abbr>, none of which itself compound.
     *
     * @since 3.1
     */
    default List<ReferenceSystem> getSingleComponents() {
        var singles = new ArrayList<ReferenceSystem>(5);
        flatten(singles, new LinkedList<>());   // Linked list is cheap to construct and efficient with 0 or 1 element.
        return Collections.unmodifiableList(singles);
    }

    /**
     * Appends recursively all single components in the given list.
     *
     * @param  singles  the list where to add single components.
     * @param  safety   a safety against infinite recursive method calls.
     * @throws IllegalStateException if recursive components are detected.
     */
    private void flatten(final List<ReferenceSystem> singles, final List<Object> safety) {
        for (ReferenceSystem rs : getComponents()) {
            if (rs instanceof SingleCRS) {
                singles.add((SingleCRS) rs);
            } else  if (rs instanceof ReferencingByIdentifiers) {
                singles.add(rs);
            } else if (rs instanceof CompoundRS) {
                final CompoundRS r = (CompoundRS) rs;
                for (Object previous : safety) {
                    if (previous == this) {
                        throw new IllegalStateException("Recursive components detected.");
                    }
                }
                safety.add(this);
                singles.addAll(r.getSingleComponents());

            } else if (rs instanceof CompoundCRS) {
                final CompoundCRS r = (CompoundCRS) rs;
                //todo CompoundCRS should be a CompoundRS
                for (Object previous : safety) {
                    if (previous == this) {
                        throw new IllegalStateException("Recursive components detected.");
                    }
                }
                safety.add(this);
                singles.addAll(r.getSingleComponents());
            } else {
                throw new IllegalArgumentException("Unknown reference system type : " + rs.getName());
            }
        }
    }

}
