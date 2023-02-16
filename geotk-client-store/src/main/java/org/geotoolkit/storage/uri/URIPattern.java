/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.storage.uri;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.storage.multires.TileMatrix;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * URI pattern resolving utility class.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class URIPattern {

    private static final String PATTERN_X = "{x}";
    private static final String PATTERN_REVERSEX = "{reversex}";
    private static final String PATTERN_Y = "{y}";
    private static final String PATTERN_REVERSEY = "{reversey}";

    private static final String PATTERN_CRS_EPSG = "{crs.epsg}";
    //TODO private static final String PATTERN_Z_RESOLUTION = "{z.resolution}";
    private static final String PATTERN_Z_LEVEL = "{z.level}";
    private static final String PATTERN_X_COLUMN = "{x.column}";
    private static final String PATTERN_Y_ROW = "{y.row}";
    private static final String PATTERN_Y_ROW_REVERSE = "{y.row.reverse}";

    private final URI base;
    private final String pattern;

    URIPattern(URI base, String pattern) {
        this.base = base;
        this.pattern = pattern;
    }

    /**
     * @return true is crs element is defined in the pattern.
     */
    public boolean isCrsDefined() {
        return pattern.contains(PATTERN_CRS_EPSG);
    }

    /**
     * Resolve final URI from knowned base and given parameters.
     *
     * @param crs not null
     * @param z not null
     * @param x not null
     * @param y not null
     * @return final resolved URI
     * @throws FactoryException
     */
    public URI resolve(CoordinateReferenceSystem crs, Number z, Number x, Number y) throws FactoryException {
        final Integer epsg = IdentifiedObjects.lookupEPSG(crs);
        String path = pattern;
        path = path.replace(PATTERN_CRS_EPSG, Integer.toString(epsg));
        path = path.replace(PATTERN_X_COLUMN, "" + x);
        path = path.replace(PATTERN_Y_ROW, "" + y);
        path = path.replace(PATTERN_Z_LEVEL, "" + z);
        path = path.replace('/', File.separatorChar);
        return base.resolve(path);
    }

    public static Path resolve(Path base, TileMatrix tilematrix, String pattern, long x, long y) {
        return Paths.get(resolve(base.toUri(), tilematrix, pattern, x, y));
    }

    public static URI resolve(URI base, TileMatrix tilematrix, String pattern, long x, long y) {
        //check for windows notation
        pattern = pattern.replace('\\', '/');
        //remove possible leading slash
        if (pattern.startsWith("/")) {
            pattern = pattern.substring(1);
        }

        //replace coordinate
        pattern = pattern.replace(PATTERN_X, "" + x);
        pattern = pattern.replace(PATTERN_Y, "" + y);
        //reverse values
        final GridExtent extent = tilematrix.getTilingScheme().getExtent();
        long reverseX = extent.getSize(0) - 1 - x;
        long reverseY = extent.getSize(1) - 1 - y;
        pattern = pattern.replace(PATTERN_REVERSEX, "" + reverseX);
        pattern = pattern.replace(PATTERN_REVERSEY, "" + reverseY);

        //resolve path
        if (base.isOpaque()) {
            //we dont know how this system works, we will assume it is similar to a normal path
            try {
                return new URI(base.getScheme(), base.getSchemeSpecificPart() + pattern, base.getFragment());
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Unsupported base URI " + base);
            }
        } else {
            URI path = base;
            final String[] parts = pattern.split("/");
            for (int i = 0; i < parts.length; i++) {
                if (i != parts.length-1) {
                    path = path.resolve(parts[i] + "/");
                } else {
                    path = path.resolve(parts[i]);
                }
            }
            return path;
        }
    }

}
