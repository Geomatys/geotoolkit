package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>THis interface maps SchemaData element.</p>
 *
 * <br />&lt;element name="SchemaData" type="kml:SchemaDataType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="SchemaDataType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:SimpleData" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:SchemaDataExtension" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;attribute name="schemaUrl" type="anyURI"/>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="SchemaDataExtension" abstract="true"/>
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
