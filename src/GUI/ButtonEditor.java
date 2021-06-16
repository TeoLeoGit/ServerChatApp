package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Tham khảo table column button tại stackoverflow
public class ButtonEditor extends DefaultCellEditor {
    protected JButton btn;
    private String lbl;
    private Boolean isClicked;

    //test
    public JButton getBtn() {
        return btn;
    }
    public ButtonEditor(JTextField textField) {
        super(textField);
        btn = new JButton();
        btn.setOpaque(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(69,229, 33));

        //btn is clicked
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }
    //Override methods
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        lbl = (value==null)? "":value.toString();
        btn.setText(lbl);
        btn.setForeground(Color.RED);
        isClicked = true;
        return btn;
    }
    //if btn cell value changes, if clicked that is
    @Override
    public Object getCellEditorValue() {
        isClicked = false;
        return new String(lbl);
    }

    @Override
    public boolean stopCellEditing() {
        isClicked = false;

        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
