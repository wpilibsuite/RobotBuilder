
package robotbuilder.actions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

import robotbuilder.MainFrame;
import robotbuilder.Utils;

/**
 *
 * @author alex
 */
public class GettingStartedAction extends AbstractAction {

    private static final String url =
            "https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robotbuilder/introduction/robotbuilder-overview.html";

    public GettingStartedAction() {
        putValue(Action.NAME, "Getting Started");
        putValue(Action.SHORT_DESCRIPTION, "Show the getting started information.");

        EventQueue.invokeLater(() -> {
            if (MainFrame.getInstance().prefs.getBoolean("getting_started.visible", true)) {
                actionPerformed(null);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Utils.browse(url);
        MainFrame.getInstance().prefs.putBoolean("getting_started.visible", false);
    }

}
