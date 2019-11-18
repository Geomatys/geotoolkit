package org.geotoolkit.wps.xml.v100;

import org.geotoolkit.wps.xml.XMLBindingTestBuilder;
import org.geotoolkit.wps.xml.v200.Capabilities;
import org.geotoolkit.wps.xml.v200.Execute;
import org.geotoolkit.wps.xml.v200.ProcessOfferings;
import org.geotoolkit.wps.xml.v200.Result;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class BindingV1Test {

    @Test
    public void GetCapabilities() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/WPSCapabilities_Examind.xml", Capabilities.class);
    }

    @Test
    public void describeProcess() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/ProcessDescriptions.xml", ProcessOfferings.class);
    }

    @Test
    public void execute() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/Execute.xml", Execute.class);
        XMLBindingTestBuilder.test("xml/v100/Execute2.xml", Execute.class);
        XMLBindingTestBuilder.test("xml/v100/Execute3.xml", Execute.class);
    }

    @Test
    public void executeChain() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/Execute_chain.xml", Execute.class);
    }

//    @Test
//    public void getStatus() throws Exception {
//        XMLBindingTestBuilder.test("xml/v200/StatusInfo.xml", StatusInfo.class);
//    }

    @Test
    public void result() throws Exception {
        XMLBindingTestBuilder.test("xml/v100/ExecuteResponse.xml", Result.class);
        //XMLBindingTestBuilder.test("xml/v100/ExecuteResponse2.xml", Result.class); //on perd le CData et c normal
    }

    /*@Test
    public void marshTest() throws Exception {

        Format f = new Format("enc", "mime", "schem", Integer.MAX_VALUE);
        //ComplexData cdata = new ComplexData(Arrays.asList(f));
        //cdata.getContent().add("this is a test");
        Data data = new Data(f, "<![CDATA[{this is a test}]]>");
        DataOutput out = new DataOutput("idtest", "titletest", "abstest", data);
        Result res = new Result(Arrays.asList(out), "jid");
        res.setVersion("1.0");
        WPSMarshallerPool.getInstance().acquireMarshaller().marshal(res, new OutputStreamWriter(System.out));

    }*/
}
