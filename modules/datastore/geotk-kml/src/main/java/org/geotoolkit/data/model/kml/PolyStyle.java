package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p></p>
 *
 * <br />&lt;element name="PolyStyle" type="kml:PolyStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 * <br />&lt;complexType name="PolyStyleType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractColorStyleType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:fill" minOccurs="0"/>
 * <br />&lt;element ref="kml:outline" minOccurs="0"/>
 * <br />&lt;element ref="kml:PolyStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:PolyStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="PolyStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="PolyStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface PolyStyle extends AbstractColorStyle {

    /**
     *
     * @return
     */
    public boolean getFill();

    /**
     *
     * @return
     */
    public boolean getOutline();

    /**
     *
     * @return the list of PolyStyle simple extensions.
     */
    public List<SimpleType> getPolyStyleSimpleExtensions();

    /**
     *
     * @return the list of PolyStyle object extensions.
     */
    public List<AbstractObject> getPolyStyleObjectExtensions();

}
