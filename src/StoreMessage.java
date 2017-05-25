//*import org.apache.axis.client.Call;*/

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.TransformerException;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Created by Ольга on 08.04.2017.
 */
public class StoreMessage {
    static String fileXml;
    static SOAPMessage reqMess,respMess;
    public void createSOAPRequest() throws SOAPException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        reqMess = factory.createMessage();
        SOAPPart soapPart = reqMess.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPHeader header = envelope.getHeader();
        SOAPBody body = envelope.getBody();
        header.detachNode();
        Name bodyName = envelope.createName("GetFile");
        SOAPBodyElement bodyElement = 	body.addBodyElement(bodyName);

        Name name = envelope.createName("file");
        SOAPElement fileName = bodyElement.addChildElement(name);
        fileName.addTextNode(fileXml);
        MimeHeaders headers=reqMess.getMimeHeaders();
        headers.addHeader("SOAPAction","");
    }

    private static Element getSingleChild(Element element, String childName){
        NodeList nlist = element.getElementsByTagName(childName);
        return (Element)nlist.item(0);
    }

    public static void displayMessage(SOAPMessage msg) throws SOAPException, TransformerException, IOException, ParserConfigurationException {
        SOAPBody body = msg.getSOAPBody();
        NodeList list = body.getElementsByTagName("company");
        // System.out.println(list.getLength());
        System.out.println("company: ");
        // System.out.println(list.getLength());

        for (int i = 0; i < list.getLength(); i++) {
            System.out.print("\n");
            Element vehicle = (Element) list.item(i);
            System.out.println(vehicle.getAttribute("ID"));

            NodeList list2 = vehicle.getElementsByTagName("type");
            for (int i2 = 0; i2 < list2.getLength(); i2++) {
                Element model = (Element) list2.item(i2);
                System.out.print("\n");
                System.out.print("type: ");
                System.out.println(model.getAttribute("ID"));
                System.out.print("code: ");
                System.out.println(getSingleChild(model, "code").getTextContent().trim());
                System.out.print("model: ");
                System.out.println(getSingleChild(model, "model").getTextContent().trim());
                System.out.print("price: ");
                System.out.println(getSingleChild(model, "price").getTextContent().trim());
            }
        }
    }

    public void start(){

        BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter the file XML:");
            fileXml = br.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SOAPException, MalformedURLException, TransformerException {

        StoreMessage client = new StoreMessage();
        client.start();
        client.createSOAPRequest();

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnectionFactory.createConnection();

        URL endpoint = new 	URL("http://localhost:8080/axis/services/StoreService");

        respMess = connection.call(reqMess, endpoint);
        try {
            displayMessage(respMess);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}



