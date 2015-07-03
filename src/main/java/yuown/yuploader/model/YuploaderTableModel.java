package yuown.yuploader.model;

import org.springframework.stereotype.Component;

import javax.swing.table.DefaultTableModel;

@Component
public class YuploaderTableModel extends DefaultTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 3855594580035551957L;

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

}
