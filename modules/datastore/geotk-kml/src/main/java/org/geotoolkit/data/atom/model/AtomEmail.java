package org.geotoolkit.data.atom.model;

/**
 * <p>This interface maps email atom type.</p>
 *
 * <pre>
 * &lt;simpleType name="atomEmailAddress">
 *  &lt;restriction base="string">
 *      &lt;pattern value=".+@.+"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AtomEmail {

    /**
     *
     * @return
     */
    public String getAddress();

    /**
     * 
     * @param address
     */
    public void setAddress(String address);

}
