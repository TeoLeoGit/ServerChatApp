package GUI;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

//Tham khao Column button tai stackoverflow
public class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        //set
        setOpaque(false);
        //transparent
        setBackground(Color.WHITE);
        setForeground(new Color(69,229, 33));
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "":value.toString());
        return this;
    }
}
