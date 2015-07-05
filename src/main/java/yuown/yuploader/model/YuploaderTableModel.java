package yuown.yuploader.model;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class YuploaderTableModel extends DefaultTableModel {

	/**
     * 
     */
	private static final long serialVersionUID = 3855594580035551957L;

	private JTable table;

	Class[] columnTypes = { FileObject.class, String.class, String.class, String.class, Object.class, String.class };

	boolean[] columnEditables = { false, false, false, false, false, false };

	public YuploaderTableModel() {
		super(new Object[0][], new String[] { "File Name", "Size", "Progress", "Status", "Speed", "Time (Seconds)" });
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
			if (Status.IN_PROGRESS != fileSelected.getStatus() && Status.PAUSED != fileSelected.getStatus()) {
				removeRow(selectedRows[i]);
			}
		}
	}

	public void setTable(JTable table) {
		this.table = table;
	}
}