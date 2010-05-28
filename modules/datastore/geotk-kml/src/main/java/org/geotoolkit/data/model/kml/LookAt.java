package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <p>This interface maps Lod element.</p>
 *
 * <br />&lt;element name="LookAt" type="kml:LookAtType" substitutionGroup="kml:AbstractViewGroup"/>
 * <br />&lt;complexType name="LookAtType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractViewType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:longitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:latitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitude" minOccurs="0"/>
 * <br />&lt;element ref="kml:heading" minOccurs="0"/>
 * <br />&lt;element ref="kml:tilt" minOccurs="0"/>
 * <br />&lt;element ref="kml:range" minOccurs="0"/>
 * <br />&lt;element ref="kml:altitudeModeGroup" minOccurs="0"/>
 * <br />&lt;element ref="kml:LookAtSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:LookAtObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="LookAtSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="LookAtObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface LookAt extends AbstractView {

    /**
     *
     * @return
     */
    public Angle180 getLongitude();

    /**
     *
     * @return
     */
    public Angle90 getLatitude();

    /**
     *
     * @return
     */
    public double getAltitude();

    /**
     *
     * @return
     */
    public Angle360 getHeading();

    /**
     *
     * @return
     */
    public Anglepos180 getTilt();

    /**
     *
     * @return
     */
    public double getRange();

    /**
     *
     * @return the list of LookAt simple extensions.
     */
    public List<SimpleType> getLookAtSimpleExtensions();

    /**
     *
     * @return the lis of LookAt object extensions.
     */
    public List<AbstractObject> getLookAtObjectExtensions();
}
