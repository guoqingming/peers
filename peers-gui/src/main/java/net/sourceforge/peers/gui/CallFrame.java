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
    
    Copyright 2010 Yohann Martineau 
*/

package net.sourceforge.peers.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.border.Border;

import cn.hutool.core.util.StrUtil;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.ext.cache.ContactCache;
import net.sourceforge.peers.ext.cache.LocalCache;
import net.sourceforge.peers.ext.domain.DemoData;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;

public class CallFrame implements ActionListener, WindowListener {

    public static final String HANGUP_ACTION_COMMAND    = "hangup";
    public static final String PICKUP_ACTION_COMMAND    = "pickup";
    public static final String BUSY_HERE_ACTION_COMMAND = "busyhere";
    public static final String CLOSE_ACTION_COMMAND     = "close";

    private CallFrameState state;

    public final CallFrameState INIT;
    public final CallFrameState UAC;
    public final CallFrameState UAS;
    public final CallFrameState RINGING;
    public final CallFrameState SUCCESS;
    public final CallFrameState FAILED;
    public final CallFrameState REMOTE_HANGUP;
    public final CallFrameState TERMINATED;

    private JFrame frame;
    private JPanel callPanel;
    private JPanel callPanelContainer;
    private CallFrameListener callFrameListener;
    private SipRequest sipRequest;

    private ButtonGroup resultButtonGroup;

    CallFrame(String remoteParty, String id,
            CallFrameListener callFrameListener, Logger logger,SipRequest sipRequest) {
        INIT = new CallFrameStateInit(id, this, logger);
        UAC = new CallFrameStateUac(id, this, logger);
        UAS = new CallFrameStateUas(id, this, logger);
        RINGING = new CallFrameStateRinging(id, this, logger);
        SUCCESS = new CallFrameStateSuccess(id, this, logger);
        FAILED = new CallFrameStateFailed(id, this, logger);
        REMOTE_HANGUP = new CallFrameStateRemoteHangup(id, this, logger);
        TERMINATED = new CallFrameStateTerminated(id, this, logger);
        state = INIT;
        this.callFrameListener = callFrameListener;
        frame = new JFrame(remoteParty);
        frame.setSize(600,400);
        Container contentPane = frame.getContentPane();
        contentPane.setSize(800,800);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        JLabel remotePartyLabel = new JLabel(remoteParty);
        Border remotePartyBorder = BorderFactory.createEmptyBorder(5, 5, 0, 5);
        remotePartyLabel.setBorder(remotePartyBorder);
        remotePartyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(remotePartyLabel);
        this.sipRequest = sipRequest;
        //todo 临时删除拨号
//        Keypad keypad = new Keypad(this);
//        contentPane.add(keypad);
//        DemoData demoData = LocalCache.getInstance().getCache().get(sipRequest.getPhone());
//        if (demoData == null) {
//            return;
//        }
//        String arr[][] = new String[1][4];
//        arr[0] = new String[]{demoData.getPhone(), demoData.getContractNo(), demoData.getName(), demoData.getIdNo()};
//        String column[]={"Phone","ContractNo","Name","IdNo"};
//        JTable jt=new JTable(arr,column);
//        jt.setBounds(30,40,200,100);
//        jt.setBackground(Color.PINK);
//        JScrollPane sp=new JScrollPane(jt);
//        contentPane.add(sp);
//        sp.setSize(300,200);
//        contentPane.add(jt);
        JLabel resultLabel = new JLabel("Call Result: ");
        JRadioButton button1 = new JRadioButton("正常接听");
        JRadioButton button2 = new JRadioButton("无人接听");
        JRadioButton button3 = new JRadioButton("关机");
        JRadioButton button4 = new JRadioButton("停机");
        JRadioButton button5 = new JRadioButton("无法接通");
        resultButtonGroup = new ButtonGroup();
        resultButtonGroup.add(button1);
        resultButtonGroup.add(button2);
        resultButtonGroup.add(button3);
        resultButtonGroup.add(button4);
        resultButtonGroup.add(button5);
//        contentPane.add(button1);
//        contentPane.add(button2);
//        contentPane.add(button3);
//        contentPane.add(button4);
//        contentPane.add(button5);
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEADING,50,50));
        resultPanel.add(resultLabel);
        resultPanel.add(button1);
        resultPanel.add(button2);
        resultPanel.add(button3);
        resultPanel.add(button4);
        resultPanel.add(button5);
        contentPane.add(resultPanel);
        callPanelContainer = new JPanel(new FlowLayout());
        contentPane.add(callPanelContainer);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(this);
    }

    public void callClicked() {
        state.callClicked();
    }

    public void incomingCall() {
        state.incomingCall();
    }

    public void remoteHangup() {
        state.remoteHangup();
    }

    public void error(SipResponse sipResponse) {
        state.error(sipResponse);
    }

    public void calleePickup() {
        state.calleePickup();
    }

    public void ringing() {
        state.ringing();
    }

    void hangup() {
        if (callFrameListener != null) {
            callFrameListener.hangupClicked(sipRequest);
        }
    }

    void pickup() {
        if (callFrameListener != null && sipRequest != null) {
            callFrameListener.pickupClicked(sipRequest);
        }
    }

    void busyHere() {
        if (callFrameListener != null && sipRequest != null) {
            frame.dispose();
            callFrameListener.busyHereClicked(sipRequest);
            sipRequest = null;
        }
    }

    void close() {
        frame.dispose();
    }
    
    public void setState(CallFrameState state) {
        this.state.log(state);
        this.state = state;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setCallPanel(JPanel callPanel) {
        if (this.callPanel != null) {
            callPanelContainer.remove(this.callPanel);
            frame.pack();
        }
        callPanelContainer.add(callPanel);
        frame.pack();
        this.callPanel = callPanel;
    }

    public void addPageEndLabel(String text) {
        Container container = frame.getContentPane();
        JLabel label = new JLabel(text);
        Border labelBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
        label.setBorder(labelBorder);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(label);
        frame.pack();
    }

    public void setSipRequest(SipRequest sipRequest) {
        this.sipRequest = sipRequest;
    }

    // action listener methods

    public SipRequest getSipRequest() {
        return sipRequest;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        Runnable runnable = null;
        if (HANGUP_ACTION_COMMAND.equals(actionCommand)) {
            runnable = new Runnable() {
                @Override
                public void run() {
//                    String result = getSelectedButtonText(resultButtonGroup);
//                    if (StrUtil.isBlank(result)) {
//                        JOptionPane.showMessageDialog(null,"请选择拨打结果！");
//                        return;
//                    }
                    state.hangupClicked();
                }
            };
        } else if (CLOSE_ACTION_COMMAND.equals(actionCommand)) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    String result = getSelectedButtonText(resultButtonGroup);
                    if (StrUtil.isBlank(result)) {
                        JOptionPane.showMessageDialog(null,"请选择拨打结果！");
                        return;
                    }
                    String phone = sipRequest.getPhone();
                    DemoData demoData = LocalCache.getInstance().getCache().get(phone);
                    if (demoData != null) {
                        demoData.setCallResult(result);
                        ContactCache.getInstance().getCache().put(phone, demoData);
                    }
                    state.closeClicked();
                }
            };
        } else if (PICKUP_ACTION_COMMAND.equals(actionCommand)) {
            runnable = new Runnable() {
                public void run() {
                    state.pickupClicked();
                }
            };
        } else if (BUSY_HERE_ACTION_COMMAND.equals(actionCommand)) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    state.busyHereClicked();
                }
            };
        }
        if (runnable != null) {
            SwingUtilities.invokeLater(runnable);
        }
    }

    // window listener methods

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        state.hangupClicked();
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

    public void keypadEvent(char c) {
        callFrameListener.dtmf(c);
    }


    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
    }

}
