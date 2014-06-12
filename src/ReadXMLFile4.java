import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadXMLFile4 {

	public static void main(String[] args) {
		try {
			File file = new File("TestConfig.xml");

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = dBuilder.parse(file);

			if (doc.hasChildNodes()) {
				printNote(doc.getChildNodes(), 1);
			}

		} catch (Exception e) {
			System.out.println("exeption :" + e.getMessage());
		}
	}

	private static void printNote(NodeList nodeList, int aReduire) {

		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				if (tempNode.hasAttributes()) {

					System.out.println("<" + tempNode.getNodeName());

					NamedNodeMap nodeMap = tempNode.getAttributes();
					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node node = nodeMap.item(i);

						System.out.println(node.getNodeName() + "=\""
								+ node.getNodeValue() + "\"");
					}
					System.out.println(">");
				} else {
					System.out.println("<" + tempNode.getNodeName() + ">");
				}

				if (tempNode.hasChildNodes()) {
					printNote(tempNode.getChildNodes(), 1);
				}
				System.out.println("</" + tempNode.getNodeName() + ">");
			}
		}
	}
}