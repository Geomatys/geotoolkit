/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $Source: /cvs/ctree/LiteGO1/src/jar/com/polexis/lite/spatialschema/geometry/EnvelopeImpl.java,v $
 **
 ** Copyright (C) 2003 Open GIS Consortium, Inc. All Rights Reserved. http://www.opengis.org/Legal/
 **
 *************************************************************************************************/
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry;


import javax.measure.unit.NonSI;

import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.GeometryUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * A minimum bounding box or rectangle. Regardless of dimension, an {@code Envelope} can
 * be represented without ambiguity as two direct positions (coordinate points). To encode an
 * {@code Envelope}, it is sufficient to encode these two points. This is consistent with
 * all of the data types in this specification, their state is represented by their publicly
 * accessible attributes.
 *
 * @UML datatype GM_Envelope
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 */
public class JTSEnvelope implements Envelope {

    /**
     * DirectPosition that has the minimum values for each coordinate dimension
     * (e.g. min x and min y).
     */
    private final DirectPosition lowerCorner;

    /**
     * DirectPosition that has the maximum values for each coordinate dimension
     * (e.g. max x and max y).
     */
    private final DirectPosition upperCorner;

    /**
     * Creates a new {@code EnvelopeImpl}.
     * @param lowerCorner
     * @param upperCorner
     */
    public JTSEnvelope(final DirectPosition lowerCorner, final DirectPosition upperCorner) {
        this.lowerCorner = new GeneralDirectPosition(lowerCorner);
        this.upperCorner = new GeneralDirectPosition(upperCorner);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final int getDimension() {
        return upperCorner.getDimension();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double getMinimum(int dimension) {
        return lowerCorner.getOrdinate(dimension);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double getMaximum(int dimension) {
        return upperCorner.getOrdinate(dimension);
    }

    /**
     * {@inheritDoc }
     */
    @Deprecated
    @Override
    public final double getCenter(int dimension) {
        return 0.5 * (upperCorner.getOrdinate(dimension) + lowerCorner.getOrdinate(dimension));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double getMedian(int dimension) {
        return 0.5 * (upperCorner.getOrdinate(dimension) + lowerCorner.getOrdinate(dimension));
    }

    /**
     * {@inheritDoc }
     */
    @Deprecated
    @Override
    public final double getLength(int dimension) {
        return upperCorner.getOrdinate(dimension) - lowerCorner.getOrdinate(dimension);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double getSpan(int dimension) {
        return upperCorner.getOrdinate(dimension) - lowerCorner.getOrdinate(dimension);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final DirectPosition getUpperCorner() {
        return new GeneralDirectPosition(upperCorner);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final DirectPosition getLowerCorner() {
        return new GeneralDirectPosition(lowerCorner);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final double[] bbox = GeometryUtils.getBBox(this, NonSI.DEGREE_ANGLE);
        
        final StringBuffer returnable = new StringBuffer("Envelope[").append(bbox[0]);
        for (int i = 1; i < bbox.length; i++) {
            returnable.append(",").append(bbox[i]);
        }
        return returnable.append("]").toString();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {

        if(!(obj instanceof Envelope)){
            return false;
        }

        final Envelope that = (Envelope) obj;
        return GeometryUtils.equals(this, that);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return getUpperCorner().getCoordinateReferenceSystem();
    }

}
