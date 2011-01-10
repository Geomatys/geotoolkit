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

import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSPointArray;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.Precision;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class JTSPositionFactory implements PositionFactory {

	private final CoordinateReferenceSystem crs;
	
	/**
	 * No argument constructor for the plugin system.
	 */
	public JTSPositionFactory(){
	    this( DefaultGeographicCRS.WGS84);
	}
	public JTSPositionFactory( final CoordinateReferenceSystem crs ){
		this.crs = crs;
	}
    
    @Override
	public DirectPosition createDirectPosition(final double[] ordinates)
			throws MismatchedDimensionException {
        GeneralDirectPosition position = new GeneralDirectPosition(ordinates);
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

	public Precision getPrecision() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public PointArray createPointArray(final float[] array, final int start, final int end) {
		PointArray pointArray = (PointArray) createPointArray();
		int D = crs.getCoordinateSystem().getDimension();
		if (D == 2) {
			for (int i = start; i < end; i += D) {
				double[] ordinates = new double[] { array[i], array[i + 1] };
                GeneralDirectPosition pos = new GeneralDirectPosition(ordinates);
                pos.setCoordinateReferenceSystem(crs);
				pointArray.add(pos);
			}
		} else if (D == 3) {
			for (int i = start; i < end; i += D) {
				double[] ordinates = new double[] { array[i], array[i + 1],
						array[i + 2] };
                GeneralDirectPosition pos = new GeneralDirectPosition(ordinates);
                pos.setCoordinateReferenceSystem(crs);
				pointArray.add(pos);
			}
		} else {
			for (int i = start; i < end; i += D) {
				double[] ordinates = new double[D];
				for (int o = 0; i < D; i++) {
					ordinates[o] = array[i + o];
				}
                GeneralDirectPosition pos = new GeneralDirectPosition(ordinates);
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
		double[] ordinates = new double[n]; 
		for (int i=start; i<array.length && i <= end; i += n) {
		    for ( int j = i; j < i + n; j++ ) {
		        ordinates[j-i] = array[j]; 
		    }
		    
			pointArray.add(createDirectPosition(ordinates));
		}
		return pointArray;
	}
	public PointArray createPointArray() {
		return new JTSPointArray(crs);
	}

}
