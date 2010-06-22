package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps PointStyle element.</p>
 *
 * <pre>
 * &lt;element name="PolyStyle" type="kml:PolyStyleType" substitutionGroup="kml:AbstractColorStyleGroup"/>
 *
 * &lt;complexType name="PolyStyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractColorStyleType">
 *          &lt;sequence>
 *              &lt;element ref="kml:fill" minOccurs="0"/>
 *              &lt;element ref="kml:outline" minOccurs="0"/>
 *              &lt;element ref="kml:PolyStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:PolyStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PolyStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PolyStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
