/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.metadata.geotiff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.internal.referencing.GeodeticObjectBuilder;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.logging.Logging;

import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.opengis.referencing.crs.SingleCRS;

/**
 * Geotiff is a widely used format, many metadata formats may be associated with it.
 * Extensions may modify metadatas, samples or even grid geometry and crs.
 *
 * @author Remi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public abstract class GeoTiffExtension {

    private static final GeoTiffExtension[] EXTENSIONS;
    static {
        final ServiceLoader<GeoTiffExtension> loader = ServiceLoader.load(GeoTiffExtension.class);
        final List<GeoTiffExtension> extensions = new ArrayList<>();
        for(GeoTiffExtension extension : loader){
            extensions.add(extension);
        }
        EXTENSIONS = extensions.toArray(new GeoTiffExtension[0]);
    }

    /**
     * Get all extensions.
     *
     * @return All geotiff extensions
     */
    public static GeoTiffExtension[] getExtensions(){
        return EXTENSIONS.clone();
    }

    /**
     * Check if this extension is present for given input.
     * if isPresent return true and before using any method
     * a new instance of the extension should be created.
     *
     * @param input
     * @return true if extension exist
     */
    public abstract boolean isPresent(Object input);

    /**
     * Create a new instance of this extension.
     * @return
     */
    public GeoTiffExtension newInstance(){
        try {
            return this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create a new instance of "+this.getClass());
        }
    }

    /**
     * Modify the spatial metadata of the geotiff coverage.
     *
     * @param reader
     * @param metadata to be modified
     * @return
     * @throws java.io.IOException
     */
    public abstract SpatialMetadata fillSpatialMetaData(TiffImageReader reader, SpatialMetadata metadata) throws IOException;

    /**
     * Modify the given spatial metadata, adding a new crs axis dimension.
     * If the crs axis already exist it will not be added and only the value will be updated.
     *
     * This method should be used only to declare the geotiff on a new axis.
     * For example adding a temporal dimension and define it's value.
     *
     * @param metadata
     * @param axisCrs
     * @param value
     * @throws FactoryException
     */
     public static void setOrCreateSliceDimension(SpatialMetadata metadata, CoordinateReferenceSystem axisCrs, double value) throws FactoryException{
        //ensure no cache modify the values
        metadata.clearInstancesCache();

        final ReferencingBuilder rb = new ReferencingBuilder(metadata);
        final GridDomainAccessor acc = new GridDomainAccessor(metadata);
        final RectifiedGrid rectifiedGrid = metadata.getInstanceForType(RectifiedGrid.class);

        //search for the coordinate reference system
        CoordinateReferenceSystem crs = rb.getCoordinateReferenceSystem(CoordinateReferenceSystem.class);
        if(crs==null){
            //no crs defined, we can't add any slice axis value
            final Logger logger = Logging.getLogger("org.geotoolkit.metadata.geotiff");
            logger.info("Tiff has no base CRS, slice dimension crs will not be added.");
            return;
        }

        final List<SingleCRS> crss = CRS.getSingleComponents(crs);
        int axisIndex = -1;
        int inc = 0;
        for(CoordinateReferenceSystem cs : crss){
            if(cs.equals(axisCrs)){
                axisIndex = inc;
                break;
            }
            inc += cs.getCoordinateSystem().getDimension();
        }

        if (axisIndex < 0) {
            //this axis is not declared, add it
            crs = new GeodeticObjectBuilder().addName(crs.getName().getCode()+"/"+axisCrs.getName().getCode())
                                             .createCompoundCRS(crs, axisCrs);
            rb.setCoordinateReferenceSystem(crs);
            //calculate new transform values
            final List<double[]> offsetVectors = new ArrayList(rectifiedGrid.getOffsetVectors());
                for (int i = 0; i < offsetVectors.size(); i++) {
                    double[] vector = offsetVectors.get(i);
                    vector = Arrays.copyOf(vector, vector.length+1);
                    offsetVectors.set(i, vector);
                }
                final double[] tempVector = new double[crs.getCoordinateSystem().getDimension()];
                tempVector[tempVector.length-1] = 1;
                offsetVectors.add(tempVector);

                //new origin
                final DirectPosition oldOrigin = rectifiedGrid.getOrigin();
                    final GeneralDirectPosition newOrigin = new GeneralDirectPosition(crs);
                    for (int i = 0, n = oldOrigin.getDimension(); i < n; i++) {
                        newOrigin.setOrdinate(i, oldOrigin.getOrdinate(i));
                    }
                    newOrigin.setOrdinate(oldOrigin.getDimension(), value);

                    //new limits
                    final int[][] limits = acc.getLimits();
                    limits[0] = Arrays.copyOf(limits[0], limits[0].length+1);
                    limits[1] = Arrays.copyOf(limits[1], limits[1].length+1);
        //            limits[1][limits[1].length-1] = 1;

                    //set new values
                    acc.setOrigin(newOrigin.getCoordinate());
                    acc.setLimits(limits[0], limits[1]);
                    acc.clearOffsetVectors();
                    for (double[] ov : offsetVectors) {
                        acc.addOffsetVector(ov);
                    }

        } else {
            //axis already exist, update the value

            //new origin
            final DirectPosition oldOrigin = rectifiedGrid.getOrigin();
            final GeneralDirectPosition newOrigin = new GeneralDirectPosition(oldOrigin);
            newOrigin.setOrdinate(axisIndex, value);

            //set new values
            acc.setOrigin(newOrigin.getCoordinate());
        }

        //metadata keeps a cache of object likes crs, rectifiedgrid and so on ...
        //we must clear them since we modifyed the sub nodes
        metadata.clearInstancesCache();
    }

}
