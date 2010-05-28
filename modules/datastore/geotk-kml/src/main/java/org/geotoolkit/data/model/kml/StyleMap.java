package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps StyleMap element.</p>
 *
 * <br />&lt;element name="StyleMap" type="kml:StyleMapType" substitutionGroup="kml:AbstractStyleSelectorGroup"/>
 * <br />&lt;complexType name="StyleMapType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractStyleSelectorType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:Pair" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:StyleMapSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:StyleMapObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="StyleMapSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="StyleMapObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface StyleMap extends AbstractStyleSelector {

    /**
     *
     * @return
     */
    public List<Pair> getPairs();

    /**
     *
     * @return the list of StyleMap simple extensions.
     */
    public List<SimpleType> getStyleMapSimpleExtensions();

    /**
     *
     * @return the list of StyleMap object extensions.
     */
    public List<AbstractObject> getStyleMapObjectExtensions();

}
