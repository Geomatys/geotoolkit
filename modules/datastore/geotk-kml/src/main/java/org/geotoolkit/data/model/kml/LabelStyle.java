package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <p>This interface maps LabelStyle element.</p>
 *
 * <br />&lt;element name="LabelStyle" type="kml:LabelStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 * <br />&lt;complexType name="LabelStyleType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractColorStyleType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:scale" minOccurs="0"/>
 * <br />&lt;element ref="kml:LabelStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LabelStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LabelStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LabelStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface LabelStyle extends AbstractColorStyle {

    /**
     *
     * @return
     */
    public double getScale();

    /**
     *
     * @return the list of LabelStyle simple extensions.
     */
    public List<SimpleType> getLabelStyleSimpleExtensions();

    /**
     *
     * @return The list of LabelStyle object extensions.
     */
    public List<AbstractObject> getLabelStyleObjectExtensions();

}
