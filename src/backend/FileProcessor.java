package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

/**
 * Class that 1) retrieves text from text documents and 2) finds the folder with the most mp3 files
 * It implements runnable so we can start looking for the folder with most mp3's on startup
 * If the user tries to add a song before the traversal is finished, he/she will open up the
 * folder that is currently marked as having the most .mp3 files (defaulted to /src/sounds)
 * @author abok
 *
 */
public class FileProcessor extends Thread {

	/*Define valid file names to read from*/
	public static final String README_FILENAME = "README";
	public static final String CONTROLS_FILENAME = "Controls.txt";
	
	/*Folder with most mp3's - default is /src/sounds*/
	private static File _folder = new File(System.getProperty("user.dir") + "/src/sounds");
	
	private int _numMp3Files = numberOfMp3Files(_folder);
	
	/**
	 * Empty constructor - needed so we can start thread
	 */
	public FileProcessor() {
		/*do nothing*/
	}
	
	/**
	 * Returns all text from a given file name as a String
	 * @param fileName must be a valid file name as defined by this class (returns null otherwise)
	 * @return
	 */
	public static String getTextFromFile(String fileName) {
		
		if (!fileName.equals(README_FILENAME) && !fileName.equals(CONTROLS_FILENAME)) {
			System.out.println("ERROR: Invalid file name. FileProcessor.getTextFromFile()");
			return null;
		}
		
		String dir = System.getProperty("user.dir");
		StringBuilder sb = new StringBuilder();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(dir + "/" + fileName)));
			String line = in.readLine();
			while (line != null){
			  sb.append(line + "\n");
			  line = in.readLine();
			}
			in.close();
			return sb.toString();
		} catch (IOException e) {
			System.out.println("ERROR: IOException while reading file " + dir + "/" + fileName);
		}
		return null;
	}
	
	/**
	 * Provides public access to folder with most .mp3 files
	 */
	public static File getFolderWithMostMp3Files() {
		return _folder;
	}
	
	
	/*NON-STATIC METHODS*/
	
	/**
	 * Recursively traverses file directory starting at file and finds the
	 * folder with the most .mp3 files in it (ignores hidden files)
	 * @param file
	 */
	private void findDirectoryWithMostMp3Files(File file) {
		
		if (file.isDirectory()) {
			
			//if the number of mp3 files it contains is greater than the current, update
			int numMp3Files = numberOfMp3Files(file);
			if (numMp3Files > _numMp3Files) {
				_folder = file;
				_numMp3Files = numMp3Files;
			}
			
			//recursively look through all of the contained files
			File[] children = file.listFiles();
			for (File child : children) {
				//ignore hidden files
				if (!child.getName().startsWith("."))
					findDirectoryWithMostMp3Files(child);
			}
		}
	}
	
	/**
	 * Determines the number of .mp3 files in the input folder
	 * @param folder
	 * @return
	 */
	private int numberOfMp3Files(File folder) {
		int count = 0;
		for (File file: folder.listFiles()) {
			if (file.getName().endsWith(".mp3"))
				count++;
		}
		return count;
	}
	
	/**
	 * Runs it in a thread so we can start looking at launch and so it doesn't lag up GUI
	 * when the user tries to add new songs
	 */
	@Override
	public void run() {
		File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
		findDirectoryWithMostMp3Files(homeDirectory);
	}
	


}
