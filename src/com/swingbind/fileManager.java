package com.swingbind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

class sortparameters {
    public String fname = "default.txt";
    public Hashtable<String, String> paramsTable = new Hashtable<>();
    sortparameters (String sortparamfn) {
        fname = sortparamfn;
    }
    public void processParameters (boolean printParams) {
        BufferedReader br;
        FileReader fr = null;
        String lineString = "";
        String[] parsingArr = new String[2];
        try {
            fr = new FileReader(fname);
            br = new BufferedReader(fr);
            while((lineString = br.readLine()) != null) {
                parsingArr = lineString.split(" ", 2);
                if(printParams)
                    System.out.println("Param 1: " + parsingArr[0] + " Param 2: " + parsingArr[1]);
                paramsTable.put(parsingArr[0], parsingArr[1]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getCorrespondingParam (String fname) {
        String corresponding = "";
        String[] fnameArr = new String[3];
        if(fname.contains(".")) {
            fnameArr = fname.split("\\.", 3);
            fname = ("."+fnameArr[fnameArr.length-1]).toLowerCase(Locale.ROOT);
            System.out.println("FNAME: "+fname);
            corresponding = paramsTable.get(fname);
        }
        return corresponding;
    }
}

class directorysweep {
    File fdirectory;
    String[] directoryList;
    public void getFileList (boolean printParams) {
        directoryList = fdirectory.list();
        if(printParams) {
            System.out.println(fdirectory.getName());
            for(String str : directoryList) {
                System.out.println("-- "+str);
            }
        }
    }
}

public class fileManager {
    private JButton demoButton;
    private JPanel panelMain;
    public static sortparameters params = new sortparameters("parameters.txt");
    public directorysweep dir = new directorysweep();

    public void sweepFiles (String[] directoryList) {
        for(String str : directoryList) {
            params.processParameters(false);
            try {
                Path temp = Files.move(Paths.get(dir.fdirectory.getPath() + "//" + str),
                        Paths.get(dir.fdirectory.getPath() + "//" + params.getCorrespondingParam(str) + "//" + str));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public fileManager() {
        demoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fcReturn;
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fcReturn = fc.showOpenDialog(panelMain);
                if(fcReturn == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    System.out.println(file.getName());
                    dir.fdirectory = file;
                    dir.getFileList(true);
                    sweepFiles(dir.directoryList);
                }
            }
        });
    }

    public static void main (String[] args) {
        //JFrame setup
        JFrame mainFrame = new JFrame("JavaFileManager");
        mainFrame.setContentPane(new fileManager().panelMain);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setPreferredSize(new Dimension(200, 200));
        mainFrame.pack();
        mainFrame.setVisible(true);
        //File based technical stuff
        params.processParameters(false);
    }
}