package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nikolaos Bousios (Rambou)
 */
public class Writter {

    private PrintWriter writer;
    private boolean console = true;

    public Writter(String LOG_PATH, boolean console) {
        this.console = console;
        try {
            //Create Log File
            writer = new PrintWriter(new FileOutputStream(new File(System.getProperty("user.dir") + "\\" + LOG_PATH)), true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GeneralFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void outln(String msg) {
        writer.println(msg);
        if (console) {
            System.out.println(msg);
        }
    }

    public void out(String msg) {
        writer.print(msg);
        if (console) {
            System.out.print(msg);
        }
    }

    public void close() {
        writer.close();
    }

}
