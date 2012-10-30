
package robotbuilder;

import robotbuilder.robottree.RobotTree;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import robotbuilder.data.RobotComponent;
import robotbuilder.data.properties.Property;

/**
 *
 * @author brad
 */
public class PropertiesDisplay extends JPanel {
    public JTable propTable;
    TableModel propTableModel;
    RobotComponent currentComponent;
    String[] keys;
    RobotTree robot;
    
    public PropertiesDisplay() {
        setLayout(new BorderLayout());
	propTableModel = new PropertiesTableModel();
	propTable = new PropertiesTable(propTableModel);
        add(new JScrollPane(propTable), BorderLayout.CENTER);
        propTable.setFillsViewportHeight(true);
        propTable.getTableHeader().setReorderingAllowed(false);
    }

    public void setCurrentComponent(DefaultMutableTreeNode node) {
        currentComponent = (RobotComponent) node;
        keys = currentComponent.getPropertyKeys();
        update();
    }
    
    public void setEditName() {
        propTable.editCellAt(0, 1);
        propTable.requestFocusInWindow();
        ((JTextField) ((DefaultCellEditor) propTable.getCellEditor()).getComponent()).selectAll();
    }
    
    public void update() {
        updateUI();
    }

    public void setRobotTree(RobotTree robot) {
        this.robot = robot;
    }
    
    class PropertiesTable extends JTable {

        private PropertiesTable(TableModel propTableModel) {
            super(propTableModel);
        }
        
        @Override
        public TableCellEditor getCellEditor(final int row, final int column) {
            Object value = super.getValueAt(row, column);
            if(value != null) {
                if (value instanceof JComboBox) {
                    return new DefaultCellEditor((JComboBox) value);
                } else if (value instanceof JFileChooser) {
                    return new FileCellEditor((JFileChooser) value);
                } else if (value instanceof Boolean) {
                    JCheckBox checkbox = new JCheckBox("", (Boolean) this.getValueAt(row, column));
                    checkbox.setOpaque(false);
                    TableCellEditor editor = new DefaultCellEditor(checkbox);
                    editor.getTableCellEditorComponent(propTable, null, true, row, column).setBackground(Color.BLUE);
                    return editor;
                }
                DefaultCellEditor editor = (DefaultCellEditor) getDefaultEditor(value.getClass());
                editor.setClickCountToStart(1);
                return editor;
            }
            final TableCellEditor editor = super.getCellEditor(row, column);
            editor.addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent ce) {}

                @Override
                public void editingCanceled(ChangeEvent ce) {
                    setValueAt(editor.getCellEditorValue(), row, column);
                }
            });
            return editor;
        }

        @Override
        public TableCellRenderer getCellRenderer(final int row, final int column) {
            final Object value = super.getValueAt(row, column);
            if (column == 0) {
                return new TableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                        JLabel label = new JLabel(value.toString());
                        label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
                        Property property = currentComponent.getProperty(keys[row]);
                        if (!property.isValid()) {
                            label.setBackground(new Color(255, 150, 150));
                            label.setOpaque(true);
                            label.setToolTipText(property.getErrorMessage());
                            
                        } else {
                            label.setForeground(Color.black);
                        }
                        return label;
                    }
                };
            }
            if (value != null) {
                if (value instanceof JComboBox) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            try {
                                return new JLabel(((JComboBox) value).getSelectedItem().toString());
                            } catch (NullPointerException ex) {
                                return new JLabel("No Choices Available");
                            }
                        }
                    };
                } else if (value instanceof JFileChooser) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            try {
                                String path = ((JFileChooser) value).getSelectedFile().getPath();
                                setValueAt(path, row, column);
                                return new JLabel(path);
                            } catch (NullPointerException e) {
                                return new JLabel("Click to Select");
                            }
                        }
                    };
                } else if (value instanceof Boolean) {
                    return new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                            JCheckBox checkbox = new JCheckBox("", (Boolean) value);
                            checkbox.setOpaque(false);
                            return checkbox;
                        }
                    };
                }
                return getDefaultRenderer(value.getClass());
            }
            return super.getCellRenderer(row, column);
        }
        
    }
    
    class PropertiesTableModel extends AbstractTableModel {

        @Override
	public String getColumnName(int col) {
            if (col == 0)
                return "Property";
            else
                return "Value";
        }
        
        @Override
	public int getRowCount() {
            if (currentComponent == null) {
                return 0;
            } else {
                return currentComponent.getPropertyKeys().length;
            }
	}

	@Override
	public int getColumnCount() {
	    return 2;
	}

	@Override
	public Object getValueAt(int row, int column) {
            if (column == 0)
                return currentComponent.getProperty(keys[row]).getName();
            else
                return currentComponent.getProperty(keys[row]).getDisplayValue();
	}
        
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 1 && currentComponent.getProperty(keys[row]).isEditable();
        }
        
        @Override
        public void setValueAt(Object val, int row, int column) {
            assert column == 1; // TODO: Deal with more cleanly
            final String key = keys[row];
            currentComponent.getProperty(key).setValue(val);
            update();
            //robot.update();
        }
    }
}
