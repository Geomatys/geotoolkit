package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>This interface maps ExtendedDate element.</p>
 *
 * <pre>
 * &lt;element name="ExtendedData" type="kml:ExtendedDataType"/>
 *
 * &lt;complexType name="ExtendedDataType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:Data" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:SchemaData" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;any namespace="##other" processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ExtendedData {

    /**
     *
     * @return
     */
    public List<Data> getDatas();

    /**
     *
     * @return
     */
    public List<SchemaData> getSchemaData();

    /**
     * 
     * @return
     */
    public List<Object> getAnyOtherElements();
}
