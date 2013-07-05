/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.lang.reflect.UndeclaredThrowableException;

import org.opengis.util.FactoryException;

import org.geotoolkit.util.Utilities;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.Classes;


/**
 * Base class for loaders of {@link GridTransform2D} data.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
class GridLoader {
    /**
     * The cache of grids loaded so far. Keys are instances of the exact {@code GridLoader} class,
     * which values are instances of subclasses. Grids are retained by soft references only.
     */
    private static final Cache<GridLoader,GridLoader> CACHE = new Cache<>(4, 0, true);

    /**
     * The type of the grid loader. This is not necessarily the same than
     * {@link Object#getClass()}, since this {@code GridLoader} instance
     * may be used as a key for an other {@code GridLoader} instance of
     * that class.
     */
    private final Class<? extends GridLoader> type;

    /**
     * Longitude and latitude grid shift file names. The object type can be either
     * {@link File} or {@link URL}. For NADCON grids, the longitude and latitude
     * grids are two distinct files. For NTv2 grids, they are the same file.
     */
    Object longitudeGridFile, latitudeGridFile;

    /**
     * Creates a new grid loader of the given type.
     *
     * @param type The type of the grid loader.
     */
    GridLoader(final Class<? extends GridLoader> type) {
        this.type = type;
    }

    /**
     * If a loader of the given type is cached for the given files, returns that loader.
     * Otherwise creates a new loader and caches it for future reuse.
     *
     * @param  <T>               The grid loader type.
     * @param  type              The grid loader type.
     * @param  longitudeGridFile The file with longitude data.
     * @param  latitudeGridFile  The file with latitude data. May be the same than the longitude file.
     * @param  creator           The method to call if a new grid needs to be loaded.
     * @return The cached or the newly created loader.
     * @throws FactoryException If an error occurred while creating the loader.
     */
    protected static <T extends GridLoader> T loadIfAbsent(final Class<T> type,
            final Object longitudeGridFile, final Object latitudeGridFile,
            final Callable<T> creator) throws FactoryException
    {
        final GridLoader key = new GridLoader(type);
        key.longitudeGridFile = longitudeGridFile;
        key.latitudeGridFile  = latitudeGridFile;
        final GridLoader grid;
        try {
            grid = CACHE.getOrCreate(key, creator);
        } catch (FactoryException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UndeclaredThrowableException(e);
        }
        assert key.equals(grid);
        return type.cast(grid);
    }

    /**
     * Returns {@code true} if the given object is equals to the given {@code GridLoader}.
     * Only the source files and the type are compared. All other fields defined in subclasses
     * are considered derived informations.
     */
    @Override
    public final boolean equals(final Object other) {
        if (other instanceof GridLoader) {
            final GridLoader that = (GridLoader) other;
            return Objects.equals(type, that.type) &&
                   Objects.equals(longitudeGridFile, that.longitudeGridFile) &&
                   Objects.equals(latitudeGridFile,  that.latitudeGridFile);
        }
        return false;
    }

    /**
     * Returns a hash code value for this grid loader.
     */
    @Override
    public final int hashCode() {
        return Utilities.hash(longitudeGridFile, Utilities.hash(latitudeGridFile, type.hashCode()));
    }

    /**
     * Returns a string representation of this key.
     */
    @Override
    public final String toString() {
        return Classes.getShortName(type) + "[\"" + longitudeGridFile + "\",\"" + latitudeGridFile + "\"]";
    }
}
