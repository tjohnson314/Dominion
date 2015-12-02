/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author timothyjohnson
 */
public class CardPanel extends JPanel
{
    //private final int ID;
    //private final int playerNum;
    private final ArrayList<Card> cardList;
    
    public static final int supplyID = 0;
    public static final int handID = 1;
    public static final int handWidth = 5;
    
    //Uses a GridLayout to display buttons for cards in supply, and a FlowLayout for cards in hand.
    public CardPanel(final int ID, final int playerNum)
    {
        //this.ID = ID;
        //this.playerNum = playerNum;
        if(ID == supplyID)
        {
            cardList = new ArrayList<Card>(Main.supply);
            this.setLayout(new GridLayout(cardList.size(), 1)); //For now we are only using default cards
        }
        else if(ID == handID)
        {
            cardList = Main.players.get(playerNum).Hand(); 
            this.setLayout(new GridLayout((int) Math.ceil(cardList.size()/(float) handWidth), handWidth));
        }
        else
        {
            cardList = new ArrayList<Card>();
            System.out.println("Error: Invalid card panel.");
        }
        
        final JButton[] buttons = new JButton[cardList.size()];
        
        for(int i = 0; i < cardList.size(); i++)
        {
            String buttonName = cardList.get(i).Name();
            if(ID == supplyID)
                buttonName += " (" + cardList.get(i).Supply() + ")";
            
            buttons[i] = new JButton(buttonName);
            this.add(buttons[i]);
            
            buttons[i].addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Object source = e.getSource();
                    for(int i = 0; i < buttons.length; i++)
                    {
                        if(source == buttons[i])
                        {
                            final int index = i;
                            Thread thread = (new Thread()
                            {
                                public void run()
                                {
                                    if(ID == supplyID && playerNum == Main.currentPlayer && !Player.Choosing() 
                                            && !Main.players.get(playerNum).Gaining())
                                    {
                                        Main.players.get(playerNum).BuyCard(index);
                                    }
                                    else if(ID == supplyID && Main.players.get(playerNum).Gaining())
                                    {
                                        Main.players.get(playerNum).SetDecision(index);
                                    }
                                    else if(ID == handID && playerNum == Main.currentPlayer && !Player.Choosing()
                                            && !Main.players.get(playerNum).Gaining())
                                    {
                                        System.out.println("Playing card.");
                                        Main.players.get(playerNum).PlayCard(index);
                                    }
                                }
                            });
                            
                            thread.start();
                            
                            //System.out.println("Redraw in CardPanel.");
                            //Main.Redraw();
                        }
                    }
                }
            });
        }
        
        if(ID == handID && cardList.size()%5 > 0)
        {
            for(int i = 0; i < (5 - cardList.size()%5); i++)
            {
                JTextArea dummy = new JTextArea();
                dummy.setEditable(false);
                this.add(dummy);
            }
        }
    }
}
