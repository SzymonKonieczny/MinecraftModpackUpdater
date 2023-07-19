package main.java;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI {
    MainGUI()
    {
      CheckUpdate.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("Check for  Updates"))
            {

            }

          }
      });


        Update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("Update"))
                {

                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Testing");
        frame.setContentPane(new MainGUI().MainPanel);
      //  frame.pack();
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JLabel Label;
    private JPanel MainPanel;
    private JButton CheckUpdate;
    private JButton Update;


}
