import java.io.File;

public class Liste_fichiers_xml {

	public static void main(String[] args) {
		String path = new File("").getAbsolutePath();
		File test = new File(path);
		listerRepertoire(test);
	}

	public static void listerRepertoire(File repertoire) {
		String[] listefichiers;
		int i;
		listefichiers = repertoire.list();

		for (i = 0; i < listefichiers.length; i++) {
			if (listefichiers[i].endsWith(".xml") == true) {
				System.out.println(listefichiers[i].substring(0,
						listefichiers[i].length()));
			}
		}
	}
}
