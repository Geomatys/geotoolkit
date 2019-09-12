/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts.spatialschema;

import java.util.List;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPointArray;
import org.apache.sis.referencing.CommonCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class JTSPositionFactory implements PositionFactory {

    private final CoordinateReferenceSystem crs;

    /**
     * No argument constructor for the plugin system.
     */
    public JTSPositionFactory(){
        this( CommonCRS.WGS84.normalizedGeographic());
    }
    public JTSPositionFactory( final CoordinateReferenceSystem crs ){
        this.crs = crs;
    }

    @Override
    public DirectPosition createDirectPosition(final double[] coordinates)
            throws MismatchedDimensionException {
        GeneralDirectPosition position = new GeneralDirectPosition(coordinates);
        position.setCoordinateReferenceSystem(crs);
        return position;
    }

    @Override
    public Position createPosition(final Position position) {
        return new GeneralDirectPosition(position.getDirectPosition());
    }

    public List createPositionList() {
        return new JTSPointArray( crs );
    }

    public List createPositionList(final double[] coordinates, final int start, final int end) {
        PointArray array = new JTSPointArray( crs );
        int N = crs.getCoordinateSystem().getDimension();
        for( int i=start; i < end ; i += N ){
            double[] ords = new double[N];
            System.arraycopy( coordinates, i, ords, 0, N );
            array.add( createDirectPosition( ords ));
        }
        return array;
    }

    public List createPositionList(final float[] coordinates, final int start, final int end) {
        PointArray array = new JTSPointArray( crs );
        int N = crs.getCoordinateSystem().getDimension();
        for( int i=start; i < end ; i += N ){
            double[] ords = new double[N];
            System.arraycopy( coordinates, i, ords, 0, N );
            array.add( createDirectPosition( ords ));
        }
        return array;
    }

    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    public PointArray createPointArray(final float[] array, final int start, final int end) {
        PointArray pointArray = (PointArray) createPointArray();
        int D = crs.getCoordinateSystem().getDimension();
        if (D == 2) {
            for (int i = start; i < end; i += D) {
                double[] coordinates = new double[] { array[i], array[i + 1] };
                GeneralDirectPosition pos = new GeneralDirectPosition(coordinates);
                pos.setCoordinateReferenceSystem(crs);
                pointArray.add(pos);
            }
        } else if (D == 3) {
            for (int i = start; i < end; i += D) {
                double[] coordinates = new double[] { array[i], array[i + 1],
                        array[i + 2] };
                GeneralDirectPosition pos = new GeneralDirectPosition(coordinates);
                pos.setCoordinateReferenceSystem(crs);
                pointArray.add(pos);
            }
        } else {
            for (int i = start; i < end; i += D) {
                double[] coordinates = new double[D];
                for (int o = 0; i < D; i++) {
                    coordinates[o] = array[i + o];
                }
                GeneralDirectPosition pos = new GeneralDirectPosition(coordinates);
                pos.setCoordinateReferenceSystem(crs);
                pointArray.add(pos);
            }
        }
        return pointArray;
    }

    public PointArray createPointArray(final double[] array,
            final int start, final int end) {
        PointArray pointArray = (PointArray) createPointArray();
        int n = crs.getCoordinateSystem().getDimension();
        double[] coordinates = new double[n];
        for (int i=start; i<array.length && i <= end; i += n) {
            for ( int j = i; j < i + n; j++ ) {
                coordinates[j-i] = array[j];
            }

            pointArray.add(createDirectPosition(coordinates));
        }
        return pointArray;
    }
    public PointArray createPointArray() {
        return new JTSPointArray(crs);
    }

}
