/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

import java.util.*;
import javax.swing.*;

/**
 *
 * @author timothyjohnson
 */
public class Player
{
    //The three phases are as follows:
    // - Action phase: play action cards
    // - Treasure phase: play treasures
    // - Buy phase: buy new cards
    private static final int actionPhase = 0;
    private static final int treasurePhase = 1;
    private static final int buyPhase = 2;
    
    //The number of cards, actions, buys, and coin that players have at the start of their turns.
    public static int startHand;
    public static int startActions;
    public static int startBuys;
    public static int startMoney;
    
    //The list of all cards trashed from the game.
    public static ArrayList<Card> Trash = new ArrayList<Card>();
    
    private static int turn = 1; //The current turn.
    private static int phase; //The phase of their turn the current player is in.
    private static int discardRunning; //The number of players currently discarding cards.
    
    //The number of actions, buys, and coin each player has.
    private int actions, buys, money;
    
    private String name; //Name of a player.
    private int playerNum; //Index of a player in the list.
    private int victoryTokens; //Victory point tokens a player has earned.
    
    //The list of cards in a player's hand, deck, discard, and the cards they have in play.
    private ArrayList<Card> Hand;
    private ArrayList<Card> Deck; 
    private ArrayList<Card> Discard;
    private ArrayList<Card> InPlay;
    
    //A list of cards for a player to choose from (for discarding, trashing, etc.).
    private ArrayList<Card> Choice;
    
    private ArrayList<Integer> chosen; //The indices of which cards they have chosen from that list.
    private boolean choiceMade; //True if a player's choice has been made.
    private int toChoose; //The number of cards to choose.
    private boolean chooseFewer; //Whether that player can choose fewer than the maximum number of cards.
    private String prompt; //The prompt to explain why a player must choose some number of cards.
    private int decide; //Used for a yes/no decision, or for the index of single card chosen from the supply.
    private boolean gaining; //True if a player is gaining a card for free, rather than paying for it.
    
    public Player(ArrayList<Card> startDeck, String name, int playerNum)
    {
        this.name = name;
        this.playerNum = playerNum;
        victoryTokens = 0;
        
        Deck = new ArrayList<Card>(startDeck);
        Hand = new ArrayList<Card>();
        Discard = new ArrayList<Card>();
        InPlay = new ArrayList<Card>();
        
        Choice = new ArrayList<Card>();
        chosen = new ArrayList<Integer>();
        discardRunning = -1;
        choiceMade = false;
        toChoose = -1;
        chooseFewer = false;
        prompt = "";
        decide = -1;
        gaining = false;
        
        //Every player always has a hand, with the appropriate number of actions, buys, and coin.
        //These are reset as the last step of the clean-up after someone's turn ends.
        actions = startActions;
        buys = startBuys;
        money = startMoney;
        this.Draw(startHand);
    }
    
    /**
     * Returns the turn phase for the current player.
     * @return Returns a string to be printed.
     */
    public static String Phase()
    {
        if(phase == actionPhase)
            return "Action";
        else if(phase == treasurePhase)
            return "Treasure";
        else if(phase == buyPhase)
            return "Buy";
        else
            return "Error";
    }
    
    public int DeckSize()
    {
        return Deck.size();
    }
    
    /**
     * Returns the list of cards in a player's hand.
     */
    public ArrayList<Card> Hand()
    {
        return Hand;
    }
    
    /**
     * Returns the list of cards a player must choose from.
     */
    public ArrayList<Card> Choice()
    {
        return Choice;
    }
    
    /**
     * Returns the instructions prompting a player to make a choice. 
     */
    public String Prompt()
    {
        return prompt;
    }
    
    /**
     * Returns the maximum number of cards a player can choose from their list.
     */
    public int ToChoose()
    {
        return toChoose;
    }
    
    /**
     * Returns true if a player can choose fewer cards than the maximum. 
     */
    public boolean ChooseFewer()
    {
        return chooseFewer;
    }
    
    /**
     * Takes the list of cards chosen from the GUI, and indicates that the choice has been made. 
     */
    public void setChosen(ArrayList<Integer> chosen)
    {
        this.chosen = chosen;
        choiceMade = true;
    }
    
    /**
     * Returns a decision for a question with a set list of options.
     */
    public int Decision()
    {
        return decide;
    }
    
    /**
     * Sets a decision for a question with a set list of options.
     * @param decision 
     */
    public void SetDecision(int decision)
    {
        decide = decision;
    }
    
    /**
     * True if a player is choosing a card to gain. 
     */
    public boolean Gaining()
    {
        return gaining;
    }
    
    /**
     * Returns the number of actions a player has left to use. 
     */
    public int Actions()
    {
        return actions;
    }
    
    /**
     * Returns the number of buys a player has left to use. 
     */
    public int Buys()
    {
        return buys;
    }
    
    /**
     * Returns the amount of money a player has to spend. 
     */
    public int Money()
    {
        return money;
    }
    
    /**
     * Returns a player's name. 
     */
    public String Name()
    {
        return name;
    }
    
    /**
     * Takes the discard pile and shuffles a player's deck.
     */
    public void Shuffle()
    {
        while(Discard.size() > 0)
        {
            Deck.add(Discard.remove(0));
        }
        
        //For testing purposes, the seed has been set to a constant value, so that any
        //errors found are repeatable.
        long seed = 1;
        Collections.shuffle(Deck, new Random(seed));
    }
    
    /**
     * Draws cards from a player's deck into their hand.
     * @param numCards The number of cards to draw.
     */
    public void Draw(int numCards)
    {
        //If no more cards are available, a player will draw not draw the full number of cards.
        for(int i = 0; i < numCards; i++)
        {
            if(Deck.size() == 0)
                Shuffle();
            
            if(Deck.size() > 0)
                Hand.add(Deck.remove(0));
        }
        Collections.sort(Hand, new CustomComparator());
    }
    
    /**
     * Discards a card from a player's hand.
     * @param handLoc The index of the card to discard in the list of that player's cards in hand.
     */
    public void DiscardCard(int handLoc)
    {
        Discard.add(Hand.remove(handLoc));
    }
    
    /**
     * Puts a card from a player's hand onto his deck.
     * @param handLoc The index of the card to put on top of the deck.
     */
    public void PutOnDeck(int handLoc)
    {
        Deck.add(0, Hand.remove(handLoc));
    }
    
    /**
     * Trashes a card from a player's hand.
     * @param handLoc The index of the card to trash in the list of the player's hand of cards.
     */
    public void TrashCard(int handLoc)
    {
        Trash.add(Hand.remove(handLoc));
    }
    
    /**
     * Finds a card in a player's hand.
     * @param card The card to search for.
     * @return Returns the index of that card in the player's hand, or -1 if it was not found.
     */
    public int FindInHand(Card card)
    {
        for(int i = 0; i < Hand.size(); i++)
        {
            if(Hand.get(i).Name().equals(card.Name()))
                return i;
        }
        return -1;
    }
    
    /**
     * Plays a card from a player's hand.
     * @param cardLoc The index of the card to play in the player's hand.
     */
    public void PlayCard(int cardLoc)
    {
        Card card = Hand.get(cardLoc);
        //To play an action card, we must have actions and be in the action phase.
        //After being played, a card is "in play." This is distinct from the discard pile.
        if(phase == actionPhase && card.Type() == Card.ActionType && actions > 0)
        {
            ActionCard tempCard = (ActionCard)card;
            Hand.remove(cardLoc);
            InPlay.add(card);
            Main.gameLog += (name + " plays a(n) " + card.Name() + "\n");
            
            //The PlayAction function takes care of redrawing everyone's game window after each
            //instruction on that card is performed, so that is unnecessary here.
            PlayAction(tempCard);
        }
        //To play a treasure card, we must be in the treasure phase. If we are in the action phase,
        //we automatically end the action phase and move to the treasure phase. I may add a prompt
        //later asking the player whether this is what they want to do. There is no limit on the
        //number of treasure cards that may be played.
        else if((phase == actionPhase || phase == treasurePhase) && card.Type() == Card.TreasureType)
        {
            phase = treasurePhase;
            Hand.remove(cardLoc);
            TreasureCard tempCard = (TreasureCard)card;
            money += tempCard.Value();
            InPlay.add(card);
            Main.gameLog += (name + " plays a(n) " + card.Name() + "\n");
            
            //After a treasure card is played, we update everyone's game window with that info.
            System.out.println("Redraw in PlayCard.");
            for(int i = 0; i < Main.numPlayers; i++)
                Main.Redraw(i);
        }
    }
    
    /**
     * Follows all instructions on a given card, from start to finish.
     * @param card The card to be played.
     */
    public void PlayAction(ActionCard card)
    {
        //Playing the card requires using an action.
        actions -= 1;
        System.out.println(card.Instructions().size());
        
        //We continue with this card until every instruction has been followed as much as possible.
        for(int i = 0; i < card.Instructions().size(); i++)
        {
            //We must acquire a permit to follow an instruction on a card, and then finish
            //it before we begin the next instruction.
            try
            {
                Main.actionSemaphore.acquire();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            
            Instruction next = card.Instructions().get(i);
            System.out.println(i + ", " + next.Command());
            
            //Instruction: +X Cards.
            if(next.Command().equals("PlusCards"))
            {
                Draw(next.Num());
                Main.actionSemaphore.release();
            }
            
            //Instruction: +X Actions.
            else if(next.Command().equals("PlusActions"))
            {
                actions += next.Num();
                Main.actionSemaphore.release();
            }
            
            //Instruction: +X Buys
            else if(next.Command().equals("PlusBuys"))
            {
                buys += next.Num();
                Main.actionSemaphore.release();
            }
            
            //Instruction: + $X
            else if(next.Command().equals("PlusMoney"))
            {
                money += next.Num();
                Main.actionSemaphore.release();
            }
            
            //Instruction: All other players draw X cards.
            else if(next.Command().equals("PlusCardsOthers"))
            {
                for(int j = 1; j < Main.numPlayers; j++)
                {
                    int nextPlayer = (Main.currentPlayer + j)%Main.numPlayers;
                    Main.players.get(nextPlayer).Draw(next.Num());
                }
                Main.actionSemaphore.release();
            }
            
            //Instruction: Gain a card costing up to $X.
            else if(next.Command().equals("GainUpTo"))
            {
                this.ChooseGain(next.Num(), true);
            }
            
            //Instruction: Gain a card costing exactly $X.
            else if(next.Command().equals("Gain"))
            {
                this.ChooseGain(next.Num(), false);
            }

            //Instruction: Trash up to X cards.
            else if(next.Command().equals("TrashUpTo"))
            {
                this.Trash(next.Num(), true);
            }
            
            //Instruction: Trash (exactly) X cards.
            else if(next.Command().equals("Trash"))
            {
                this.Trash(next.Num(), false);
            }
            
            //Instruction: You may put your deck into your discard pile.
            else if(next.Command().equals("DeckToDiscard"))
            {   
                this.DeckToDiscard();
            }
            
            else if(next.Command().equals("Discard"))
            {
                this.Discard(Main.currentPlayer, next.Num(), false);
            }
            
            else if(next.Command().equals("DiscardToDraw"))
            {
                int discarded = this.Discard(Main.currentPlayer, this.Hand().size(), true);
                this.Draw(discarded);
            }
            
            else if(next.Command().equals("GainSilverTop"))
            {
                int silverNum = Main.FindCard("Silver");
                this.GainCard(silverNum, false, true);
                Main.gameLog += this.Name() + " gains a silver on top of his deck.\n";
                Main.actionSemaphore.release();
            }
            
            //All attack instructions are lumped together here. The number specifies which attack is performed.
            else if(next.Command().equals("Attack"))
            {
                final int attackNum = next.Num();
                Main.players.get(Main.currentPlayer).Attack(attackNum);
            }
            
            else if(next.Command().equals("RepeatReq"))
            {
                
            }
            
            //After a step is completed, we update every player's game window.
            System.out.println("Redraw in PlayAction.");
            for(int j = 0; j < Main.numPlayers; j++)
                Main.Redraw(j);
        }
    }
    
    public void ChooseGain(int cost, boolean costFewer)
    {
        try
        {
            Main.dataSemaphore.acquire();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        int options = 0;
        for(int i = 0; i < Main.actionSize; i++)
        {
            Card card = Main.supply.get(i);
            if((card.Cost() == cost || (card.Cost() < cost && costFewer)) && card.Supply() > 0)
            {
                options++;
                decide = i;
            }
        }
        
        if(options > 0)
        {
            gaining = true;
            if(options > 1)
            {
                decide = -1;
                if(costFewer)
                    prompt = "Gain a card from the supply costing up to " + cost + ".";
                else
                    prompt = "Gain a card from the supply costing exactly " + cost + ".";

                Main.dataSemaphore.release();

                System.out.println("Redraw in ChooseGain.");
                Main.Redraw(this.playerNum);
            }
            
            while(true)
            {
                if(decide != -1)
                {
                    System.out.println(Main.supply.get(decide).Name());
                    Card card = Main.supply.get(decide);
                    if((card.Cost() == cost || (card.Cost() < cost && costFewer)) && card.Supply() > 0)
                        break;
                }
                
                decide = -1;
                try
                {
                    Thread.sleep(100);
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
            }
            
            this.GainCard(decide, false, false);
            Main.gameLog += this.Name() + " gains a " + Main.supply.get(decide).Name() + "\n";
            
            decide = -1;
            gaining = false;
            prompt = "";
        }
        else
            System.out.println("There are no cards available to gain.");
        
        Main.actionSemaphore.release();
    }
    
    public void Trash(int toTrash, boolean fewer)
    {
        try
        {
            Main.dataSemaphore.acquire();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        for(int j = 0; j < this.Hand().size(); j++)
        {
            Card choiceCard = this.Hand().get(j);
            this.Choice.add(choiceCard);
        }

        toChoose = toTrash;
        chooseFewer = fewer;
        
        if(chooseFewer)
            prompt = "Choose up to " + toChoose + " cards to trash.\n";
        else
            prompt = "Choose " + toChoose + " cards to trash.\n";

        Main.dataSemaphore.release();
        
        System.out.println("Redraw in Trash.");
        Main.Redraw(this.playerNum);
        
        while((chooseFewer && !choiceMade) || (!chooseFewer && chosen.size() != toChoose))
        {
            try
            {
                Thread.sleep(100);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
        
        Collections.sort(chosen, Collections.reverseOrder());
        for(int i = 0; i < chosen.size(); i++)
        {
            System.out.println(this.Hand.size() + ", " + i);
            this.TrashCard(chosen.get(i));
        }
        
        toChoose = -1;
        prompt = "";
        chosen = new ArrayList<Integer>();
        choiceMade = false;
        Choice = new ArrayList<Card>();
        
        System.out.println("Done trashing.");
        Main.actionSemaphore.release();
    }
    
    public void DeckToDiscard()
    {
        try
        {
            Main.dataSemaphore.acquire();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        prompt = "Would you like to put your deck into your discard pile?";
        System.out.println("Prompt set.");
        
        Main.dataSemaphore.release();
        
        System.out.println("Redraw in DeckToDiscard.");
        Main.Redraw(Main.currentPlayer);
        
        while(decide == -1)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
        
        if(decide == 1)
        {
            while(Deck.size() > 0)
            {
                Discard.add(Deck.remove(0));
            }
        }
        
        prompt = "";
        decide = -1;
        Main.actionSemaphore.release();
    }
    
    public static void Attack(int attackNum)
    {
        System.out.println("Attack: " + attackNum);
        if(attackNum == Card.AttackCurse)
        {
            int curseNum = Main.FindCard("Curse");
            for(int i = 1; i < Main.numPlayers; i++)
            {
                int nextPlayer = (Main.currentPlayer + i)%Main.numPlayers;
                if(Main.players.get(nextPlayer).GainCard(curseNum, false, false))
                    Main.gameLog += Main.players.get(nextPlayer).Name() + "gains a curse.\n";
            }
        }
        else if(attackNum == Card.AttackDiscard)
        {
            Thread[] thread = new Thread[Main.numPlayers - 1];
            for(int i = 1; i < Main.numPlayers; i++)
            {
                final int nextPlayer = (Main.currentPlayer + i)%Main.numPlayers;
                final int toDiscard = Main.players.get(nextPlayer).Hand().size() - Card.AttackHandSize;
                
                if(toDiscard > 0)
                {
                    thread[i - 1] = (new Thread()
                    {
                        public void run()
                        {
                            discardRunning++;
                            Main.players.get(nextPlayer).Discard(nextPlayer, toDiscard, false);
                            discardRunning--;
                            System.out.println("Redraw in DiscardAttack thread for player " + (nextPlayer + 1));
                            Main.Redraw(nextPlayer);
                        }

                    });
                
                    try
                    {
                        Main.dataSemaphore.acquire(1);
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }

                    thread[i - 1].start();
                }
            }
        }
        
        else if(attackNum == Card.AttackVictoryOnDeck)
        {
            for(int i = 1; i < Main.numPlayers; i++)
            {
                final int nextPlayer = (Main.currentPlayer + i)%Main.numPlayers;
                {
                    if(!Main.players.get(nextPlayer).VictoryOnDeck())
                    {
                        Main.players.get(nextPlayer).RevealHand();
                    }
                }
            }
        }
        
        while(discardRunning > 0)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
        
        Main.actionSemaphore.release();
        System.out.println("Finishing attack.");
    }
    
    public int Discard(int nextPlayer, int toDiscard, boolean discardFewer)
    {
        int numDiscarded = -1;
        
        for(int j = 0; j < this.Hand().size(); j++)
        {
            Card card = this.Hand().get(j);
            this.Choice().add(card);
        }

        toChoose = toDiscard;
        chooseFewer = discardFewer;
        
        if(chooseFewer)
            prompt = "Choose up to " + toChoose + " cards to discard.\n";
        else
            prompt = "Choose " + toChoose + " cards to discard.\n";

        Main.dataSemaphore.release();

        //System.out.println("Redraw 1 in DiscardAttack.");
        Main.Redraw(nextPlayer);

        while(chosen.size() != toDiscard || (chooseFewer && !choiceMade) || chosen.size() > toDiscard)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
        
        numDiscarded = chosen.size();
        
        Collections.sort(chosen, Collections.reverseOrder());
        for(int i = 0; i < chosen.size(); i++)
        {
            this.DiscardCard(chosen.get(i));
        }

        Main.gameLog += this.Name() + " discards " + toChoose + " cards.\n";

        toChoose = -1;
        prompt = "";
        chosen = new ArrayList<Integer>();
        choiceMade = false;
        Choice = new ArrayList<Card>();

        //System.out.println("Redraw 2 in DiscardAttack.");
        Main.Redraw(nextPlayer);
        
        return numDiscarded;
    }
    
    public boolean VictoryOnDeck()
    {
        System.out.println("VictoryOnDeck");
        for(int i = 0; i < this.Hand.size(); i++)
        {
            Card card = this.Hand.get(i);
            if(card.Type() == Card.VictoryType)
                this.Choice.add(card);
        }
        
        System.out.println(Choice.size());
        
        if(Choice.size() > 0)
        {
            Card choiceCard = Choice.get(0);
            if(Choice.size() > 1)
            {
                toChoose = 1;
                chooseFewer = false;
                prompt = "Choose a victory card to put back on your deck.\n";
                
                Main.Redraw(this.playerNum);
                
                while(chosen.size() != 1)
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                }
                
                choiceCard = Choice.get(chosen.get(0));
            }
            
            System.out.println(choiceCard.Name());
            
            int handLoc = this.FindInHand(choiceCard);
            this.DiscardCard(handLoc);
            
            prompt = "";
            Choice = new ArrayList<Card>();
            chosen = new ArrayList<Integer>();
            choiceMade = false;
            Main.gameLog += this.Name() + " reveals a " + choiceCard.Name() + " and puts it on his deck.\n";
            Main.Redraw(this.playerNum);
            return true;
        }
        else
            return false;
    }
    
    public void RevealHand()
    {
        Main.gameLog += this.Name() + " reveals a hand of ";
        for(int i = 0; i < this.Hand.size() - 1; i++)
        {
            Main.gameLog += this.Hand.get(i).Name() + ", ";
        }
        Main.gameLog += this.Hand.get(Hand.size() - 1) + ".\n";
    }
    
    public static boolean Choosing()
    {
        for(int i = 0; i < Main.numPlayers; i++)
        {
            if(Main.players.get(i).toChoose > 0)
                return true;
        }
        
        return false;
    }
    
    public void BuyCard(int cardNum)
    {
        phase = buyPhase;
        Card card = Main.supply.get(cardNum);
        if(buys < 0)
            System.out.println("Error: You have no more buys.");
        else if(money < card.Cost())
            System.out.println("Error: You do not have enough money to buy that. Cost: " + card.Cost());
        else
        {
            if(GainCard(cardNum, true, false))
            {
                buys--;
                Main.gameLog += (name + " buys a(n) " + card.Name() + "\n");
            }
        }
        
        if(buys == 0)
        {
            //System.out.println("You have no more buys. Ending turn.");
            NewTurn();
        }
        
        System.out.println("Redraw in BuyCard.");
        for(int i = 0; i < Main.numPlayers; i++)
            Main.Redraw(i);
    }
    
    public boolean GainCard(int cardNum, boolean pay, boolean topDeck)
    {
        Card card = Main.supply.get(cardNum);
        if(card.Supply() == 0)
        {
            System.out.println("Error: That card is no longer in the supply.");
            return false;
        }
        else
        {
            if(pay)
                money -= card.Cost();
            
            Main.supply.get(cardNum).DecreaseSupply();
            if(Main.supply.get(cardNum).Supply() == 0)
                Main.pilesGone++;
                
            Discard.add(card);
            return true;
        }
    }
    
    public void NewTurn()
    {
        if(!Main.End())
        {
            while(Hand.size() > 0)
                Discard.add(Hand.remove(0));

            while(InPlay.size() > 0)
                Discard.add(InPlay.remove(0));

            this.Draw(startHand);
            actions = startActions;
            buys = startBuys;
            money = startMoney;
            phase = actionPhase;
            Main.currentPlayer = (Main.currentPlayer + 1)%Main.numPlayers;
            if(Main.currentPlayer == 0)
                turn++;
            Main.gameLog += ("\n" + Main.players.get(Main.currentPlayer).Name() + ", Turn " + turn + "\n");
            
            for(int i = 0; i < Main.numPlayers; i++)
                Main.Redraw(i);
        }
        else
        {
            System.out.println("End of Game!");
            ShowScores();
            System.exit(0);
        }
    }
    
    public void ShowScores()
    {
        String message = "Turns: " + turn + "\nFinal scores: ";
        
        for(int i = 0; i < Main.numPlayers; i++)
            message += Main.players.get(i).Name() + ": " + Main.players.get(i).CountPoints();
        
        JOptionPane.showMessageDialog(Main.windows.get(0), message);
    }
    
    public int CountPoints()
    {
        int totalPoints = 0;
        for(int i = 0; i < Hand.size(); i++)
        {
            if(Hand.get(i).Type() == Card.VictoryType)
            {
                VictoryCard tempCard = (VictoryCard)Hand.get(i);
                totalPoints += tempCard.Points();
            }
        }
        
        for(int i = 0; i < Discard.size(); i++)
        {
            if(Discard.get(i).Type() == Card.VictoryType)
            {
                VictoryCard tempCard = (VictoryCard)Discard.get(i);
                totalPoints += tempCard.Points();
            }
        }
        
        for(int i = 0; i < InPlay.size(); i++)
        {
            if(InPlay.get(i).Type() == Card.VictoryType)
            {
                VictoryCard tempCard = (VictoryCard)InPlay.get(i);
                totalPoints += tempCard.Points();
            }
        }
        
        for(int i = 0; i < Deck.size(); i++)
        {
            if(Deck.get(i).Type() == Card.VictoryType)
            {
                VictoryCard tempCard = (VictoryCard)Deck.get(i);
                totalPoints += tempCard.Points();
            }
        }
        
        return totalPoints;
    }
}
