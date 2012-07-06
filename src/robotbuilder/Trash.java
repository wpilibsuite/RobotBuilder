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
import java.util.HashSet;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.RobotVisitor;
import robotbuilder.data.RobotWalker;

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
                return true;
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                System.out.println("import[1]");
                if (support.getTransferable().isDataFlavorSupported(RobotTree.ROBOT_COMPONENT_FLAVOR)) {
                    RobotComponent node = null;
                    try {
                        node = (RobotComponent) support.getTransferable().getTransferData(RobotTree.ROBOT_COMPONENT_FLAVOR);
                    } catch (UnsupportedFlavorException e) {
                        System.out.println("UnsupportedFlavor");
                        return false;
                    } catch (IOException e) {
                        System.out.println("IOException");
                        return false;
                    }
                    Set<String> invalid = new HashSet();
                    invalid.add("Robot"); invalid.add("Subsystems");
                    invalid.add("OI"); invalid.add("Commands");
                    if (node == null) return false;
                    if (invalid.contains(node.getBase().getType())) return false;
                    return true;
                }
                return false;
            }
            
            @Override
            public boolean importData(JComponent comp, Transferable t) {
                return true;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) return false;
                System.out.println("import[1]");
                if (support.getTransferable().isDataFlavorSupported(RobotTree.ROBOT_COMPONENT_FLAVOR)) {
                    System.out.println("Moving a robot component");
                    RobotComponent node = null;
                    try {
                        node = (RobotComponent) support.getTransferable().getTransferData(RobotTree.ROBOT_COMPONENT_FLAVOR);
                    } catch (UnsupportedFlavorException e) {
                        System.out.println("UnsupportedFlavor");
                        return false;
                    } catch (IOException e) {
                        System.out.println("IOException");
                        return false;
                    }
                    System.out.println("Imported a robot component: " + node.toString());
                    
                    node.walk(new RobotWalker() {
                        @Override
                        public void handleRobotComponent(RobotComponent self) {
                            MainFrame.getInstance().getCurrentRobotTree().removeName(self.getFullName());
                        }
                    });
                    node.removeFromParent();
                    return true;
                }
                return false;
            }
        });
    }
}
