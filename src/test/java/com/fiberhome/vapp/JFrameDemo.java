package com.fiberhome.vapp;

import javax.swing.*;

/**
 * @author vv
 * @since 2017/1/21.
 */
public class JFrameDemo {

    public static void main(String[] args) {
        JFrame frame = new JFrame("demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.setContentPane(panel);
        frame.setSize(1024, 512);
        frame.setVisible(true);
    }
}
