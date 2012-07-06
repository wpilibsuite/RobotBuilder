/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;
import robotbuilder.data.RobotComponent;

/**
 *
 * @author alex
 */
public class Trash extends JLabel {
    public Trash() {
        super("[Trash]");
        setHorizontalAlignment(CENTER);
        setPreferredSize(new Dimension(200, 100));
        setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
                System.out.println("canImport[0]");
                return true;
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                System.out.println("canImport[1]");
                return true;
            }
            
            @Override
            public boolean importData(JComponent comp, Transferable t) {
                System.out.println("import[0]");
                return true;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                System.out.println("import[1]");
                if (support.getTransferable().isDataFlavorSupported(RobotTree.ROBOT_COMPONENT_FLAVOR)) {
                    System.out.println("Moving a robot component");
                    RobotComponent newNode = null;
                    try {
                        newNode = (RobotComponent) support.getTransferable().getTransferData(RobotTree.ROBOT_COMPONENT_FLAVOR);
                    } catch (UnsupportedFlavorException e) {
                        System.out.println("UnsupportedFlavor");
                        return false;
                    } catch (IOException e) {
                        System.out.println("IOException");
                        return false;
                    }
                    System.out.println("Imported a robot component: " + newNode.toString());
                    MainFrame.getInstance().getCurrentRobotTree().removeName(newNode.getFullName());
                    newNode.removeFromParent();
                    return true;
                }
                return false;
            }
        });
    }
}
