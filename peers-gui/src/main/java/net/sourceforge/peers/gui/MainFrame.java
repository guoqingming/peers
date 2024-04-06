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

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import cn.hutool.core.util.ReUtil;
import net.sourceforge.peers.FileLogger;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.ext.cache.LocalCache;
import net.sourceforge.peers.ext.domain.DemoData;
import net.sourceforge.peers.ext.excel.parse.ExcelUtils;
import net.sourceforge.peers.javaxsound.JavaxSoundManager;
import net.sourceforge.peers.media.AbstractSoundManager;
import net.sourceforge.peers.sip.Utils;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;
import org.apache.commons.collections4.CollectionUtils;

public class MainFrame implements WindowListener, ActionListener {

    private static final String column[] = {"Phone", "ContractNo", "Name", "IdNo"};

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(args);
            }
        });
    }

    private static void createAndShowGUI(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        new MainFrame(args);
    }

    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel dialerPanel;
    private JTextField uri;
    private JButton actionButton;

    private JTable jt;
    private JButton nextButton;
    private JLabel statusLabel;

    private EventManager eventManager;
    private Registration registration;
    private Logger logger;

    public JTextField getUri() {
        return uri;
    }

    public MainFrame(final String[] args) {
        String peersHome = Utils.DEFAULT_PEERS_HOME;
        if (args.length > 0) {
            peersHome = args[0];
        }
        logger = new FileLogger(peersHome);
        String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (Exception e) {
            logger.error("cannot change look and feel", e);
        }
        final AbstractSoundManager soundManager = new JavaxSoundManager(
                false, //TODO config.isMediaDebug(),
                logger, peersHome);
        String title = "";
        if (!Utils.DEFAULT_PEERS_HOME.equals(peersHome)) {
            title = peersHome;
        }
        title += "Peers";
        mainFrame = new JFrame(title);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.addWindowListener(this);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        dialerPanel = new JPanel();
        actionButton = new JButton("Call");
        actionButton.addActionListener(this);
        uri = new JTextField("sip:", 15);
//        uri.addActionListener(this);
        uri.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("---------insertUpdate---------------");
//                actionButton.doClick();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }

        });
        actionButton = new JButton("Call");
        actionButton.addActionListener(this);
        dialerPanel.add(uri);
        dialerPanel.add(actionButton);
        nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Next button clicked");
                if (!LocalCache.getInstance().getCache().iterator().hasNext()) {
                    JOptionPane.showMessageDialog(null, "所有电话已拨打完毕！");
                    return;
                }
                String phone = ReUtil.get("(?<=sip:)(\\d*?)(?=@)", uri.getText(), 0);

                LocalCache.getInstance().getCache().remove(phone);
                DemoData next = LocalCache.getInstance().getCache().iterator().next();
                uri.setText("sip:" + next.getPhone() + "@tinysnail.site");
                actionButton.doClick();
                setTableData();
            }
        });
        dialerPanel.add(nextButton);
        dialerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton importButton = new JButton("Import");
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            //filtering files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xls", "xls", "xlsx");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setDialogTitle("选择文件");
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            int res = fileChooser.showSaveDialog(null);
            //if the user clicks on save in Jfilechooser
            if (res == JFileChooser.APPROVE_OPTION) {
                File selFile = fileChooser.getSelectedFile();
                String path = selFile.getAbsolutePath();
                List<DemoData> rows = ExcelUtils.getRows(path, DemoData.class);
                if (CollectionUtils.isEmpty(rows)) {
                    JOptionPane.showMessageDialog(null, "数据文件为空");
                    return;
                }
                for (DemoData row : rows) {
                    LocalCache.getInstance().getCache().put(row.getPhone(), row);
                }
                JOptionPane.showMessageDialog(null, "数据导入成功");
                Iterator<DemoData> iterator = LocalCache.getInstance().getCache().iterator();
                if (iterator.hasNext()) {
                    DemoData next = iterator.next();
                    uri.setText("sip:" + next.getPhone() + "@tinysnail.site");
                }
                setTableData();
            }

        });
        dialerPanel.add(importButton, BorderLayout.NORTH);
        int size = LocalCache.getInstance().getCache().size();
        String arr[][] = new String[size][4];
        jt = new JTable(arr, column);
        jt.setBounds(30, 40, 200, 300);
        JScrollPane sp = new JScrollPane(jt);
        dialerPanel.add(sp);
        statusLabel = new JLabel(title);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        Border border = BorderFactory.createEmptyBorder(0, 2, 2, 2);
        statusLabel.setBorder(border);

        mainPanel.add(dialerPanel);
        mainPanel.add(statusLabel);

        Container contentPane = mainFrame.getContentPane();
        contentPane.add(mainPanel);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        JMenuItem menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic('x');
        menuItem.setActionCommand(EventManager.ACTION_EXIT);

        registration = new Registration(statusLabel, logger);

        Thread thread = new Thread(new Runnable() {
            public void run() {
                String peersHome = Utils.DEFAULT_PEERS_HOME;
                if (args.length > 0) {
                    peersHome = args[0];
                }
                eventManager = new EventManager(MainFrame.this,
                        peersHome, logger, soundManager);
                eventManager.register();
            }
        }, "gui-event-manager");
        thread.start();

        try {
            while (eventManager == null) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            return;
        }
//        batchCallButton.addActionListener(eventManager);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu("Edit");
        menu.setMnemonic('E');
        menuItem = new JMenuItem("Account");
        menuItem.setMnemonic('A');
        menuItem.setActionCommand(EventManager.ACTION_ACCOUNT);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuItem = new JMenuItem("Export Call Result");
        menuItem.setMnemonic('E');
        menuItem.setActionCommand(EventManager.ACTION_EXPORT_CALL_RESULT);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuItem = new JMenuItem("Preferences");
        menuItem.setMnemonic('P');
        menuItem.setActionCommand(EventManager.ACTION_PREFERENCES);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu("Help");
        menu.setMnemonic('H');
        menuItem = new JMenuItem("User manual");
        menuItem.setMnemonic('D');
        menuItem.setActionCommand(EventManager.ACTION_DOCUMENTATION);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuItem = new JMenuItem("About");
        menuItem.setMnemonic('A');
        menuItem.setActionCommand(EventManager.ACTION_ABOUT);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuBar.add(menu);

        mainFrame.setJMenuBar(menuBar);

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    // window events

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        eventManager.windowClosed();
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    // action event

    @Override
    public void actionPerformed(ActionEvent e) {
        eventManager.callClicked(uri.getText());
    }

    // misc.
    public void setLabelText(String text) {
        statusLabel.setText(text);
        mainFrame.pack();
    }

    public void registerFailed(SipResponse sipResponse) {
        registration.registerFailed();
    }

    public void registerSuccessful(SipResponse sipResponse) {
        registration.registerSuccessful();
    }

    public void registering(SipRequest sipRequest) {
        registration.registerSent();
    }

    public void socketExceptionOnStartup() {
        JOptionPane.showMessageDialog(mainFrame, "peers SIP port " +
                "unavailable, exiting");
        System.exit(1);
    }

    public void setTableData() {
        TableModel tableModel = new DefaultTableModel(LocalCache.getInstance().getDataArray(), column);
        jt.setModel(tableModel);
        if (jt.getModel().getRowCount() == 0) {
            return;
        }
        jt.setSelectionForeground(Color.PINK);
        jt.addRowSelectionInterval(0, 0);
    }

}
