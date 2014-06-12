import java.io.File;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadXMLFile3 {

	static int j = 0;

	public static void main(String[] args) {
		try {

			File file = new File("TestConfig.xml");

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = dBuilder.parse(file);

			// System.out.println("<" + doc.getDocumentElement().getNodeName()
			// + ">");

			if (doc.hasChildNodes()) {
				printNote(doc.getChildNodes(), 1);
			}

		} catch (Exception e) {
			System.out.println("exeption :" + e.getMessage());
		}

		Scanner saisieUtilisateur = new Scanner(System.in);
		System.out.println("Veuillez saisir un mot :");
		String str = saisieUtilisateur.next();
		saisieUtilisateur.close();
	}

	private static void printNote(NodeList nodeList, int aReduire) {

		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				// System.out.println("<" + tempNode.getNodeName() + ">");

				/*
				 * if (j != 0 && j != 1) {
				 * System.out.println(tempNode.getTextContent()); }
				 */

				if (tempNode.hasAttributes()) {

					System.out.println("<" + tempNode.getNodeName());

					NamedNodeMap nodeMap = tempNode.getAttributes();
					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node node = nodeMap.item(i);
						// System.out.println("Nom attribut : "
						// + node.getNodeName());
						// System.out.println("Valeur attribut : "
						// + node.getNodeValue());

						System.out.println(node.getNodeName() + "=\""
								+ node.getNodeValue() + "\"");
					}
					System.out.println(">");
				} else {
					System.out.println("<" + tempNode.getNodeName() + ">");
				}

				if (tempNode.hasChildNodes()) {
					j = j + 1;
					printNote(tempNode.getChildNodes(), 1);
				}
				System.out.println("</" + tempNode.getNodeName() + ">");
			}
		}
	}
}