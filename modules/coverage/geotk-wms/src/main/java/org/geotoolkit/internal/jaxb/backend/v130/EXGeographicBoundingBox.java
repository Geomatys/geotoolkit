package org.geotoolkit.internal.jaxb.backend.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.internal.jaxb.backend.AbstractGeographicBoundingBox;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="westBoundLongitude" type="{http://www.opengis.net/wms}longitudeType"/>
 *         &lt;element name="eastBoundLongitude" type="{http://www.opengis.net/wms}longitudeType"/>
 *         &lt;element name="southBoundLatitude" type="{http://www.opengis.net/wms}latitudeType"/>
 *         &lt;element name="northBoundLatitude" type="{http://www.opengis.net/wms}latitudeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "westBoundLongitude",
    "eastBoundLongitude",
    "southBoundLatitude",
    "northBoundLatitude"
})
@XmlRootElement(name = "EX_GeographicBoundingBox")
public class EXGeographicBoundingBox extends AbstractGeographicBoundingBox {

    private double westBoundLongitude;
    private double eastBoundLongitude;
    private double southBoundLatitude;
    private double northBoundLatitude;

    /**
     * An empty constructor used by JAXB.
     */
    EXGeographicBoundingBox() {
    }

    /**
     * Build a new bounding box.
     *
     */
    public EXGeographicBoundingBox(final double westBoundLongitude, final double southBoundLatitude,
            final double eastBoundLongitude, final double northBoundLatitude) {
        this.eastBoundLongitude = eastBoundLongitude;
        this.northBoundLatitude = northBoundLatitude;
        this.southBoundLatitude = southBoundLatitude;
        this.westBoundLongitude = westBoundLongitude;
        
    }
    /**
     * Gets the value of the westBoundLongitude property.
     * 
     */
    public double getWestBoundLongitude() {
        return westBoundLongitude;
    }

    /**
     * Gets the value of the eastBoundLongitude property.
     * 
     */
    public double getEastBoundLongitude() {
        return eastBoundLongitude;
    }

    /**
     * Gets the value of the southBoundLatitude property.
     * 
     */
    public double getSouthBoundLatitude() {
        return southBoundLatitude;
    }

    /**
     * Gets the value of the northBoundLatitude property.
     * 
     */
    public double getNorthBoundLatitude() {
        return northBoundLatitude;
    }
    
    @Override
    public String toString() {
        return "Env[" + westBoundLongitude + " : " + eastBoundLongitude + 
                 ", " + southBoundLatitude + " : " + northBoundLatitude + "]";
    }
    
}
