package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractLatLonBoxType type.</p>
 *
 * <pre>
 * &lt;complexType name="AbstractLatLonBoxType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:north" minOccurs="0"/>
 *              &lt;element ref="kml:south" minOccurs="0"/>
 *              &lt;element ref="kml:east" minOccurs="0"/>
 *              &lt;element ref="kml:west" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractLatLonBoxSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractLatLonBoxObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractLatLonBoxSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="AbstractLatLonBoxObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractLatLonBox extends AbstractObject {

    /**
     *
     * @return the north angle.
     */
    public Angle180 getNorth();

    /**
     *
     * @return the south angle.
     */
    public Angle180 getSouth();

    /**
     *
     * @return the east angle.
     */
    public Angle180 getEast();

    /**
     *
     * @return the west angle.
     */
    public Angle180 getWest();

    /**
     *
     * @return the AbstractLatLonBox simple extensions.
     */
    public List<SimpleType> getAbstractLatLonBoxSimpleExtensions();

    /**
     *
     * @return the abstractLAtLonBox object extensions.
     */
    public List<AbstractObject> getAbstractLatLonBoxObjectExtensions();

}
