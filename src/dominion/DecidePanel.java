/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author timothyjohnson
 */
public class DecidePanel extends JPanel
{
    public DecidePanel(final int playerNum, String message)
    {
        this.setLayout(new GridLayout(2, 1));
        
        JTextArea text = new JTextArea(message);
        this.add(text);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        final JButton yes = new JButton("Yes");
        buttonPanel.add(yes);
        final JButton no = new JButton("No");
        buttonPanel.add(no);
        
        yes.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent e)
           {
               Object source = e.getSource();
               if(source == yes)
               {
                   Main.players.get(playerNum).SetDecision(1);
                   
                   //System.out.println("Redraw 1 in DecidePanel.");
                   //Main.Redraw();
               }
           }
        });
        
        no.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Object source = e.getSource();
                if(source == no)
                {
                    Main.players.get(playerNum).SetDecision(0);
                    
                    //System.out.println("Redraw 2 in DecidePanel.");
                    //Main.Redraw();
                }
            }
        });
        
        if(!Main.players.get(playerNum).Gaining())
            this.add(buttonPanel);
    }
}
