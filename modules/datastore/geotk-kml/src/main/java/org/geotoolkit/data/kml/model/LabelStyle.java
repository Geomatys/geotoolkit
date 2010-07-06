package org.geotoolkit.data.kml.model;

/**
 *
 * <p>This interface maps LabelStyle element.</p>
 *
 * <pre>
 * &lt;element name="LabelStyle" type="kml:LabelStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 *
 * &lt;complexType name="LabelStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractColorStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:scale" minOccurs="0"/>
 *              &lt;element ref="kml:LabelStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LabelStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LabelStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LabelStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
     * @param scale
     */
    public void setScale(double scale);
}
