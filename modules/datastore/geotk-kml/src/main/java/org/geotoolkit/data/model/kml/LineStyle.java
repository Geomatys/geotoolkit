package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps LineStyle element.</p>
 * 
 * <br />&lt;element name="LineStyle" type="kml:LineStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 * <br />&lt;complexType name="LineStyleType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractColorStyleType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:width" minOccurs="0"/>
 * <br />&lt;element ref="kml:LineStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LineStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LineStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LineStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface LineStyle extends AbstractColorStyle {

    /**
     *
     * @return
     */
    public double getWidth();

    /**
     *
     * @return
     */
    public List<SimpleType> getLineStyleSimpleExtensions();

    /**
     * 
     * @return
     */
    public List<AbstractObject> getLineStyleObjectExtensions();

}
