
package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

    public LineStringPosListType() {

    }

    public LineStringPosListType(List<Double> value) {
        this.posList = new PosListType(value);
    }

    public LineStringPosListType(JTSLineString lineString) {
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
            } catch (NoSuchAuthorityCodeException ex) {
                Logger.getLogger(LineStringPosListType.class.getName()).log(Level.WARNING, null, ex);
            } catch (FactoryException ex) {
                Logger.getLogger(LineStringPosListType.class.getName()).log(Level.WARNING, null, ex);
            }
        }
        JTSLineString result = new JTSLineString(crs);
        for (int i = 0; i < posList.getValue().size(); i = i + 2) {
            result.getPositions().add(new GeneralDirectPosition(posList.getValue().get(i), posList.getValue().get(i + 1)));
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
    public void setSrsName(String srsName) {
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
    public void setPosList(PosListType posList) {
        this.posList = posList;
    }
}
