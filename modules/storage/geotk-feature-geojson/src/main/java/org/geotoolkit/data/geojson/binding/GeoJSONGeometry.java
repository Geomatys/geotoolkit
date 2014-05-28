/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson.binding;

import org.geotoolkit.data.geojson.utils.GeoJSONTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONGeometry extends GeoJSONObject implements Serializable {

    public GeoJSONGeometry() {
    }

    /**
     * POINT
     */
    public static class GeoJSONPoint extends GeoJSONGeometry {

        private double[] coordinates = null;

        public GeoJSONPoint() {
            setType(GeoJSONTypes.POINT);
        }

        public double[] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[] coordinates) {
            this.coordinates = coordinates;
        }

    }

    /**
     * MULTI-POINT
     */
    public static class GeoJSONMultiPoint extends GeoJSONGeometry {

        private double[][] coordinates = null;

        public GeoJSONMultiPoint() {
            setType(GeoJSONTypes.MULTI_POINT);
        }

        public double[][] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[][] coordinates) {
            this.coordinates = coordinates;
        }
    }

    /**
     * LINESTRING
     */
    public static class GeoJSONLineString extends GeoJSONGeometry {

        private double[][] coordinates = null;

        public GeoJSONLineString() {
            setType(GeoJSONTypes.LINESTRING);
        }

        public double[][] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[][] coordinates) {
            this.coordinates = coordinates;
        }
    }

    /**
     * MULTI-LINESTRING
     */
    public static class GeoJSONMultiLineString extends GeoJSONGeometry {

        private double[][][] coordinates = null;

        public GeoJSONMultiLineString() {
            setType(GeoJSONTypes.MULTI_LINESTRING);
        }

        public double[][][] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[][][] coordinates) {
            this.coordinates = coordinates;
        }
    }

    /**
     * POLYGON
     */
    public static class GeoJSONPolygon extends GeoJSONGeometry {

        private double[][][] coordinates = null;

        public GeoJSONPolygon() {
            setType(GeoJSONTypes.POLYGON);
        }

        public double[][][] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[][][] coordinates) {
            this.coordinates = coordinates;
        }
    }

    /**
     * MULTI-POLYGON
     */
    public static  class GeoJSONMultiPolygon extends GeoJSONGeometry {

        private double[][][][] coordinates = null;

        public GeoJSONMultiPolygon() {
            setType(GeoJSONTypes.MULTI_POLYGON);
        }

        public double[][][][] getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(double[][][][] coordinates) {
            this.coordinates = coordinates;
        }
    }

    /**
     * GEOMETRY-COLLECTION
     */
    public static class GeoJSONGeometryCollection extends GeoJSONGeometry {

        protected List<GeoJSONGeometry> geometries = new ArrayList<GeoJSONGeometry>();

        public GeoJSONGeometryCollection() {
            setType(GeoJSONTypes.GEOMETRY_COLLECTION);
        }

        public List<GeoJSONGeometry> getGeometries() {
            return geometries;
        }

        public void setGeometries(List<GeoJSONGeometry> geometries) {
            this.geometries = geometries;
        }
    }
}
