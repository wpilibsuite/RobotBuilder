/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import robotbuilder.robottree.RobotTree;

    
/**
 *
 * @author alex
 */
public class RelativePathAccessory extends JPanel implements PropertyChangeListener {
    RobotTree tree;
    File file;
    ButtonGroup options;
    JRadioButton relative, absolute;
    JTextField relativePreview, absolutePreview;
    
    public RelativePathAccessory(RobotTree tree) {
        this.tree = tree;
        addComponents();
    }
    
    private void addComponents() {
        setLayout(new GridLayout(4, 1));
        
        relative = new JRadioButton("Use path relative to the RobotBuilder save file.");
        relative.setSelected(true);
        relativePreview = new JTextField(".");
        relativePreview.setEditable(false);
        relativePreview.setEnabled(false);
        relativePreview.setForeground(Color.BLACK);
        
        absolute = new JRadioButton("Use absolute path.");
        absolutePreview = new JTextField("");
        absolutePreview.setEditable(false);
        absolutePreview.setEnabled(false);
        absolutePreview.setForeground(Color.BLACK);
        
        options = new ButtonGroup();
        options.add(relative);
        options.add(absolute);
        
        add(relative);
        add(relativePreview);
        add(absolute);
        add(absolutePreview);
    }
    
    public void attachTo(JFileChooser chooser) {
        System.out.println("Accessory set.");
        chooser.setAccessory(this);
        chooser.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getNewValue() instanceof File) {
            file = (File) e.getNewValue();
            if (tree.getFilePath() == null) {
                relativePreview.setText("File not saved yet.");
            } else {
                relativePreview.setText(RelativePath.getRelativePath(new File(tree.getFilePath()).getParentFile(), file));
            }
            try {
                absolutePreview.setText(file.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(RelativePathAccessory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String getFileName() {
        if (file == null) return null;
        if (relative.isSelected()) {
            return RelativePath.getRelativePath(new File(tree.getFilePath()).getParentFile(), file);
        } else {
            try {
                return file.getCanonicalPath();
            } catch (IOException ex) {
                Logger.getLogger(RelativePathAccessory.class.getName()).log(Level.SEVERE, null, ex);
                return file.getAbsolutePath();
            }
        }
    }

    public String getPathName(File currFile) {
        if (relative.isSelected()) {
            return RelativePath.getRelativePath(new File(tree.getFilePath()).getParentFile(), currFile);
        } else {
            try {
                return currFile.getCanonicalPath();
            } catch (IOException ex) {
                Logger.getLogger(RelativePathAccessory.class.getName()).log(Level.SEVERE, null, ex);
                return currFile.getAbsolutePath();
            }
        }
    }

    public void setRelative(boolean relative) {
        if (relative) this.relative.setSelected(true);
        else this.absolute.setSelected(true);
    }
}
/**
 * this class provides functions used to generate a relative path
 * from two absolute paths
 * @author David M. Howard
 */
class RelativePath {
    /**
     * break a path down into individual elements and add to a list.
     * example : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
     * @param f input file
     * @return a List collection with the individual elements of the path in
     * reverse order
     */
    private static List getPathList(File f) {
        List l = new ArrayList();
        File r;
        try {
            r = f.getCanonicalFile();
            while(r != null) {
                l.add(r.getName());
                r = r.getParentFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            l = null;
        }
        return l;
    }
    
    /**
     * figure out a string representing the relative path of
     * 'f' with respect to 'r'
     * @param r home path
     * @param f path of file
     */
    private static String matchPathLists(List r,List f) {
        int i;
        int j;
        String s;
        // start at the beginning of the lists
        // iterate while both lists are equal
        s = "";
        i = r.size()-1;
        j = f.size()-1;
        
        // first eliminate common root
        while((i >= 0)&&(j >= 0)&&(r.get(i).equals(f.get(j)))) {
            i--;
            j--;
        }
        
        // for each remaining level in the home path, add a ..
        for(;i>=0;i--) {
            s += ".." + File.separator;
        }
        
        // for each level in the file path, add the path
        for(;j>=1;j--) {
            s += f.get(j) + File.separator;
        }
        
        // file name
        if (j != -1) s += f.get(j);
        return s;
    }
    
    /**
     * get relative path of File 'f' with respect to 'home' directory
     * example : home = /a/b/c
     *           f    = /a/d/e/x.txt
     *           s = getRelativePath(home,f) = ../../d/e/x.txt
     * @param home base path, should be a directory, not a file, or it doesn't
     * make sense
     * @param f file to generate path for
     * @return path from home to f as a string
     */
    public static String getRelativePath(File home, File f) {
        File r;
        List homelist;
        List filelist;
        String s;
        
        homelist = getPathList(home);
        filelist = getPathList(f);
        s = matchPathLists(homelist,filelist);
        System.out.println("\t"+homelist+" ~ "+filelist+" --> "+s);
        
        return s;
    }
}
