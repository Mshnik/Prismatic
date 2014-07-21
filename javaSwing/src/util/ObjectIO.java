package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JFileChooser;

public class ObjectIO{

	private static final String EXTENSION = ".ser";	//The default extension for serialized objects
	
	/**Creates a serialized Object file for input Object for later reading and editing
	 * @param toWrite - the Serializable Object to write to memory.
	 * @param directory - the location to write the object to
	 * @param name - the name to give to the serialized version of this object. 
	 * 			Must not contain the '.' or the '/' characters. */
	public static void write(Serializable toWrite, String directory, String name) 
			throws IllegalArgumentException, IOException{
		
		if(name.contains(".") || name.contains("/"))
			throw new IllegalArgumentException("Input name contains a '.' or a '/'");
		
		try{
			FileOutputStream fileOut = new FileOutputStream(directory + "/" + name + EXTENSION);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(toWrite);
			out.close();
			fileOut.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	/** Prompts the user to select a serialized object from memory to load.
	 * @param objectClass - The class of the object that the user is expected to return
	 * @param directory - The location the user is prompted to select from, though they can navigate to other locations.
	 * @return - The selected object, loaded from memory
	 * @throws RuntimeException - If the user selects the wrong class of object.
	 */
	public static Object load(Class objectClass , String directory) throws RuntimeException{
		JFileChooser chooser = new JFileChooser(directory);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = chooser.showOpenDialog(null);

		Object g = null;

		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			try{
				FileInputStream fileIn = new FileInputStream(f);
				ObjectInputStream in = new ObjectInputStream(fileIn);

				g = in.readObject();

				in.close();
				fileIn.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if(objectClass.isInstance(g))
			return g;
		
		throw new RuntimeException("Invalid Object Selection - found instance of " + g.getClass() + " required " + objectClass);
	}
	
}
