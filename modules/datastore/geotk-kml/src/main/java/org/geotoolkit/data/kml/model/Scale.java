package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 * <p>This interface maps Scale element.</p>
 *
 * <pre>
 * &lt;element name="Scale" type="kml:ScaleType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="ScaleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:x" minOccurs="0"/>
 *              &lt;element ref="kml:y" minOccurs="0"/>
 *              &lt;element ref="kml:z" minOccurs="0"/>
 *              &lt;element ref="kml:ScaleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:ScaleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="ScaleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="ScaleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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
