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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.GeometryUtils;
import org.geotoolkit.internal.jaxb.DirectPositionAdapter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.apache.sis.measure.Units;

/**
 * A minimum bounding box or rectangle. Regardless of dimension, an {@code Envelope} can
 * be represented without ambiguity as two direct positions (coordinate points). To encode an
 * {@code Envelope}, it is sufficient to encode these two points. This is consistent with
 * all of the data types in this specification, their state is represented by their publicly
 * accessible attributes.
 *
 * @author ISO/DIS 19107
 * @author <A HREF="http://www.opengis.org">OpenGIS&reg; consortium</A>
 * @version 2.0
 * @module
 */
@XmlType(name="EnvelopeType", namespace="http://www.opengis.net/gml")
public class JTSEnvelope implements Envelope {

    /**
     * DirectPosition that has the minimum values for each coordinate dimension
     * (e.g. min x and min y).
     */
    @XmlElement(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(DirectPositionAdapter.class)
    private final DirectPosition lowerCorner;

    /**
     * DirectPosition that has the maximum values for each coordinate dimension
     * (e.g. max x and max y).
     */
    @XmlElement(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(DirectPositionAdapter.class)
    private final DirectPosition upperCorner;

    public JTSEnvelope() {
        lowerCorner = null;
        upperCorner = null;
    }

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
    public final double getMinimum(final int dimension) {
        return lowerCorner.getOrdinate(dimension);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double getMaximum(final int dimension) {
        return upperCorner.getOrdinate(dimension);
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public final double getMedian(final int dimension) {
        return 0.5 * (upperCorner.getOrdinate(dimension) + lowerCorner.getOrdinate(dimension));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final double getSpan(final int dimension) {
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
        final double[] bbox = GeometryUtils.getBBox(this, Units.DEGREE);

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.lowerCorner != null ? this.lowerCorner.hashCode() : 0);
        hash = 17 * hash + (this.upperCorner != null ? this.upperCorner.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return getUpperCorner().getCoordinateReferenceSystem();
    }

}
