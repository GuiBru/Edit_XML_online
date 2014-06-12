import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadXMLFile2 {

	public static void main(String[] args) {

		try {

			// File file = new File(args[1]);
			File file = new File("staff.xml");

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = dBuilder.parse(file);

			System.out.println("Racine = "
					+ doc.getDocumentElement().getNodeName());

			// Le document peut être lu :
			if (doc.hasChildNodes()) {
				printNote(doc.getChildNodes());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private static void printNote(NodeList nodeList) {

		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);

			// L'élement est bien un node :
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				// Nom et valeur du node :
				System.out.println("\nNom balise = " + tempNode.getNodeName()
						+ " [OUVERTURE BALISE]");
				System.out.println("Contenu balise = "
						+ tempNode.getTextContent());

				// Si attributs du node :
				if (tempNode.hasAttributes()) {
					NamedNodeMap nodeMap = tempNode.getAttributes();

					for (int i = 0; i < nodeMap.getLength(); i++) {

						Node node = nodeMap.item(i);
						System.out.println("Nom attribut : "
								+ node.getNodeName());
						System.out.println("Valeur attribut : "
								+ node.getNodeValue());
					}
				}

				// boucle pour les nodes enfants
				if (tempNode.hasChildNodes()) {
					printNote(tempNode.getChildNodes());
				}
				System.out.println("Nom balise = " + tempNode.getNodeName()
						+ " [FERMETURE BALISE]");
			}
		}
	}
}