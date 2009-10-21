/**
 * @author Hyacinthe MENIET
 * Created on 15 juil. 07
 */
package org.geotoolkit.test;



/**
 * Unmarshalles the given XML Document.
 * @module pending
 */
public class Test {

    /**
     * The main method.
     * @param args the path to file to read.
     * @throws Exception*/
     
    public static void main(String[] args) throws Exception {

        
        /*
        //String fileName = "test serviceIdentification.xml";
        //String fileName = "test service.xml";
        //String fileName = "test19139.xml";
        String fileName = "test19119.xml";
        // Unmarshalles the given XML file to objects
        JAXBContext context;
        context = JAXBContext.newInstance(ServiceIdentificationImpl.class , MetaDataImpl.class, CoupledResourceImpl.class, InterfaceImpl.class, OperationChainImpl.class,
                OperationChainMetadataImpl.class, OperationImpl.class, OperationMetadataImpl.class, ParameterImpl.class,PlatformNeutralServiceSpecificationImpl.class
                , PlatformSpecificServiceSpecificationImpl.class, PortImpl.class, PortSpecificationImpl.class, ServiceIdentificationImpl.class,
                ServiceImpl.class, ServiceProviderImpl.class, ServiceTypeImpl.class);//, IdentifierImpl.class, ReferenceSystemImpl.class);       
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Marshaller marshaller     = context.createMarshaller();
         try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl("http://www.isotc211.org/2005/srv"));
        } catch (PropertyException e) {
            System.out.println("prefix non trouv");
        }
        
        Object request;
        if (true){

            request = unmarshaller.unmarshal(new FileReader(fileName));
            System.out.println("unmarshalled:" + request.getClass().getSimpleName() +" \n " +  request.toString());
        
        } else {
            List<OperationMetadata> operations = new ArrayList<OperationMetadata>();
            operations.add(new OperationMetadataImpl("operation1"));
            org.opengis.util.LocalName lname = new LocalName("CSW");
            request = new ServiceIdentificationImpl(operations, lname, CouplingType.LOOSE);
            ServiceIdentificationImpl si = (ServiceIdentificationImpl) request;
            
        }
        
        String fileOutput = "output.xml";
        marshaller.marshal(request, new File(fileOutput));

       */
    }
}
