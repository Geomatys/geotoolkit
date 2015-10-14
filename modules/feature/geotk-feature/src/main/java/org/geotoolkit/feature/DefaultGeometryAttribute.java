/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.feature;

import java.util.logging.Level;

import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.GeometryType;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.Objects;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.apache.sis.util.logging.Logging;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultGeometryAttribute extends DefaultAttribute<Object,GeometryDescriptor,Identifier> implements GeometryAttribute {

    /**
     * bounds, derived
     */
    protected BoundingBox bounds;

    public DefaultGeometryAttribute(final Object content, final GeometryDescriptor descriptor,
            final Identifier id){
        super(content, descriptor, id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeometryType getType() {
        return (GeometryType)super.getType();
    }

    /**
     * Set the bounds for the contained geometry.
     */
    @Override
    public synchronized void setBounds(final Envelope bbox) {
        bounds = DefaultBoundingBox.castOrCopy(bbox);
    }

    /**
     * Returns the non null envelope of this attribute. If the attribute's
     * geometry is <code>null</code> the returned Envelope
     * <code>isNull()</code> is true.
     *
     * @return Bounds of the geometry
     */
    @Override
    public synchronized BoundingBox getBounds() {
        final Object val = getValue();
        if(bounds == null){
            //we explicitly use the getValue method, since subclass can override it

            //get the type crs if defined
            CoordinateReferenceSystem crs = getType().getCoordinateReferenceSystem();

            if(crs == null){
                //the type does not define the crs, then the object value might define it
                if(val instanceof Geometry){
                    try {
                        crs = JTS.findCoordinateReferenceSystem((Geometry) val);
                    } catch (FactoryException ex) {
                        Logging.getLogger("org.geotoolkit.feature").log(Level.WARNING, null, ex);
                    }
                }else if(val instanceof org.opengis.geometry.Geometry){
                    crs = ((org.opengis.geometry.Geometry)val).getCoordinateReferenceSystem();
                }
            }

            bounds = new JTSEnvelope2D(crs);
        }

        if (val instanceof Geometry) {
            ((JTSEnvelope2D)bounds).init(((Geometry)val).getEnvelopeInternal());
        } else {
            ((JTSEnvelope2D)bounds).setToNull();
        }

        return bounds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DefaultGeometryAttribute)) {
            return false;
        }

        final DefaultGeometryAttribute att = (DefaultGeometryAttribute) o;

        //JD: since Geometry does not implement equals(Object) "properly",( ie
        // if you dont call equals(Geomtery) two geometries which are equal
        // will not be equal) we dont call super.equals()
        if (!Objects.equals(descriptor, att.descriptor)) {
            return false;
        }

        if(descriptor==null){
            //test type
            if (!Objects.equals(type, att.type)) {
                return false;
            }
        }

        if (!Objects.equals(id, att.id)) {
            return false;
        }

        if (value != null && att.value != null) {
            //another lovley jts thing... comparing geometry collections that
            // arent multi point/line/poly throws an exception, so we nee dto
            // that comparison
            if (att.value instanceof GeometryCollection &&
                    !(att.value instanceof MultiPoint) &&
                    !(att.value instanceof MultiLineString) &&
                    !(att.value instanceof MultiPolygon)) {

                if (value instanceof GeometryCollection) {
                    //compare the two collections
                    final GeometryCollection c1 = (GeometryCollection) value;
                    final GeometryCollection c2 = (GeometryCollection) att.value;

                    if (c1.getNumGeometries() != c2.getNumGeometries()) {
                        return false;
                    }

                    for (int i = 0; i < c1.getNumGeometries(); i++) {
                        final Geometry g1 = c1.getGeometryN(i);
                        final Geometry g2 = c2.getGeometryN(i);

                        if (!g1.equals(g2)) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
            if (!((Geometry) value).equals((Geometry) att.value)) {
                return false;
            }
        } else {
            return Objects.deepEquals(value, this.value);
        }

        return true;
    }

    @Override
    public void setValue(Object newValue) throws IllegalArgumentException, IllegalStateException {
        bounds = null; //clear cache
        super.setValue(newValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = descriptor.hashCode();

        if (id != null) {
            hash += 37 * id.hashCode();
        }

        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(":");
        sb.append(getName());
        final CoordinateReferenceSystem crs = getType().getCoordinateReferenceSystem();
        if (id != null || crs != null) {
            sb.append("<");
            sb.append(getType().getName().tip().toString());
            if (id != null) {
                sb.append(" id=");
                sb.append(id);
            }
            if (crs != null) {
                sb.append(" crs=");
                sb.append(crs);
            }
            if (id != null) {
                sb.append(" id=");
                sb.append(id);
            }
            sb.append(">");
        }
        sb.append("=");
        sb.append(value);
        return sb.toString();
    }
}
