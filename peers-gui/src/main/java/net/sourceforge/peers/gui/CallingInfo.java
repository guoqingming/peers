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

import net.sourceforge.peers.ext.cache.LocalCache;
import net.sourceforge.peers.ext.domain.DemoData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class CallingInfo extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;


    private CallFrame callFrame;

    public CallingInfo(CallFrame callFrame) {
        this.callFrame = callFrame;
        initComponents();
    }

    private void initComponents() {
        if (callFrame.getSipRequest() == null) {
            return;
        }
        if (callFrame.getSipRequest().getPhone() == null || callFrame.getSipRequest().getPhone().isEmpty()) {
            return;
        }
        DemoData demoData = LocalCache.getInstance().getCache().get(callFrame.getSipRequest().getPhone());
        if (demoData == null) {
            return;
        }
        String arr[][] = new String[1][4];
        arr[0] = new String[]{demoData.getPhone(), demoData.getContractNo(), demoData.getName(), demoData.getIdNo()};
        String column[]={"Phone","ContractNo","Name","IdNo"};
        JTable jt=new JTable(arr,column);
        jt.setBounds(30,40,200,100);
        jt.setBackground(Color.PINK);
        JScrollPane sp=new JScrollPane(jt);
        add(sp);
        JRadioButton button1 = new JRadioButton("正常接听");
        JRadioButton button2 = new JRadioButton("无人接听");
        JRadioButton button3 = new JRadioButton("关机");
        JRadioButton button4 = new JRadioButton("停机");
        JRadioButton button5 = new JRadioButton("无法接通");
        ButtonGroup resultButtonGroup = new ButtonGroup();
        resultButtonGroup.add(button1);
        resultButtonGroup.add(button2);
        resultButtonGroup.add(button3);
        resultButtonGroup.add(button4);
        resultButtonGroup.add(button5);
        add(button1);
        add(button2);
        add(button3);
        add(button4);
        add(button5);

//        setSize(1000,600);
        setVisible(true);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
//        Dimension dimension = new Dimension(600, 115);
//        setMinimumSize(dimension);
//        setMaximumSize(dimension);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        callFrame.keypadEvent(command.charAt(0));
    }

}
