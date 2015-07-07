package yuown.yuploader.model;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import yuown.yuploader.ui.Client;
import yuown.yuploader.util.Helper;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class YuploaderTableModel extends DefaultTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 3855594580035551957L;

    private JTable table;
    
    @Autowired
    private Client client;

    Class[] columnTypes = { FileObject.class, String.class, String.class, String.class, Object.class, String.class };

    boolean[] columnEditables = { false, false, false, false, false, false };
    
    @Autowired
    private Helper helper;

    private AutowireCapableBeanFactory aw;

    public YuploaderTableModel() {
        super(new Object[0][], new String[] { "File Name", "Size", "Progress", "Status", "Speed", "Time (Seconds)"});
    }

    public Class getColumnClass(int columnIndex) {
        return this.columnTypes[columnIndex];
    }

    public boolean isCellEditable(int row, int column) {
        return this.columnEditables[column];
    }

    public void addRow(FileObject fileObject) {
        if (!contains(fileObject)) {
            addRow(new Object[] { fileObject, fileObject.getKBSize(), fileObject.getProgress(), fileObject.getStatus() });
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
            if (StringUtils.equals(Status.IN_PROGRESS.toString(), fileSelected.getStatus().toString())) {
            	int choice = helper.confirm(client, "Are you Sure to Delete Uploading a File which is in " + fileSelected.getStatus().toString() + " State ?");
            	if(choice == JOptionPane.YES_OPTION) {
        			removeConfirmed(selectedRows, i);
            	}
            } else {
            	removeConfirmed(selectedRows, i);
            }
        }
    }

	private void removeConfirmed(int[] selectedRows, int i) {
		client.setStart(client.getStart() - 1);
		removeRow(selectedRows[i]);
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
