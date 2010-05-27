package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractLatLonBoxType type.</p>
 *
 * <br />&lt;complexType name="AbstractLatLonBoxType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:north" minOccurs="0"/>
 * <br />&lt;element ref="kml:south" minOccurs="0"/>
 * <br />&lt;element ref="kml:east" minOccurs="0"/>
 * <br />&lt;element ref="kml:west" minOccurs="0"/>
 * <br />&lt;element ref="kml:AbstractLatLonBoxSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractLatLonBoxObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractLatLonBoxSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="AbstractLatLonBoxObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
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
