
package robotbuilder.palette;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.FileNotFoundException;

import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import robotbuilder.MainFrame;
import robotbuilder.Utils;
import robotbuilder.data.PaletteComponent;
import robotbuilder.extensions.ExtensionComponent;
import robotbuilder.extensions.Extensions;

/**
 *
 * @author alex
 */
public class IconView extends JPanel {

    Palette palette;

    public IconView(Palette palette) {
        this.palette = palette;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        TreeModel model = palette.getPaletteModel();
        Enumeration children = ((DefaultMutableTreeNode) model.getRoot()).children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            add(new IconSection(child));
        }
    }

    private static class IconSection extends JPanel {

        private IconSection(DefaultMutableTreeNode node) {
            //BorderFactory.createT
            setBorder(BorderFactory.createTitledBorder(node.getUserObject().toString()));
            TitledBorder border = ((TitledBorder) getBorder());
            border.setTitleFont(new Font("Arial", Font.BOLD, 12));
            border.setTitleJustification(TitledBorder.CENTER);

            setLayout(new GridLayout(0, 2, 5, 5));

            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                PaletteComponent component = (PaletteComponent) ((DefaultMutableTreeNode) children.nextElement()).getUserObject();
                add(new PaletteIcon(component));
            }
        }
    }

    private static class PaletteIcon extends JLabel {

        PaletteComponent component;

        private PaletteIcon(final PaletteComponent comp) {
            this.component = comp;
            try {
                setIcon(new ImageIcon(Utils.getResource("/icons/" + component.getName() + ".png")));
            } catch (FileNotFoundException ex) {
                setIcon(new ImageIcon(Extensions.EXTENSIONS_FOLDER_PATH + component.getName() + "/" + ExtensionComponent.ICON_FILE_NAME));
            }
            setToolTipText(component.getName());
            setName(component.getName());
            setTransferHandler(new IconPaletteTransferHandler());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    MainFrame.getInstance().setHelp(component);
                    JComponent comp = (JComponent) e.getSource();
                    TransferHandler th = comp.getTransferHandler();
                    th.exportAsDrag(comp, e, TransferHandler.COPY);
                }
            });
        }
    }
}
