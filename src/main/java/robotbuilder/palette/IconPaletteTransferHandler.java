
package robotbuilder.palette;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

/**
 *
 * @author brad
 */
public class IconPaletteTransferHandler extends TransferHandler {

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        return new Transferable() {
            DataFlavor[] flavors = {DataFlavor.stringFlavor};
            Object data = ((JLabel) c).getName();

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor df) {
                for (DataFlavor flavor : flavors) {
                    if (flavor.equals(df)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
                return data;
            }
        };
    }

}
