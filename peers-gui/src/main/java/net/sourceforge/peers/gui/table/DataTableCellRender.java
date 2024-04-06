package net.sourceforge.peers.gui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DataTableCellRender implements TableCellRenderer {

    public final static DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component render = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color foreground, backGround;
        foreground = Color.WHITE;
        backGround = Color.WHITE;
        if (isSelected) {
            backGround = Color.GREEN;
        }
        render.setForeground(foreground);
        render.setBackground(backGround);
        return render;
    }
}
