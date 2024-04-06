package net.sourceforge.peers.gui;

import javax.swing.*;
import java.awt.*;

public class Test {
    public static void main(String[] args) {
        {
            JFrame frame=new JFrame("Java第三个GUI程序"); //创建Frame窗口
            frame.setSize(400,200);
            frame.setLayout(new BorderLayout()); //为Frame窗口设置布局为BorderLayout
            JButton button1=new JButton ("上");
            JButton button2=new JButton("左");
            JButton button3=new JButton("中");
            JButton button4=new JButton("右");
            JButton button5=new JButton("下");
//            frame.add(button1,BorderLayout.NORTH);
//            frame.add(button2,BorderLayout.WEST);
//            frame.add(button3,BorderLayout.CENTER);
//            frame.add(button4,BorderLayout.EAST);
//            frame.add(button5,BorderLayout.SOUTH);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.setApproveButtonText("11111111");
            frame.add(fileChooser);
            frame.setBounds(300,200,600,300);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
