package org.geotoolkit.data.kml.model;

import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPoint extends com.vividsolutions.jts.geom.Point implements Point {

    private IdAttributes idAttributes;
    private final Extensions extensions = new Extensions();
    private boolean extrude;
    private AltitudeMode altitudeMode;

    /**
     * 
     * @param coordinates
     * @param factory
     */
    public DefaultPoint(Coordinates coordinates, GeometryFactory factory) {
        super(coordinates, factory);
        this.extrude = DEF_EXTRUDE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractGeometrySimpleExtensions
     * @param abstractGeometryObjectExtensions
     * @param extrude
     * @param altitudeMode
     * @param coordinates
     * @param pointSimpleExtensions
     * @param pointObjectExtensions
     * @param factory
     */
    public DefaultPoint(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> pointSimpleExtensions,
            List<AbstractObject> pointObjectExtensions,
            GeometryFactory factory) {
        super(coordinates, factory);
        this.idAttributes = idAttributes;
        this.extrude = extrude;
        this.altitudeMode = altitudeMode;
        if (objectSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.OBJECT).addAll(objectSimpleExtensions);
        }
        if (abstractGeometrySimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.GEOMETRY).addAll(abstractGeometrySimpleExtensions);
        }
        if (abstractGeometryObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.GEOMETRY).addAll(abstractGeometryObjectExtensions);
        }
        if (pointSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.POINT).addAll(pointSimpleExtensions);
        }
        if (pointObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.POINT).addAll(pointObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getExtrude() {
        return this.extrude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {
        return this.altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinates getCoordinateSequence() {
        return (Coordinates) super.getCoordinateSequence();
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExtrude(boolean extrude) {
        this.extrude = extrude;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Extensions extensions() {
        return extensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public IdAttributes getIdAttributes() {
        return this.idAttributes;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIdAttributes(IdAttributes idAttributes) {
        this.idAttributes = idAttributes;
    }
}
