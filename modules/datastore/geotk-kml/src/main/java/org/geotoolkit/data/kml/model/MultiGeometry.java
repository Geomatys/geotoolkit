package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps MultiGeometry element.</p>
 *
 * <pre>
 * &lt;element name="MultiGeometry" type="kml:MultiGeometryType" substitutionGroup="kml:AbstractGeometryGroup"/>
 *
 * &lt;complexType name="MultiGeometryType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractGeometryType">
 *          &lt;sequence>
 *              &lt;element ref="kml:AbstractGeometryGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:MultiGeometrySimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:MultiGeometryObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="MultiGeometrySimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="MultiGeometryObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface MultiGeometry extends AbstractGeometry {

    /**
     *
     * @return
     */
    public List<AbstractGeometry> getGeometries();

    /**
     *
     * @return the list of MultiGeometry simple extensions.
     */
    public List<SimpleType> getMultiGeometrySimpleExtensions();

    /**
     *
     * @return the list of MultiGeometry object extensions.
     */
    public List<AbstractObject> getMultiGeometryObjectExtensions();

    /**
     *
     * @param geometries
     */
    public void setGeometries(List<AbstractGeometry> geometries);

    /**
     *
     * @param multiGeometrySimpleExtensions
     */
    public void setMultiGeometrySimpleExtensions(List<SimpleType> multiGeometrySimpleExtensions);

    /**
     * 
     * @param multiGeometryObjectExtensions
     */
    public void setMultiGeometryObjectExtensions(List<AbstractObject> multiGeometryObjectExtensions);

}
