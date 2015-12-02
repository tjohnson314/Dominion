/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author timothyjohnson
 */
public class ChoicePanel extends JPanel
{
    public static final int choiceWidth = 5;
    
    private final ArrayList<Card> cardList;
    
    public ChoicePanel(final int playerNum, String message)
    {
        cardList = Main.players.get(playerNum).Choice();
        this.setLayout(new GridLayout(2, 1));
        
        JTextArea text = new JTextArea(message);
        this.add(text);
        
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new GridLayout((int) Math.ceil(cardList.size()/(float) choiceWidth), choiceWidth));
        
        final JCheckBox[] checks = new JCheckBox[cardList.size()];

        for(int i = 0; i < cardList.size(); i++)
        {
            String boxName = cardList.get(i).Name();
            
            checks[i] = new JCheckBox(boxName, false);
            boxPanel.add(checks[i]);
        }
        
        final JButton done = new JButton("Done");
        boxPanel.add(done);
        done.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent e)
           {
               Object source = e.getSource();
               if(source == done)
               {
                   ArrayList<Integer> chosen = new ArrayList<Integer>();
                   for(int i = 0; i < checks.length; i++)
                   {
                       if(checks[i].isSelected())
                           chosen.add(i);
                   }
                   
                   if(chosen.size() == Main.players.get(playerNum).ToChoose()
                           || (Main.players.get(playerNum).ChooseFewer() 
                           && chosen.size() < Main.players.get(playerNum).ToChoose()))
                   {
                       Main.players.get(playerNum).setChosen(chosen);
                   }
                   
                   //System.out.println("Redraw in ChoicePanel.");
                   //Main.Redraw();
               }
           }
        });
        
        this.add(boxPanel);
        
        /*if(cardList.size()%5 > 0)
        {
            for(int i = 0; i < (5 - cardList.size()%5); i++)
            {
                JTextArea dummy = new JTextArea();
                dummy.setEditable(false);
                this.add(dummy);
            }
        }*/
    }
}
