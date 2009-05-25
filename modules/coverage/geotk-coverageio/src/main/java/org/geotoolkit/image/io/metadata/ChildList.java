/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.metadata;

import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.geotoolkit.resources.Errors;


/**
 * A list of child elements, for example {@code <SampleDimensions>} or {@code <Axis>}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
abstract class ChildList<T extends MetadataAccessor> extends MetadataAccessor {
    /**
     * The list of childs.
     */
    private final List<T> childs;

    /**
     * Creates a parser for childs. The arguments are given unchanged to the
     * {@linkplain MetadataAccessor#MetadataAccessor super-class constructor}.
     *
     * @param  metadata   The metadata node.
     * @param  parentPath The path to the {@linkplain Node node} of interest, or {@code null}
     *                    if {@code metadata} is directly the node of interest.
     * @param  childPath  The path (relative to {@code parentPath}) to the child
     *                    {@linkplain Element elements}, or {@code null} if none.
     */
    protected ChildList(final GeographicMetadata metadata,
            final String parentPath, final String childPath)
    {
        super(metadata, parentPath, childPath);
        final int count = childCount();
        childs = new ArrayList<T>(count != 0 ? count : 4);
    }

    /**
     * Returns the child at the specified index.
     *
     * @param  index the child index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     */
    public T getChild(final int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= childCount()) {
            throw new IndexOutOfBoundsException(Errors.format(outOfBounds(), index));
        }
        while (childs.size() <= index) {
            childs.add(null);
        }
        T candidate = childs.get(index);
        if (candidate == null) {
            candidate = newChild(index);
            childs.set(index, candidate);
        }
        return candidate;
    }

    /**
     * Creates a new child, append to the list and returns it.
     */
    public T addChild() {
        final int index = appendChild();
        final T candidate = newChild(index);
        assert index == childs.size();
        childs.add(candidate);
        return candidate;
    }

    /**
     * Creates a new child at the specified index.
     */
    protected abstract T newChild(int index);

    /**
     * Returns the key for "out of range" error localization.
     */
    int outOfBounds() {
        return Errors.Keys.INDEX_OUT_OF_BOUNDS_$1;
    }

    /**
     * A list of {@linkplain Band bands}.
     */
    static final class Bands extends ChildList<Band> {
        /** Creates a parser for bands. */
        public Bands(final GeographicMetadata metadata) {
            super(metadata, "rectifiedGridDomain/rangeSet/bands", "band");
        }

        /** Creates a new band. */
        @Override
        protected Band newChild(final int index) {
            return new Band(this, index);
        }

        /** Returns the key for "out of range" error localization. */
        @Override
        int outOfBounds() {
            return Errors.Keys.BAD_BAND_NUMBER_$1;
        }
    }

    /**
     * A list of {@linkplain Axis axis}.
     */
    static final class Axes extends ChildList<Axis> {
        /** Creates a parser for axis. */
        public Axes(final GeographicMetadata metadata) {
            super(metadata, "rectifiedGridDomain/crs/cs", "axis");
        }

        /** Creates a new axis. */
        @Override
        protected Axis newChild(final int index) {
            return new Axis(this, index);
        }
    }

    /**
     * A list of {@linkplain Parameter parameters}.
     */
    static final class Parameters extends ChildList<Parameter> {
        /** Creates a parser for parameters. */
        public Parameters(final GeographicMetadata metadata) {
            super(metadata, "rectifiedGridDomain/crs/projection", "parameter");
        }

        /** Creates a new parameter. */
        @Override
        protected Parameter newChild(final int index) {
            return new Parameter(this, index);
        }
    }
}
