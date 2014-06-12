import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ClassChooser2 extends JFrame implements ActionListener {
	private static final long serialVersionUID = 42L;
	File file;
	private JMenu fileMenu;
	private JMenu arbreMenu;
	String line;
	private JFileChooser fc = null;
	BufferedReader br;
	JTextArea ta = new JTextArea();
	static JTextArea ta2 = new JTextArea();
	private String currentFileBeingEdited = null;
	private String fichierTmp = null;

	public ClassChooser2() {
		JScrollPane scrollPane = new JScrollPane(ta);
		scrollPane.setPreferredSize(new Dimension(750, 200));
		ta.setText("");
		ta2.setEditable(false);
		JScrollPane scrollPane2 = new JScrollPane(ta2);
		scrollPane2.setPreferredSize(new Dimension(430, 200));
		ta.setText("");
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(scrollPane, BorderLayout.EAST);
		content.add(scrollPane2, BorderLayout.WEST);
		createFileMenu();
		createArbreMenu();
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		menuBar.add(arbreMenu);
		actionsGauche();
		actionsDroite();
		this.setContentPane(content);
		this.setTitle("Editeur XML Bêta");
		this.setVisible(true);
		this.setSize(1200, 600);
		ta.setBounds(100, 100, 612, 474);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GestionFenetre();
	}

	// Actions à effectuer lors de modification de la fenêtre
	public void GestionFenetre() {

		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				File MyFile = new File(fichierTmp);
				System.out.println("Suppression de : " + fichierTmp);
				MyFile.delete();
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowOpened(WindowEvent e) {
			}
		});

	}

	// Actions sur l'écran de droite qui influent sur tmp et écran de gauche :
	public void actionsDroite() {
		ta.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				majTmp();
				AffichageGauche();
			}

			public void mousePressed(MouseEvent e) {
				majTmp();
				AffichageGauche();
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
		ta.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				majTmp();
				AffichageGauche();
			}

			public void keyReleased(KeyEvent e) {
			}
		});
	}

	// création et màj du fichier tmp :
	public void majTmp() {
		// création fichier tmp :
		fichierTmp = currentFileBeingEdited;
		fichierTmp = (fichierTmp != null) ? fichierTmp.substring(0,
				fichierTmp.indexOf('.')) : "";
		fichierTmp = fichierTmp + "_tmp.xml";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					fichierTmp)));
			writer.write("");
			writer.close();
		} catch (IOException e10) {
			e10.printStackTrace();
		}

		// écriture dans le fichier tmp :
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(fichierTmp));
			pw.println(ta.getText());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	// Mise à jour écran de gauche :
	public void AffichageGauche() {
		if (currentFileBeingEdited != null
				&& currentFileBeingEdited.matches(".*(.xml)$")) {

			System.out.println("Mise a jour affichage de gauche.");
			ta2.setText("");
			MenuGauche(fichierTmp);
			repaint();
			validate();
			revalidate();
		}
	}

	// Actions sur l'écran de gauche :
	public void actionsGauche() {
		ta2.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				Surligner(e);
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
	}

	// Surlignage des balises sélectionnées (ecran de gauche) :
	public void Surligner(MouseEvent e) {

		ta2.setCaretPosition(ta2.viewToModel(e.getPoint()));

		Highlighter hl = ta2.getHighlighter();
		hl.removeAllHighlights();
		String text = ta2.getText();
		int index = ta2.getCaretPosition();

		System.out.println("caractere cliqué : " + text.charAt(index));

		if (text.charAt(index) != '<' && text.charAt(index) != '\n'
				&& text.charAt(index) != ' ') {
			int index1 = index;
			int index2 = index;
			int longueur = 0;
			char ouvrant = text.charAt(index);
			char fermant = text.charAt(index);
			String baliseSelec = "";
			String typeBalise = "";
			String baliseMultiple = "";

			while (ouvrant != '<') {
				if (ouvrant != '/') {
					index1--;
					ouvrant = text.charAt(index1);
					typeBalise = "ouvrante";
				} else {
					ouvrant = '<';
					typeBalise = "fermante";
				}
			}
			while (fermant != '>') {
				if (fermant != ' ') {
					index2++;
					fermant = text.charAt(index2);
					baliseMultiple = "non";
				} else {
					fermant = '>';
					baliseMultiple = "oui";
				}
			}
			longueur = index2 - index1;
			System.out.println("longueur : " + longueur);
			index = index1 + 1;

			while (index1 != index2 - 1) {
				index1++;
				baliseSelec = baliseSelec + text.charAt(index1);
			}

			System.out.println("Balise détectée : " + baliseSelec);
			String pattern = baliseSelec;

			try {
				hl.addHighlight(index, index + pattern.length(),
						DefaultHighlighter.DefaultPainter);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}

			if (typeBalise == "ouvrante") {
				if (baliseMultiple == "oui") {
					index = text.indexOf(pattern, text.indexOf(">", index));
				} else {
					index = text.indexOf(pattern, index + pattern.length());
				}
			} else if (typeBalise == "fermante") {
				index = text.lastIndexOf("<" + pattern, index - 2) + 1;
			}

			try {
				hl.addHighlight(index, index + pattern.length(),
						DefaultHighlighter.DefaultPainter);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Menu gestion de fichier :
	public void createFileMenu() {
		JMenuItem item;
		fileMenu = new JMenu("File");
		item = new JMenuItem("New"); // ok
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem("Open"); // ok
		item.addActionListener(new OpenListener());
		fileMenu.add(item);
		item = new JMenuItem("Save"); // ok
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem("Rename"); // pas encore traité
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem("Delete"); // ne marche pas : buffers à fermer
		item.addActionListener(this);
		fileMenu.add(item);
		fileMenu.addSeparator();
		item = new JMenuItem("Exit"); // ok
		item.addActionListener(this);
		fileMenu.add(item);
	}

	// Menu de gestion des arbres
	public void createArbreMenu() {
		JMenuItem item;
		arbreMenu = new JMenu("Arbre");
		item = new JMenuItem("Créer");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Effacer");
		item.addActionListener(new OpenListener());
		arbreMenu.add(item);
		item = new JMenuItem("Rennomer");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Copier");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Déplacer");
		item.addActionListener(this);
		arbreMenu.add(item);
	}

	// Ouverture et impression fichier dans affichage de droite
	private class OpenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// ADDED ONLY THIS LINE
			ta.setText("");
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setSelectedFile(fc.getCurrentDirectory());
			fc.setDialogTitle("Selection fichier");
			fc.setMultiSelectionEnabled(false);

			int retVal = fc.showOpenDialog(ClassChooser2.this);
			if (retVal == fc.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				currentFileBeingEdited = file.getAbsolutePath();
				try {
					br = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					line = br.readLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (line != null) {
					ta.append(line + "\n");
					try {
						line = br.readLine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}

	// Lecture d'un fichier au format XML:
	private static void MenuGauche(String cheminfichier) {
		try {
			File file = new File(cheminfichier);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			// System.out.println("affichage test 3");
			// bug pour la création d'un nouveau fichier...
			Document doc = dBuilder.parse(file);
			// System.out.println("affichage test 4");
			if (doc.hasChildNodes()) {
				printNote(doc.getChildNodes(), 1);
			}
		} catch (Exception e50) {
			System.out.println(e50.getMessage());
		}
	}

	// Remplissage du champ de gauche :
	private static void printNote(NodeList nodeList, int aReduire) {
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.hasAttributes()) {
					ta2.append("<" + tempNode.getNodeName());
					NamedNodeMap nodeMap = tempNode.getAttributes();
					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node node = nodeMap.item(i);
						ta2.append(" " + node.getNodeName() + "=\""
								+ node.getNodeValue() + "\" ");
					}
					ta2.append(">\n");
				} else {
					ta2.append("<" + tempNode.getNodeName() + ">");
				}

				if (tempNode.hasChildNodes()) {
					printNote(tempNode.getChildNodes(), 1);
				}
				ta2.append("</" + tempNode.getNodeName() + ">\n");
			}
		}
	}

	// Classe main :
	public static void main(String[] args) {
		new ClassChooser2();
	}

	// Sauvegarde fichier :
	private void Sauvegarder(String currentFileBeingEdited) {
		PrintWriter pw = null;
		System.out.println("Fichier : " + currentFileBeingEdited
				+ " sauvegardé.");
		try {
			pw = new PrintWriter(new File(currentFileBeingEdited));
			pw.println(ta.getText());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	public void creerFichier() {
		ta.setText("");
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(fc.getCurrentDirectory());
		fc.setDialogTitle("Selection dossier");
		fc.setMultiSelectionEnabled(false);

		int choix = fc.showOpenDialog(ClassChooser2.this);
		file = fc.getSelectedFile();
		currentFileBeingEdited = file.getAbsolutePath();

		if (choix == fc.APPROVE_OPTION) {
			String s = (String) JOptionPane.showInputDialog(null,
					"Chemin selectionné : " + currentFileBeingEdited + "\n"
							+ "Nom du fichier (avec extension si besoin) : ",
					"Choix du nom de fichier", JOptionPane.PLAIN_MESSAGE, null,
					null, "Nouveau_fichier");
			// Ajout extension ??

			if ((s != null) && (s.length() > 0)) {
				currentFileBeingEdited = currentFileBeingEdited + "\\" + s;
				// creation du fichier :
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							new File(currentFileBeingEdited)));
					writer.write("");
					writer.close();
				} catch (IOException e10) {
					e10.printStackTrace();
				}
				// ouverture du fichier cree :
				file = new File(currentFileBeingEdited);
				try {
					br = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					line = br.readLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (line != null) {
					ta.append(line + "\n");
					try {
						line = br.readLine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "Fichier non créé.");
			}
		}
	}

	// Action sur le menu fichier :
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String menuName;
		menuName = e.getActionCommand();

		if (menuName.equals("Exit")) { // Quitter
			System.exit(0);
		} else if ("Save".equalsIgnoreCase(menuName)) { // Sauvegarder
			Sauvegarder(currentFileBeingEdited);
		} else if ("New".equalsIgnoreCase(menuName)) { // Créer nouveau fichier
			creerFichier();
		} else if ("Delete".equalsIgnoreCase(menuName)) { // Supprimer fichier
			if (currentFileBeingEdited != null) {
				Object[] options = { "Oui", "Non" };
				int n = JOptionPane
						.showOptionDialog(null, "Suppression de "
								+ currentFileBeingEdited,
								"Confirmation requise",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[1]);
				if (n == 0) {
					File MyFile = new File(currentFileBeingEdited);
					System.out.println("suppression de "
							+ currentFileBeingEdited);
					// erreur à cet endroit !!
					MyFile.delete();
					ta.setText("");
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Aucun fichier ouvert actuellement.");
			}
		} // reste rennomage à faire : copie + création + suppression
	}
}