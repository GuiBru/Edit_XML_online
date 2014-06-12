import java.io.File;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

public class Ouverture_fichier {

	public static void main(String[] args) throws DocumentException {

		// déclaration d'un fichier Java standard
		File fichier = new File("TestConfig.xml");

		// création d'un objet de type SAXReader
		SAXReader reader = new SAXReader();

		// lecture de ce fichier à l'aide de ce reader, et construction d'un
		// objet Document
		Document doc = reader.read(fichier);

		// construction de l'élément racine du document XML
		Element root = doc.getRootElement();

		// lecture des attributs de l'élément racine
		List attributes = root.attributes();

		// lecture des sous-élément de la racine
		List elements = root.elements();

		// lecture du nom complet "qualified name" (= espace de nom + nom)
		QName qName = root.getQName();

		// lecture du nom
		String nom = qName.getName();

		// lecture du nom de l'espace de noms
		String nomEspaceDeNoms = qName.getNamespaceURI();

		// lecture du préfixe utilisé pour cet espace de nom
		String nomPrefix = qName.getNamespacePrefix();

		// lecture de l'espace de nom
		Namespace nameSpace = root.getNamespace();

		// lecture de l'URI de cet espace de nom
		String uriNameSpace = nameSpace.getURI();

		// lecture du préfixe utilisé pour référencer cet espace dans le
		// document
		String prefixNameSpace = nameSpace.getPrefix();

		String texte = root.getText();

		Attribute attribute = (Attribute) attributes.iterator().next();
		QName attributeQName = attribute.getQName();
		String value = attribute.getValue();
		String nomAttribut = attribute.getName();
	}
}