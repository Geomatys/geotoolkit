package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Scale element.</p>
 *
 * <br />&lt;element name="Scale" type="kml:ScaleType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="ScaleType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:x" minOccurs="0"/>
 * <br />&lt;element ref="kml:y" minOccurs="0"/>
 * <br />&lt;element ref="kml:z" minOccurs="0"/>
 * <br />&lt;element ref="kml:ScaleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:ScaleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="ScaleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="ScaleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Scale extends AbstractObject {

    /**
     *
     * @return
     */
    public double getX();

    /**
     *
     * @return
     */
    public double getY();

    /**
     *
     * @return
     */
    public double getZ();

    /**
     *
     * @return the list of Scale simple extensions.
     */
    public List<SimpleType> getScaleSimpleExtensions();

    /**
     *
     * @return the list of Scale object extensions.
     */
    public List<AbstractObject> getScaleObjectExtensions();

}
