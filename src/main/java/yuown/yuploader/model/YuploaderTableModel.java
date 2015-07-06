package yuown.yuploader.model;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.ftp.StreamListener;
import yuown.yuploader.ui.PauseResumeButton;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class YuploaderTableModel extends DefaultTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 3855594580035551957L;

    private JTable table;

    Class[] columnTypes = { FileObject.class, String.class, String.class, String.class, Object.class, String.class, PauseResumeButton.class };

    boolean[] columnEditables = { false, false, false, false, false, false, true };

    private AutowireCapableBeanFactory aw;

    public YuploaderTableModel() {
        super(new Object[0][], new String[] { "File Name", "Size", "Progress", "Status", "Speed", "Time (Seconds)", "Pause" });
    }

    public Class getColumnClass(int columnIndex) {
        return this.columnTypes[columnIndex];
    }

    public boolean isCellEditable(int row, int column) {
        return this.columnEditables[column];
    }

    public void addRow(FileObject fileObject) {
        if (!contains(fileObject)) {
            PauseResumeButton pauseResume = aw.createBean(PauseResumeButton.class);
            pauseResume.setText("Pause");
            pauseResume.addActionListener(aw.createBean(StreamListener.class));
            TableColumn pauseColumn = table.getColumn("Pause");
            pauseColumn.setCellRenderer(pauseResume);
            pauseColumn.setCellEditor(pauseResume);
            addRow(new Object[] { fileObject, fileObject.getKBSize(), fileObject.getProgress(), fileObject.getStatus(), "", "", pauseResume });
            table.updateUI();
        }
    }

    private boolean contains(FileObject fileObject) {
        boolean contains = false;
        int rowCount = getRowCount();
        for (int i = 0; i < rowCount; i++) {
            if (getValueAt(i, 0).equals(fileObject)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public void removeSelectedRows() {
        int[] selectedRows = table.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            FileObject fileSelected = (FileObject) getValueAt(selectedRows[i], 0);
            if (Status.IN_PROGRESS != fileSelected.getStatus() && Status.PAUSED != fileSelected.getStatus()) {
                removeRow(selectedRows[i]);
                table.updateUI();
            }
        }
    }

    public void setTable(JTable table) {
        this.table = table;
    }
    
    public JTable getTable() {
        return table;
    }

    public void setAutoWireBeanCapableFactory(AutowireCapableBeanFactory aw) {
        this.aw = aw;
    }
}
