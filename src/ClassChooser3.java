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
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ClassChooser3 extends JFrame implements ActionListener {
	private static final long serialVersionUID = 42L;
	File file;
	private JMenu fileMenu;
	private JMenu arbreMenu;
	String line;
	private JFileChooser fc = null;
	BufferedReader br;
	JTextArea ta = new JTextArea();
	private String currentFileBeingEdited = null;
	private String fichierTmp = null;
	JScrollPane scrollPane2 = new JScrollPane();
	JScrollPane scrollPane = new JScrollPane(ta);
	int currentCaretPosition;

	public ClassChooser3() {
		scrollPane.setPreferredSize(new Dimension(750, 200));
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

		actionsDroite();

		this.setContentPane(content);
		this.setTitle("Editeur XML Bêta");
		this.setVisible(true);
		this.setSize(1200, 600);
		ta.setBounds(100, 100, 612, 474);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GestionFenetre();
	}

	// a finir (à peine commencé)
	public void IntendationAuto() {
		String text = ta.getText();
		char carEnCours = text.charAt(1);
		int positionCar = 1;

		while (positionCar != text.length() - 1) {
			if (carEnCours == '\n') {
				System.out.println("saut ligne trouve !");
				if (text.charAt(positionCar) != '\t') {
					System.out.println("Mais pas de tabulation");
				}
			}
			carEnCours = text.charAt(positionCar);
			positionCar++;
		}
	}

	public void AjoutArbre(String name) {
		Document doc = null;
		String filename = name;
		boolean showDetails = false;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);

			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(filename);
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(scrollPane2, filename
					+ " was not found", "Warning", JOptionPane.WARNING_MESSAGE);
			System.out.println("erreur!");
			System.exit(0);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(scrollPane2, ex.getMessage(),
					"Exception", JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
			System.exit(0);
		}

		Node root = (Node) doc.getDocumentElement();
		scrollPane2.setViewportView(new XML2JTree(root, showDetails));

		// IntendationAuto();
	}

	// Actions à effectuer lors de modification de la fenêtre
	public void GestionFenetre() {

		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				suppressionTmp(fichierTmp);
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

	// suppression de tous les fic tmp :
	public void suppressionTmp(String nomFic) {
		if (currentFileBeingEdited == null) {
			File file2 = new File("");
			System.out.println("Chemin selec : " + file2.getAbsolutePath());
			walkDir(new File(file2.getAbsolutePath()),
					Pattern.compile(".*tmp.xml"));
		} else {
			File file2 = new File(currentFileBeingEdited);
			String dossierCourant = file2.getAbsolutePath().substring(0,
					file2.getAbsolutePath().lastIndexOf('\\'));
			File file3 = new File(file2.getAbsolutePath().substring(0,
					file2.getAbsolutePath().lastIndexOf('\\')));
			System.out.println(dossierCourant);
			walkDir(new File(file3.getAbsolutePath()),
					Pattern.compile(".*tmp.xml"));
		}
	}

	// supprime les fichiers avec une certaine extension :
	private static void walkDir(final File dir, final Pattern pattern) {
		final File[] files = dir.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (pattern.matcher(file.getName()).matches()) {
					System.out.println("Supprime : " + file.getAbsolutePath());
					File MyFile = file;
					MyFile.delete();
				}
			}
		}
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
				if (fichierTmp != null && currentFileBeingEdited != null
						&& isWellFormed.renvoie_bool(fichierTmp) == true) {
					AjoutArbre(fichierTmp);
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
		ta.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				majTmp();
				if (fichierTmp != null && currentFileBeingEdited != null
						&& isWellFormed.renvoie_bool(fichierTmp) == true) {
					AjoutArbre(fichierTmp);
				}
				if (e.getKeyChar() != '\u0008' && e.getKeyChar() != '\u007F') {
					// ni un return ni un suppr
					ProposeFermetureBalise();
				}
			}
		});
	}

	public void ProposeFermetureBalise() {
		String text = ta.getText();
		char lastCaretTyped;

		int currentCaretPosition = ta.getCaretPosition();
		lastCaretTyped = text.charAt(currentCaretPosition - 1);
		System.out.println("Dernier char tape : " + lastCaretTyped);

		if (lastCaretTyped == '>') {
			// vérification du format " <contenu> " et ...

			// Si oui, récupération de "contenu" puis ...

			// ... vérification existence de </contenu> et ...

			// Si non, création de </contenu>

		}

		// ta.insert(String str, int pos);
		// Inserts the specified text at the specified position in this text
		// area.
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

		// écriture (màj) dans le fichier tmp :
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
		item = new JMenuItem("Delete"); // ne marche pas : buffers à fermer...
										// a l'air de marcher maintenant ?!
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
			ta.setText("");
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setSelectedFile(fc.getCurrentDirectory());
			fc.setDialogTitle("Selection fichier");
			fc.setMultiSelectionEnabled(false);

			int retVal = fc.showOpenDialog(ClassChooser3.this);
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

	// Classe main :
	public static void main(String[] args) {
		new ClassChooser3();
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

		int choix = fc.showOpenDialog(ClassChooser3.this);
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
			// (optionnel)
	}
}