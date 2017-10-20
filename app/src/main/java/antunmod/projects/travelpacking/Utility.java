package antunmod.projects.travelpacking;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by antun on 10/17/2017.
 */

public class Utility {

    protected static int EXTENSION_LENGTH = 4;
    protected static String TEXT_EXTENSION = ".txt";
    protected static String IMAGE_EXTENSION = ".jpg";


    /*
    The name is modified so that it doesn't start or end with a space.
     */
    protected static String modifyName(String name) {
        //remove spaces before
        while(name.startsWith(" ")) {
            name = name.substring(1);
        }

        //remove spaces after
        while(name.endsWith(" ")) {
            name = name.substring(0, name.length() - 1 );
        }

        return name;
    }


    /*
    The following method created a folder at the given path.
     */
    public static void createFolder(String path) {
        File directory = new File(path);
        if(!directory.exists())
            directory.mkdir();
    }

    /*
    The following method creates a file of some type at the given path.
     */
    public static void createFile(String path, String fileName) {
        File directory = new File(path);
        if(!directory.exists())
            directory.mkdir();
    }

    /*
    The following method validates a name by checking whether it is empty by comparing
    the given name length with the EXTENSION_LENGTH.
     */
    protected static boolean nameIsValid(String name) {

        // If imageName is empty
        if (name.length()== EXTENSION_LENGTH) {
            return false;
        }

        return true;
    }

    /*
    The following method returns true if a file with the name @filename is found at the path
    from string @fileLocation.
     */
    protected static boolean filenameExists (String fileLocation, String filename) {
        File dir = new File(fileLocation);
        File[] itemList = dir.listFiles();
        for(File f : itemList) {
            if(f.getName().equals(filename))
                return true;
        }
        return false;
    }

    protected static String removeExtension (String name) {
        name = name.substring(0, name.length() - EXTENSION_LENGTH);
        return name;
    }
}