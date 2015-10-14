
package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.coordinate.Position;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lineStringPosListType", namespace="http://www.opengis.net/gml")
public class LineStringPosListType {

    @XmlAttribute
    private String srsName;

    @XmlElement(namespace = "http://www.opengis.net/gml")
    private PosListType posList;

    @XmlElement(namespace = "http://www.opengis.net/gml")
    private CoordinatesType coordinates;

    public LineStringPosListType() {

    }

    public LineStringPosListType(final List<Double> value) {
        this.posList = new PosListType(value);
    }

    public LineStringPosListType(final JTSLineString lineString) {
        this.srsName = CoordinateReferenceSystemAdapter.getSrsName(lineString.getCoordinateReferenceSystem());
        List<Double> value = new ArrayList<Double>();
        for (Position p : lineString.getPositions()) {
            for (int i = 0; i < p.getDirectPosition().getDimension(); i++) {
                value.add(p.getDirectPosition().getOrdinate(i));
            }
        }
        posList = new PosListType(value);
    }

    public JTSLineString getJTSLineString() {
        CoordinateReferenceSystem crs = null;
        if (this.srsName != null) {
            try {
                crs = CRS.decode(srsName);
            } catch (FactoryException ex) {
                Logging.getLogger("org.geotoolkit.internal.jaxb").log(Level.WARNING, null, ex);
            }
        }
        final JTSLineString result = new JTSLineString(crs);
        if (posList != null) {
            for (int i = 0; i < posList.getValue().size(); i = i + 2) {
                result.getPositions().add(new GeneralDirectPosition(posList.getValue().get(i), posList.getValue().get(i + 1)));
            }
        } else if (coordinates != null) {
            for (int i = 0; i < coordinates.getValues().size(); i = i + 2) {
                result.getPositions().add(new GeneralDirectPosition(coordinates.getValues().get(i), coordinates.getValues().get(i + 1)));
            }
        }
        return result;
    }
    /**
     * @return the srsName
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * @param srsName the srsName to set
     */
    public void setSrsName(final String srsName) {
        this.srsName = srsName;
    }

    /**
     * @return the posList
     */
    public PosListType getPosList() {
        return posList;
    }

    /**
     * @param posList the posList to set
     */
    public void setPosList(final PosListType posList) {
        this.posList = posList;
    }

    /**
     * @return the coordinates
     */
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * @param coordinates the coordinates to set
     */
    public void setCoordinates(CoordinatesType coordinates) {
        this.coordinates = coordinates;
    }
}
