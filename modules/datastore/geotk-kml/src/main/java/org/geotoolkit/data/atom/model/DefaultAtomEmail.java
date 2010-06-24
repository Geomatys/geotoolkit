package org.geotoolkit.data.atom.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultAtomEmail implements AtomEmail {

    private String address;

    /**
     *
     */
    public DefaultAtomEmail() {
        this.address = null;
    }

    /**
     * 
     * @param address
     */
    public DefaultAtomEmail(String address) {
        if (address.matches(".+@.+")) {
            this.address = address;
        } else {
            throw new IllegalArgumentException(
                    "Illegal address format for " + this.getClass());
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getAddress() {
        return this.address;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddress(String address) {
        this.address = address;
    }
}
