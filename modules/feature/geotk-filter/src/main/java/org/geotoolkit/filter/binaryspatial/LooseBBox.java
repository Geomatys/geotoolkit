/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.filter.binaryspatial;

import com.vividsolutions.jts.geom.Geometry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.PropertyName;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Perform the same work as the BBox expect it evaluate intersection only against
 * the geometry envelope.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LooseBBox extends DefaultBBox{

    public LooseBBox(final PropertyName property, final DefaultLiteral<BoundingBox> bbox) {
        super(property,bbox);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(final Object object) {
        Geometry candidate;
        if (object instanceof Feature && left.getPropertyName().isEmpty()) {
            candidate = (Geometry) ((Feature)object).getDefaultGeometryProperty().getValue();
        } else {
            candidate = left.evaluate(object, Geometry.class);
        }

        if(candidate == null){
            return false;
        }

        //we don't know in which crs it is, try to find it
        CoordinateReferenceSystem crs = null;
        try{
            crs = JTS.findCoordinateReferenceSystem(candidate);
        }catch(IllegalArgumentException ex){
            Logger.getLogger(DefaultBBox.class.getName()).log(Level.WARNING, null, ex);
        }catch(NoSuchAuthorityCodeException ex){
            Logger.getLogger(DefaultBBox.class.getName()).log(Level.WARNING, null, ex);
        }catch(FactoryException ex){
            Logger.getLogger(DefaultBBox.class.getName()).log(Level.WARNING, null, ex);
        }

        //if we don't know the crs, we will assume it's the objective crs already
        if(crs != null){
            //reproject in objective crs if needed
            if(!CRS.equalsIgnoreMetadata(this.crs,crs)){
                try {
                    candidate = JTS.transform(candidate, CRS.findMathTransform(crs, this.crs));
                } catch (MismatchedDimensionException ex) {
                    Logger.getLogger(DefaultBBox.class.getName()).log(Level.WARNING, null, ex);
                    return false;
                } catch (TransformException ex) {
                    Logger.getLogger(DefaultBBox.class.getName()).log(Level.WARNING, null, ex);
                    return false;
                } catch (FactoryException ex) {
                    Logger.getLogger(DefaultBBox.class.getName()).log(Level.WARNING, null, ex);
                    return false;
                }
            }
        }

        final com.vividsolutions.jts.geom.Envelope candidateEnv = candidate.getEnvelopeInternal();
        return boundingEnv.intersects(candidateEnv);
    }

}
