import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class ClassChooser extends JFrame implements ActionListener {
	private static final long serialVersionUID = 42L;
	File file;
	private JMenu fileMenu;
	String line;
	private JFileChooser fc = null;
	BufferedReader br;
	JTextArea ta = new JTextArea();
	private String currentFileBeingEdited = null;

	public ClassChooser() {
		JScrollPane scrollPane = new JScrollPane(ta);
		ta.setText("");
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(scrollPane, BorderLayout.CENTER);
		createFileMenu();
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		this.setContentPane(content);
		this.setTitle("Editeur de texte");
		this.setVisible(true);
		this.setSize(1000, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		item = new JMenuItem("Delete"); // ne marche pas
		item.addActionListener(this);
		fileMenu.add(item);
		fileMenu.addSeparator();
		item = new JMenuItem("Exit"); // ok
		item.addActionListener(this);
		fileMenu.add(item);
	}

	// Ouverture fichier :
	private class OpenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// ADDED ONLY THIS LINE
			ta.setText("");
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setSelectedFile(fc.getCurrentDirectory());
			fc.setDialogTitle("Selection fichier");
			fc.setMultiSelectionEnabled(false);

			int retVal = fc.showOpenDialog(ClassChooser.this);

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
		ClassChooser fce = new ClassChooser();
		String filename = File.separator + "tmp";
		System.out.println(filename);
	}

	// Action sur le menu fichier :
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String menuName;

		menuName = e.getActionCommand();

		if (menuName.equals("Exit")) { // Quitter
			System.exit(0);
		} else if ("Save".equalsIgnoreCase(menuName)) { // Sauvegarder
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
		} else if ("New".equalsIgnoreCase(menuName)) { // Créer nouveau fichier
			ta.setText("");
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setSelectedFile(fc.getCurrentDirectory());
			fc.setDialogTitle("Selection dossier");
			fc.setMultiSelectionEnabled(false);

			fc.showOpenDialog(ClassChooser.this);
			file = fc.getSelectedFile();
			currentFileBeingEdited = file.getAbsolutePath();

			String s = (String) JOptionPane.showInputDialog(null,
					"Chemin selectionné : " + currentFileBeingEdited + "\n"
							+ "Nom du fichier :", "Choix du nom de fichier",
					JOptionPane.PLAIN_MESSAGE, null, null, "Nouveau_fichier");
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
					ta.setText("");
					MyFile.delete();
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Aucun fichier ouvert actuellement.");
			}
		} else {
			System.out.println("Menu Item '" + menuName + "' is selected.");
		}
	}
}