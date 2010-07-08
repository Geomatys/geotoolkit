package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.List;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.kml.xsd.SimpleType;
import org.geotoolkit.data.xal.model.AddressDetails;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPlacemark extends DefaultAbstractFeature implements Placemark {

    private AbstractGeometry abstractGeometry;

    /**
     * 
     */
    public DefaultPlacemark() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param atomLink
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param abstractGeometry
     * @param placemarkSimpleExtensions
     * @param placemarkObjectExtensions
     */
    public DefaultPlacemark(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name, boolean visibility, boolean open,
            AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails,
            String phoneNumber, Object snippet,
            Object description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            URI styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, Object extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            AbstractGeometry abstractGeometry,
            List<SimpleType> placemarkSimpleExtensions,
            List<AbstractObject> placemarkObjectExtensions) {

        super(objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink,
                address, addressDetails,
                phoneNumber, snippet, description, view, timePrimitive,
                styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions);
        this.abstractGeometry = abstractGeometry;
        if (placemarkSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.PLACEMARK).addAll(placemarkSimpleExtensions);
        }
        if (placemarkObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.PLACEMARK).addAll(placemarkObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractGeometry getAbstractGeometry() {
        return this.abstractGeometry;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractGeometry(AbstractGeometry abstractGeometry) {
        this.abstractGeometry = abstractGeometry;
    }

    @Override
    public String toString() {
        String resultat = super.toString();
        resultat += "Placemark : ";
        //resultat += "\n\t"+abstractGeometry;
        return resultat;
    }
}
