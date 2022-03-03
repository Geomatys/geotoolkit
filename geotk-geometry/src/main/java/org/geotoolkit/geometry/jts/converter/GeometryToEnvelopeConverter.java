package org.geotoolkit.geometry.jts.converter;

import java.util.Collections;
import java.util.Set;
import org.apache.sis.math.FunctionProperty;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.geometry.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.Envelope;

/**
 * Note: temporary workaround needed until filters are fully ported to SIS.
 * Required when converting an expression to envelope value-type.
 */
public class GeometryToEnvelopeConverter implements ObjectConverter<Geometry, Envelope> {
    @Override
    public Set<FunctionProperty> properties() {
        return Collections.emptySet();
    }

    @Override
    public Class<Geometry> getSourceClass() {
        return Geometry.class;
    }

    @Override
    public Class<Envelope> getTargetClass() {
        return Envelope.class;
    }

    @Override
    public Envelope apply(Geometry object) throws UnconvertibleObjectException {
        if (object == null) return null;
        return JTS.toEnvelope(object);
    }

    @Override
    public ObjectConverter<Envelope, Geometry> inverse() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
