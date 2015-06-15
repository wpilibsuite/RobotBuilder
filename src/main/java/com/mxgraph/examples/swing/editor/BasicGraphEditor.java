
package com.mxgraph.examples.swing.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;

import com.mxgraph.view.mxGraph;

public class BasicGraphEditor extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -6561623072112577140L;

    /**
     * Adds required resources for i18n
     */
    static {
        try {
            mxResources.add("com/mxgraph/examples/swing/resources/editor");
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     *
     */
    protected mxGraphComponent graphComponent;

    /**
     *
     */
    protected mxGraphOutline graphOutline;

    /**
     *
     */
    protected JTabbedPane libraryPane;

    /**
     *
     */
    protected mxUndoManager undoManager;

    /**
     *
     */
    protected String appTitle;

    /**
     *
     */
    protected File currentFile;

    /**
     * Flag indicating whether the current graph has been modified
     */
    protected boolean modified = false;

    /**
     *
     */
    protected mxRubberband rubberband;

    /**
     *
     */
    protected mxKeyboardHandler keyboardHandler;

    /**
     *
     */
    protected mxIEventListener undoHandler = (sender, evt)
            -> undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));

    /**
     *
     */
    protected mxIEventListener changeTracker = (sender, evt) -> setModified(true);

    /**
     *
     */
    public BasicGraphEditor(String appTitle, mxGraphComponent component) {
        // Stores and updates the frame title
        this.appTitle = appTitle;

        // Stores a reference to the graph and creates the command history
        graphComponent = component;
        final mxGraph graph = graphComponent.getGraph();
        undoManager = createUndoManager();

        // Do not change the scale and translation after files have been loaded
        graph.setResetViewOnRootChange(false);

        // Updates the modified flag if the graph model changes
        graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

        // Adds the command history to the model and view
        graph.getModel().addListener(mxEvent.UNDO, undoHandler);
        graph.getView().addListener(mxEvent.UNDO, undoHandler);

        // Keeps the selection in sync with the command history
        mxIEventListener undoSelectionHandler = (sender, evt) -> {
            List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
            graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
        };

        undoManager.addListener(mxEvent.UNDO, undoSelectionHandler);
        undoManager.addListener(mxEvent.REDO, undoSelectionHandler);

        // Creates the graph outline component
        graphOutline = new mxGraphOutline(graphComponent);

        // Creates the library pane that contains the tabs with the palettes
        libraryPane = new JTabbedPane();

        // Creates the inner split pane that contains the library with the
        // palettes and the graph outline on the left side of the window
        JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                libraryPane, graphOutline);
        inner.setDividerLocation(320);
        inner.setResizeWeight(1);
        inner.setDividerSize(6);
        inner.setBorder(null);

        // Creates the outer split pane that contains the inner split pane and
        // the graph component on the right side of the window
        JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner, graphComponent);
        outer.setOneTouchExpandable(true);
        outer.setDividerLocation(200);
        outer.setDividerSize(6);
        outer.setBorder(null);

        // Display some useful information about repaint events
        installRepaintListener();

        // Puts everything together
        setLayout(new BorderLayout());
        add(outer, BorderLayout.CENTER);

        // Installs rubberband selection and handling for some special
        // keystrokes such as F2, Control-C, -V, X, A etc.
        installHandlers();
    }

    /**
     *
     */
    protected mxUndoManager createUndoManager() {
        return new mxUndoManager();
    }

    /**
     *
     */
    protected void installHandlers() {
        rubberband = new mxRubberband(graphComponent);
        keyboardHandler = new EditorKeyboardHandler(graphComponent);
    }

    /**
     *
     */
    protected void installRepaintListener() {
        graphComponent.getGraph().addListener(mxEvent.REPAINT, (source, evt) -> {
            String buffer = (graphComponent.getTripleBuffer() != null) ? "" : " (unbuffered)";
            mxRectangle dirty = (mxRectangle) evt.getProperty("region");
        });
    }

    /**
     *
     */
    public EditorPalette insertPalette(String title) {
        final EditorPalette palette = new EditorPalette();
        final JScrollPane scrollPane = new JScrollPane(palette);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        libraryPane.add(title, scrollPane);

        // Updates the widths of the palettes if the container size changes
        libraryPane.addComponentListener(new ComponentAdapter() {
            /**
             *
             */
            @Override
            public void componentResized(ComponentEvent e) {
                int w = scrollPane.getWidth()
                        - scrollPane.getVerticalScrollBar().getWidth();
                palette.setPreferredWidth(w);
            }

        });

        return palette;
    }

    /**
     *
     */
    public void setCurrentFile(File file) {
        File oldValue = currentFile;
        currentFile = file;

        firePropertyChange("currentFile", oldValue, file);
    }

    /**
     *
     */
    public File getCurrentFile() {
        return currentFile;
    }

    /**
     *
     * @param modified
     */
    public void setModified(boolean modified) {
        boolean oldValue = this.modified;
        this.modified = modified;

        firePropertyChange("modified", oldValue, modified);
    }

    /**
     *
     * @return whether or not the current graph has been modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     *
     */
    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    /**
     *
     */
    public mxGraphOutline getGraphOutline() {
        return graphOutline;
    }

    /**
     *
     */
    public JTabbedPane getLibraryPane() {
        return libraryPane;
    }

    /**
     *
     */
    public mxUndoManager getUndoManager() {
        return undoManager;
    }

    /**
     *
     * @param name
     * @param action
     * @return a new Action bound to the specified string name
     */
    public Action bind(String name, final Action action) {
        return bind(name, action, null);
    }

    /**
     *
     * @param name
     * @param action
     * @return a new Action bound to the specified string name and icon
     */
    @SuppressWarnings("serial")
    public Action bind(String name, final Action action, String iconUrl) {
        AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
                BasicGraphEditor.class.getResource(iconUrl)) : null) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action.actionPerformed(new ActionEvent(getGraphComponent(), e
                                        .getID(), e.getActionCommand()));
                    }
                };

        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

        return newAction;
    }

}
