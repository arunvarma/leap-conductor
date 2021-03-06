package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

/**
 * Class that 1) retrieves text from text documents 2) finds the folder with the most mp3 files
 * and 3) retrieves song files from previous session
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
	private static File _folder = new File(System.getProperty("user.dir") + "/src/data/sounds");
	
	/*Current maximum number of songs*/
	private int _numMp3Files = numberOfMusicFiles(_folder);
	
	/*String to determine if user has completed tutorial*/
	public static final String TUTORIAL_COMPLETE = "Tutorial Complete";
	
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
			BufferedReader in = new BufferedReader(new FileReader(new File(dir + "/src/data/" + fileName)));
			String line = in.readLine();
			while (line != null){
				sb.append(line + "\n");
				line = in.readLine();
			}
			in.close();
			return sb.toString();
		} catch (IOException e) {
			System.out.println("ERROR: IOException while reading file " + dir + "/src/data/" + fileName);
		}
		return null;
	}
	
	/**
	 * Provides public access to folder with most .mp3, .wav, .mp4, .m4a files
	 */
	public static File getFolderWithMostMusicFiles() {
		return _folder;
	}
	
	/**
	 * Keeps track of whether or not the user has completed the tutorial
	 */
	public static void completeTutorial() {
		try {
			String dir = System.getProperty("user.dir");
    		PrintWriter writer = new PrintWriter(dir + "/src/data/tutorial_complete.txt", "UTF-8");
    		writer.println(TUTORIAL_COMPLETE);
    		writer.close();
		} catch (FileNotFoundException e1) {
			/*ignore and do nothing*/
		} catch (UnsupportedEncodingException e1) {
			/*Ignore and do nothing*/
		}
	}
	
	/**
	 * Determines whether or not the user has completed the tutorial
	 */
	public static boolean hasCompletedTutorial() {

		boolean hasCompleted = false;
		String dir = System.getProperty("user.dir");
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(new File(dir + "/src/data/tutorial_complete.txt")));
			String line = in.readLine();
			
			while (line != null) {
				
				if ( line.equals(TUTORIAL_COMPLETE) )
					hasCompleted = true;

				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			/*Do nothing. If the file does not exist, an empty array will be returned*/
		}
		return hasCompleted;
		
	}
	
	
	/**
	 * Retrieves song files from previous sessions and handles File exceptions
	 */
	public static File[] getSongsFromPreviousSession() {
		
		ArrayList<File> returnFiles = new ArrayList<File>();
		String dir = System.getProperty("user.dir");
		
		try {
			
			BufferedReader in = new BufferedReader(new FileReader(new File(dir + "/src/data/song_files.txt")));
			String line = in.readLine();
			
			while (line != null) {
				
				File song = new File(line);
				
				if (song.exists())
					returnFiles.add(song);
				
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			/*Do nothing. If the file does not exist, an empty array will be returned*/
		}
		return returnFiles.toArray(new File[returnFiles.size()]);
	}
	
	/*NON-STATIC METHODS*/
	
	/**
	 * Recursively traverses file directory starting at file and finds the
	 * folder with the most .mp3, .m4a, .mp4, .wav files in it (ignores hidden files)
	 * @param file
	 */
	private void findDirectoryWithMostMusicFiles(File file) {
		
		if (file.isDirectory()) {
			
			//if the number of mp3 files it contains is greater than the current, update
			int numMp3Files = numberOfMusicFiles(file);
			if (numMp3Files > _numMp3Files) {
				_folder = file;
				_numMp3Files = numMp3Files;
			}
			
			//recursively look through all of the contained files
			File[] children = file.listFiles();
			for (File child : children) {
				//ignore hidden files
				if (!child.getName().startsWith("."))
					findDirectoryWithMostMusicFiles(child);
			}
		}
	}
	
	/**
	 * Determines the number of .mp3, .m4a, .mp4, .wav files in the input folder
	 * @param folder
	 * @return
	 */
	private int numberOfMusicFiles(File folder) {
		int count = 0;
		for (File file: folder.listFiles()) {
			if (file.getName().endsWith(".mp3") ||
				file.getName().endsWith(".mp4") ||
				file.getName().endsWith(".m4a") ||
				file.getName().endsWith(".wav"))
			{
				count++;
			}
				
		}
		return count;
	}
	
	/**
	 * Runs it in a thread so we can start looking at launch and so it doesn't lag up GUI
	 * when the user tries to add new songs
	 * If the user's iTunes library is in /Users/username/Music/iTunes/iTunes Media/Music
	 * then return it set _folder to it right away, and don't look through files
	 */
	@Override
	public void run() {
		File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
		
		File iTunesLib = new File (homeDirectory + "/Music/iTunes/iTunes Media/Music");
		
		if (iTunesLib.exists()) {
			_folder = iTunesLib;
		}
		else {
			findDirectoryWithMostMusicFiles(homeDirectory);
		}

	}
	


}
