/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.data.mapinfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.operation.builder.LinearTransformBuilder;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Static;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.util.FactoryException;

/**
 *
 * References :
 * https://en.wikipedia.org/wiki/MapInfo_TAB_format
 * https://georezo.net/jparis/mb_r/doc/Tab_file_format/tab_file_format.htm
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TabUtils extends Static {

    private TabUtils(){}

    /**
     * Parse raster tab format and return a GridGeometry
     * with CRS and GridToCrs transform properties.
     * @param path file to parse
     * @return parsed GridGeometry
     */
    public static GridGeometry parseGridGeometry(Path path) throws IOException, DataStoreException, FactoryException {
        ArgumentChecks.ensureNonNull("path", path);
        final List<String> lines = Files.readAllLines(path);

        //find Type "RASTER" delimiter
        final List<double[]> mappping = new ArrayList<>();
        String crsStr = null;
        String unitStr = null;
        for (String line : lines) {
            if (line.startsWith("(")) {
                String[] parts = line.split(" ");
                parts[0] = parts[0].trim();
                parts[1] = parts[1].trim();
                final double[] map = new double[4];
                final int split1 = parts[0].indexOf(',');
                final int split2 = parts[1].indexOf(',');
                map[0] = Double.parseDouble(parts[0].substring(1, split1));
                map[1] = Double.parseDouble(parts[0].substring(split1+1, parts[0].length()-1));
                map[2] = Double.parseDouble(parts[1].substring(1, split2));
                map[3] = Double.parseDouble(parts[1].substring(split2+1, parts[1].length()-1));

                mappping.add(map);
            } else if (line.startsWith("Units")) {
                unitStr = line.substring(5).trim();
            } else if (line.startsWith("CoordSys")) {
                crsStr = line;
            }
        }

        if (mappping.isEmpty()) {
            throw new IOException("Raster mapping points not found.");
        } else if (crsStr == null) {
            throw new IOException("Coordinate system field not found.");
        }

        final LinearTransformBuilder ltb = new LinearTransformBuilder();
        for (double[] map : mappping) {
            ltb.setControlPoint(new int[]{(int) map[2], (int) map[3]}, new double[]{map[0], map[1]});
        }
        final LinearTransform gridToCrs = ltb.create(null);
        final CoordinateReferenceSystem crs = ProjectionUtils.buildCRSFromMIF(crsStr);
        return new GridGeometry(null, PixelInCell.CELL_CENTER, gridToCrs, crs);
    }
}
