package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

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
     * @return the list of LabelStyle simple extensions.
     */
    public List<SimpleType> getLabelStyleSimpleExtensions();

    /**
     *
     * @return The list of LabelStyle object extensions.
     */
    public List<AbstractObject> getLabelStyleObjectExtensions();

    /**
     *
     * @param scale
     */
    public void setScale(double scale);

    /**
     *
     * @param labelStyleSimpleExtensions
     */
    public void setLabelStyleSimpleExtensions(List<SimpleType> labelStyleSimpleExtensions);

    /**
     *
     * @param labelStyleObjectExtensions
     */
    public void setLabelStyleObjectExtensions(List<AbstractObject> labelStyleObjectExtensions);
}
