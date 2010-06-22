package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.model.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPoint extends DefaultAbstractGeometry implements Point {

    private boolean extrude;
    private AltitudeMode altitudeMode;
    private Coordinates coordinates;
    private List<SimpleType> pointSimpleExtensions;
    private List<AbstractObject> pointObjectExtensions;

    /**
     *
     */
    public DefaultPoint(){
        this.extrude = DEF_EXTRUDE;
        this.altitudeMode = DEF_ALTITUDE_MODE;
        this.pointSimpleExtensions = EMPTY_LIST;
        this.pointObjectExtensions = EMPTY_LIST;
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
     */
    public DefaultPoint(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractGeometrySimpleExtensions,
            List<AbstractObject> abstractGeometryObjectExtensions,
            boolean extrude,
            AltitudeMode altitudeMode,
            Coordinates coordinates,
            List<SimpleType> pointSimpleExtensions,
            List<AbstractObject> pointObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
            abstractGeometrySimpleExtensions, abstractGeometryObjectExtensions);
        this.extrude = extrude;
        this.altitudeMode = altitudeMode;
        this.coordinates = coordinates;
        this.pointSimpleExtensions = (pointSimpleExtensions == null) ? EMPTY_LIST : pointSimpleExtensions;
        this.pointObjectExtensions = (pointObjectExtensions == null) ? EMPTY_LIST : pointObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getExtrude() {return this.extrude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AltitudeMode getAltitudeMode() {return this.altitudeMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinates getCoordinates() {return this.coordinates;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getPointSimpleExtensions() {return this.pointSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getPointObjectExtensions() {return this.pointObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExtrude(boolean extrude) {this.extrude = extrude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAltitudeMode(AltitudeMode altitudeMode) {this.altitudeMode = altitudeMode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setCoordinates(Coordinates coordinates) {this.coordinates = coordinates;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPointSimpleExtensions(List<SimpleType> pointSimpleExtensions) {
        this.pointSimpleExtensions = pointSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPointObjectExtensions(List<AbstractObject> pointObjectExensions) {
        this.pointObjectExtensions = pointObjectExensions;
    }

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "Point : ";
        resultat += "\n\t"+coordinates.toString();
        return resultat;
    }
}
