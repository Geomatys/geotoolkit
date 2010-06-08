package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>This interface maps SchemaData element.</p>
 *
 * <pre>
 * &lt;element name="SchemaData" type="kml:SchemaDataType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="SchemaDataType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:SimpleData" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:SchemaDataExtension" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *          &lt;attribute name="schemaUrl" type="anyURI"/>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="SchemaDataExtension" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SchemaData extends AbstractObject {

    /**
     *
     * @return the list of SimpleData simple extensions.
     */
    public List<SimpleData> getSimpleDatas();

    /**
     *
     * @return the list of SimpleData object extensions.
     */
    public List<Object> getSchemaDataExtensions();

}
