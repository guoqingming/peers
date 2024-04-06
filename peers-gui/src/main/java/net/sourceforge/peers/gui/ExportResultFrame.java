/*
    This file is part of Peers, a java SIP softphone.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Copyright 2010-2013 Yohann Martineau
*/

package net.sourceforge.peers.gui;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.ext.cache.ContactCache;
import net.sourceforge.peers.ext.cache.LocalCache;
import net.sourceforge.peers.ext.domain.DemoData;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ExportResultFrame extends JFrame implements ActionListener, HyperlinkListener {

    public static final String LICENSE_FILE = File.separator + "gpl.txt";

    private static final long serialVersionUID = 1L;

    private Logger logger;

    public ExportResultFrame(Logger logger) {
        this.logger = logger;
        JFrame jFrame = new JFrame();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        jFrame.add(jFileChooser);
        int res = jFileChooser.showSaveDialog(null);
        //if the user clicks on save in Jfilechooser
        if (res == JFileChooser.APPROVE_OPTION) {

            File selFile = jFileChooser.getSelectedFile();
            String path = selFile.getAbsolutePath();
            List<DemoData> data = ContactCache.getInstance().getData();
            if (CollectionUtil.isEmpty(data)) {
                JOptionPane.showMessageDialog(null, "没有数据可导出");
                return;
            }
            File file = new File(path + File.separator + "result.xlsx");
            if (!file.exists()) {
                try {
                    boolean createFileResult = file.createNewFile();
                    System.out.println("创建文件结果："+  createFileResult);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            EasyExcel.write(file, DemoData.class).sheet("呼叫结果").doWrite(data);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        if (EventType.ACTIVATED.equals(hyperlinkEvent.getEventType())) {
            try {
                URI uri = hyperlinkEvent.getURL().toURI();
                Desktop.getDesktop().browse(uri);
            } catch (URISyntaxException e) {
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
