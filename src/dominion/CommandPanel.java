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
public class CommandPanel extends JPanel
{
    public CommandPanel(final int playerNum)
    {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        final JButton playTreasures = new JButton("All treasures");
        this.add(playTreasures);
        playTreasures.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(e.getSource() == playTreasures && playerNum == Main.currentPlayer && !Player.Choosing())
                {
                    for(int i = 0; i < Main.players.get(Main.currentPlayer).Hand().size();)
                    {
                        Card card = Main.players.get(Main.currentPlayer).Hand().get(i);
                        if(card.Type() == Card.TreasureType)
                            Main.players.get(Main.currentPlayer).PlayCard(i);
                        else
                            i++;
                    }
                    
                    //System.out.println("Redraw 1 in CommandPanel.");
                    //Main.Redraw();
                }
            }
        });
        
        final JButton endTurn = new JButton("End turn");
        this.add(endTurn);
        endTurn.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent e) 
           {
               if(e.getSource() == endTurn && playerNum == Main.currentPlayer && !Player.Choosing())
               {
                   Main.gameLog += (Main.players.get(Main.currentPlayer).Name() + " ends his turn.\n");
                   Main.players.get(Main.currentPlayer).NewTurn();
                   
                   //System.out.println("Redraw 2 in CommandPanel.");
                   //Main.Redraw();
               }
           }
        });
        
    }
}
