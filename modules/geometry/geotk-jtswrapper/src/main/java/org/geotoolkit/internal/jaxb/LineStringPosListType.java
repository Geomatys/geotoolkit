
package org.geotoolkit.internal.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSLineString;
import org.opengis.geometry.coordinate.Position;

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
