package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps LineStyle element.</p>
 *
 * <pre>
 * &lt;element name="LineStyle" type="kml:LineStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 *
 * &lt;complexType name="LineStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractColorStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:width" minOccurs="0"/>
 *              &lt;element ref="kml:LineStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:LineStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="LineStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="LineStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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

    /**
     *
     * @param width
     */
    public void setWidth(double width);

    /**
     *
     * @param lineStyleSimpleExtensions
     */
    public void setLineStyleSimpleExtensions(List<SimpleType> lineStyleSimpleExtensions);

    /**
     *
     * @param lineStyleObjectExtensions
     */
    public void setLineStyleObjectExtensions(List<AbstractObject> lineStyleObjectExtensions);
}
