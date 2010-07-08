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
public class DefaultAbstractContainer extends DefaultAbstractFeature implements AbstractContainer {

    protected DefaultAbstractContainer() {
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
     * @param abstractContainerSimpleExtensions
     * @param abstractContainerObjectExtensions
     */
    protected DefaultAbstractContainer(List<SimpleType> objectSimpleExtensions,
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
            List<SimpleType> abstractContainerSimpleExtensions,
            List<AbstractObject> abstractContainerObjectExtensions) {

        super(objectSimpleExtensions, idAttributes,
                name, visibility, open,
                author, atomLink,
                address, addressDetails,
                phoneNumber, snippet, description,
                view, timePrimitive,
                styleUrl, styleSelector,
                region, extendedData,
                abstractFeatureSimpleExtensions,
                abstractFeatureObjectExtensions);
        if (abstractContainerSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.CONTAINER).addAll(abstractContainerSimpleExtensions);
        }
        if (abstractContainerObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.CONTAINER).addAll(abstractContainerObjectExtensions);
        }
    }

    @Override
    public String toString() {
        String resultat = super.toString()
                + "\n\tAbstractContainerDefault : ";
        return resultat;
    }
}
