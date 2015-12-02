/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author timothyjohnson
 */
public class GameWindow extends JFrame
{
    public static final int Width = 800;
    public static final int Height = 600;
    private int playerNum;
    
    private static GridBagConstraints supplyConstraints;
    private static GridBagConstraints logConstraints;
    private static GridBagConstraints statusConstraints;
    private static GridBagConstraints commandConstraints;
    private static GridBagConstraints handConstraints;
    private static GridBagConstraints choiceConstraints;
    
    
    public GameWindow(String title, int playerNum)
    {
        super(title);
        
        this.playerNum = playerNum;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(Width, Height));
        this.setResizable(false);
        
        supplyConstraints = new GridBagConstraints();
        supplyConstraints.fill = GridBagConstraints.HORIZONTAL;
        supplyConstraints.insets = new Insets(0,0,0,30);
        supplyConstraints.anchor = GridBagConstraints.NORTHWEST;
        supplyConstraints.gridx = 0;
        supplyConstraints.gridy = 0;
        supplyConstraints.gridheight = 4;
        
        logConstraints = new GridBagConstraints();
        logConstraints.fill = GridBagConstraints.HORIZONTAL;
        logConstraints.insets = new Insets(0,0,30,0);
        logConstraints.anchor = GridBagConstraints.NORTHWEST;
        logConstraints.gridheight = 1;
        logConstraints.gridwidth = 2;
        logConstraints.gridx = 1;
        logConstraints.gridy = 0;
        
        
        statusConstraints = new GridBagConstraints();
        statusConstraints.fill = GridBagConstraints.HORIZONTAL;
        statusConstraints.insets = new Insets(0,0,0,30);
        statusConstraints.anchor = GridBagConstraints.NORTHWEST;
        statusConstraints.gridheight = 1;
        statusConstraints.gridwidth = 1;
        statusConstraints.gridx = 1;
        statusConstraints.gridy = 1;
        
        commandConstraints = new GridBagConstraints();
        commandConstraints.fill = GridBagConstraints.HORIZONTAL;
        commandConstraints.anchor = GridBagConstraints.NORTHWEST;
        commandConstraints.gridheight = 1;
        commandConstraints.gridwidth = 1;
        commandConstraints.gridx = 2;
        commandConstraints.gridy = 1;
        
        handConstraints = new GridBagConstraints();
        handConstraints.fill = GridBagConstraints.HORIZONTAL;
        handConstraints.insets = new Insets(20,0,0,0);
        handConstraints.anchor = GridBagConstraints.NORTHWEST;
        handConstraints.weightx = 1;
        handConstraints.gridheight = 1;
        handConstraints.gridwidth = 2;
        handConstraints.gridx = 1;
        handConstraints.gridy = 2;
        
        choiceConstraints = new GridBagConstraints();
        choiceConstraints.fill = GridBagConstraints.HORIZONTAL;
        choiceConstraints.insets = new Insets(30,0,0,0);
        choiceConstraints.anchor = GridBagConstraints.NORTHWEST;
        choiceConstraints.weightx = 1;
        choiceConstraints.gridheight = 1;
        choiceConstraints.gridwidth = 2;
        choiceConstraints.gridx = 1;
        choiceConstraints.gridy = 3;
    }
        
    public static void addComponents(int numPlayer)
    {
        //System.out.println("Creating supply.");
        CardPanel supply = new CardPanel(CardPanel.supplyID, numPlayer);

        //System.out.println("Creating game log.");
        JTextPane gameLogText = new JTextPane();
        gameLogText.setEditable(false);
        gameLogText.setText(Main.gameLog);
        JScrollPane log = new JScrollPane(gameLogText);
        log.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));

        //System.out.println("Creating status panel.");
        JTextArea statusPanel = new JTextArea();
        statusPanel.setEditable(false);
        statusPanel.setText("Player " + (Main.currentPlayer + 1) + ", " + Player.Phase() + " Phase \n" + "Actions: "
                + Main.players.get(Main.currentPlayer).Actions() 
                + "\nBuys: " + Main.players.get(Main.currentPlayer).Buys() 
                + "\nMoney: " + Main.players.get(Main.currentPlayer).Money()
                + "\nDeck size: " + Main.players.get(Main.currentPlayer).DeckSize());

        //System.out.println("Creating command panel.");
        CommandPanel commands = new CommandPanel(numPlayer);

        //System.out.println("Creating hand.");
        CardPanel hand = new CardPanel(CardPanel.handID, numPlayer);

        //System.out.println("Creating choice panel: " + Main.players.get(i).ToChoose());
        ChoicePanel choice = new ChoicePanel(numPlayer, Main.players.get(numPlayer).Prompt());

        //System.out.println("Creating decision panel.");
        DecidePanel decide = new DecidePanel(numPlayer, Main.players.get(numPlayer).Prompt());

        Container pane = Main.windows.get(numPlayer).getContentPane();
        pane.setLayout(new GridBagLayout());
        pane.add(supply, supplyConstraints);
        pane.add(log, logConstraints);
        pane.add(statusPanel, statusConstraints);
        pane.add(commands, commandConstraints);
        pane.add(hand, handConstraints);

        if(Main.players.get(numPlayer).ToChoose() > 0)
            pane.add(choice, choiceConstraints);
        else if(!Main.players.get(numPlayer).Prompt().equals(""))
        {
            //System.out.println("Including decision panel.");
            pane.add(decide, choiceConstraints);
        }
    }
    
    public int Player()
    {
        return playerNum;
    }
}
