/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.Random;

/**
 *
 * @author timothyjohnson
 */
public class Main
{
    private static final String cardsFile = "Cards.txt"; //Stores a description of all cards and game setup.
    private static ArrayList<Card> kingdomCards;   //A list of all cards in the supply.
    private static ArrayList<Card> defaultCards;
    public static ArrayList<Card> supply;
    
    //Stores the starting deck as a list of integer arrays of length 2. The first
    //value is the index of the card in the supply, and the second is the number of
    //that card in the starting deck.
    public static ArrayList<int[]> startDeck; 
    
    //A list of the indices in the supply of all cards for which the game ends
    //after they run out.
    public static ArrayList<Integer> endCard;
    
    public static int pilesEnd; //The number of piles that need to be depleted to end the game.
    public static int pilesGone = 0; //The number of piles currently depleted.
    public static int actionSize; //The number of action cards in our kingdom.
    public static int kingdomSize; //The total number of cards in our kingdom.
    private static final int maxKingdomSize = 10; //The maximum number of kingdom cards we can include.
    
    public static int numPlayers; //The number of players.
    public static int currentPlayer = 0; //The index of the player whose turn it is currently.
    public static ArrayList<Player> players; //A list of all players.
    public static ArrayList<GameWindow> windows; //A list of game windows, one for each player.
    public static String gameLog; //The entire text of the game log.
    
    //This semaphore prevents data from being added to the game window while it is
    //in the process of being changed. It is initialized with a number of permits equal
    //to the number of players, because some action cards may require all players
    //to make a decision, or at least all other players. The Redraw() function needs to have all
    //of the permits to make any changes.
    
    //A different option would be to have a separate semaphore for each player, since the Redraw()
    //function has been recently changed to apply only to one player at a time, and this change
    //will probably be made in the near future.
    public static Semaphore dataSemaphore;

    //This semaphore prevents play from continuing until the current effect of a given card
    //has been fully resolved.
    public static Semaphore actionSemaphore;
    
    public static void main(String[] args)
    {
        //We first read in all game data and card descriptions from our input file.
        ReadStats();
        System.out.println("Default cards: " + defaultCards.size());
        ChooseKingdom();
        
        //Next we request the number of players for the game from the console.
        System.out.println("Please enter the desired number of players: ");
        Scanner sc = new Scanner(System.in);
        numPlayers = sc.nextInt();
        
        //We then create our list of players, and give them each their starting decks.
        //Currently, the players are simply named Player 1 up to Player n, but we may
        //soon implement the ability to choose names.
        CreateDecks();
        
        //We begin the game log with the first player's turn.
        gameLog = "Game log:\n" + players.get(0).Name() + ", Turn 1\n";
        
        //We initialize our semaphores. As mentioned, the data semaphore receives 
        //a number of permits equal to the number of players, while the action
        //semaphore requires only one permit.
        dataSemaphore = new Semaphore(numPlayers, true);
        actionSemaphore = new Semaphore(1, true);
        
        //Lastly, we construct the game windows for each player and display them.
        //Currently, these are all displayed simultaneously on the same screen.
        //But in the (far) future, this program may become a host for multiple
        //networked computers, each for a different player.
        CreateWindows();
    }
    
    /**
     * Reads in all game and card data from our input file.
     */
    public static void ReadStats()
    {
        String nextLine; //Stores a single line of the input file.
        String name; //Stores the name of a player being added.
        String expansion; //Stores the expansion a card was taken from.
        
        //Stores the string describing the type of each card (Action, Treasure, or Victory).
        //We have not yet supported cards which have multiple such types, or reaction cards.
        String typeString;
        
        //An integer describing the type of each card. The Card class contains the constants
        //for what number each type is assigned. It made the input file much more readable
        //and easier to create with each type stored as a String, but the program only uses
        //the integer values.
        int type;
        
        int amount; //The number of copies of a card in the supply.
        int cost; //The cost of a card.
        
        //For Treasure and Victory cards, this stores an array of all type-specific stats.
        //The Card class stores the constants describing how many parameters are needed, and
        //these are interpreted and assigned by the individual constructors.
        int[] stats;
        
        
        //For action cards, the number of parameters needed to describe all possible cards
        //would be so large as to become very unwieldy. Also, each card requires only a few
        //of the possible parameters. So instead we store the list of instructions on each card.
        //Each instruction is stored as a string followed by an integer. The list of the full
        //encoding is described in the instruction class.
        ArrayList<Instruction> commands = new ArrayList<Instruction>();
        
        
        kingdomCards = new ArrayList<Card>(); //Initializes the kingdom.
        defaultCards = new ArrayList<Card>(); //Initializes the list of default cards.
        supply = new ArrayList<Card>(); //Initializes the list for the full supply.
        
        try
        {
            System.out.println("Reading stats.");
            BufferedReader in = new BufferedReader(new FileReader(cardsFile));
            
            nextLine = in.readLine(); //We first remove the label line at the top of our input file.
            nextLine = in.readLine();

            
            
            //This loop reads in all of the cards in our file. We halt our loop once we reach the label
            //"Starting deck", which signifies that the description of each player's starting deck then
            //follows.
            do
            {
                //System.out.println("Line: " + nextLine);
                Scanner sc = new Scanner(nextLine);
                sc.useDelimiter(",");
                name = sc.next();
                expansion = sc.next();
                typeString = sc.next();
                amount = sc.nextInt();
                cost = sc.nextInt();
                
                //Treasure cards
                if(typeString.equals("Treasure"))
                {
                    type = Card.TreasureType;
                    stats = new int[Card.TreasureArgs];
                    
                    for(int i = 0; i < Card.TreasureArgs; i++)
                        stats[i] = sc.nextInt();
                    
                    TreasureCard newCard = new TreasureCard(name, expansion, type, cost, amount, stats);
                    if(newCard.Default())
                        defaultCards.add(newCard);
                    else
                        kingdomCards.add(newCard);
                }
                
                //Victory cards
                else if(typeString.equals("Victory"))
                {
                    type = Card.VictoryType;
                    stats = new int[Card.VictoryArgs];
                    
                    for(int i = 0; i < Card.VictoryArgs; i++)
                        stats[i] = sc.nextInt();
                    
                    VictoryCard newCard = new VictoryCard(name, expansion, type, cost, amount, stats);
                    if(newCard.Default())
                        defaultCards.add(newCard);
                    else
                        kingdomCards.add(newCard);
                }
                
                //Action cards
                else if(typeString.equals("Action"))
                {
                    type = Card.ActionType;
                    
                    String command;
                    int num;
                    
                    while(sc.hasNext())
                    {
                        command = sc.next();
                        num = sc.nextInt();
                        commands.add(new Instruction(command, num));
                    }
                    
                    ActionCard newCard = new ActionCard(name, expansion, type, cost, amount, commands);
                    kingdomCards.add(newCard);
                    commands = new ArrayList<Instruction>();
                }
                
                nextLine = in.readLine();
                
            }
            while(!nextLine.equals("Starting deck"));
            
            
            startDeck = new ArrayList<int[]>();
            nextLine = in.readLine();
            
            //Each line of our input now contains the name of a card, followed by an integer
            //describing how many copies of that card are in each player's deck. We stop once
            //we reach the line "Turn rules", which gives the data for how each turn begins.
            do
            {
                Scanner sc = new Scanner(nextLine);
                sc.useDelimiter(",");
                
                String startName = sc.next();
                int startAmount = sc.nextInt();
                int newCard = FindCard(startName);
                int[] newArr = new int[]{newCard, startAmount};
                startDeck.add(newArr);
                
                nextLine = in.readLine();
            }
            while(!nextLine.equals("Turn rules"));

            
            //The data for each turn is the number of cards, actions, buys, and coin
            //that each player has. We also provide the number of action cards in our kingdom.
            nextLine = in.readLine();
            nextLine = in.readLine();
            Scanner sc = new Scanner(nextLine);
            sc.useDelimiter(",");
            Main.actionSize = sc.nextInt();
            Player.startHand = sc.nextInt();
            Player.startActions = sc.nextInt();
            Player.startBuys = sc.nextInt();
            Player.startMoney = sc.nextInt();
            
            if(actionSize > maxKingdomSize)
                System.out.println("Warning: Excessive kingdom size.");
            if(actionSize > kingdomCards.size())
            {
                actionSize = kingdomCards.size();
                System.out.println("Warning: truncating kingdom size.");
            }
            
            nextLine = in.readLine();
            nextLine = in.readLine();
            sc = new Scanner(nextLine);
            sc.useDelimiter(",");
            
            //Next we read in the names of all cards for which the game ends when they
            //are depleted. We stop when we reach a number, which gives the number of
            //piles that must disappear to end the game.
            endCard = new ArrayList<Integer>();
            String nextArg = sc.next();
            while(nextArg.charAt(0) < '0' || nextArg.charAt(0) > '9')
            {
                endCard.add(FindCard(nextArg));
                nextArg = sc.next();
            }
            
            //This number is the number of piles that must run out for the game to end.
            pilesEnd = Integer.parseInt(nextArg);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    /**
     * Chooses a random kingdom from our set of possible cards using reservoir sampling.
     * The total size is equal to the value kingdomSize.
     */
    public static void ChooseKingdom()
    {
        Card[] kingdom = new Card[actionSize];
        
        //Reservoir sampling to construct set of action cards
        for(int i = 0; i < actionSize && i < kingdomCards.size(); i++)
            kingdom[i] = kingdomCards.get(i);
        
        Random rng = new Random();
        int next;
        for(int i = actionSize; i < kingdomCards.size(); i++)
        {
            next = rng.nextInt(kingdomCards.size());
            if(next < actionSize)
                kingdom[next] = kingdomCards.get(i);
        }
        
        //Add default cards to supply
        for(int i = 0; i < defaultCards.size(); i++)
            supply.add(defaultCards.get(i));
        
        for(int i = 0; i < actionSize; i++)
            supply.add(kingdom[i]);
    }
    
    /**
     * Returns the index of a given card in our supply.
     * @param name The name of the card to search for.
     * @return The index of that card in the supply.
     */
    public static int FindCard(String name)
    {
        for(int i = 0; i < defaultCards.size(); i++)
        {
            if(defaultCards.get(i).Name().equals(name))
                return i;
        }
        
        return -1;
    }
    
    /**
     * Deals the starting deck to each player. All players receive the same starting deck.
     */
    public static void CreateDecks()
    {
        ArrayList<Card> startingDeck = new ArrayList<Card>();
        for(int i = 0; i < startDeck.size(); i++)
        {
            System.out.println(startDeck.get(i)[0] + ", " + startDeck.get(i)[1]);
            for(int j = 0; j < startDeck.get(i)[1]; j++)
                startingDeck.add(supply.get(startDeck.get(i)[0]));
        }
        
        players = new ArrayList<Player>();
        for(int i = 0; i < numPlayers; i++)
        {
            Player tempPlayer = new Player(startingDeck, ("Player " + (i + 1)), i);
            tempPlayer.Shuffle();
            players.add(tempPlayer);
        }
    }
    
    /**
     * Creates the game windows for each player, and then displays them to begin the game.
     */
    public static void CreateWindows()
    {
        windows = new ArrayList<GameWindow>(numPlayers);
        for(int i = 0; i < numPlayers; i++)
        {
            GameWindow tempWindow = new GameWindow ("Dominion, Player " + (i + 1), i);
            windows.add(tempWindow);
            GameWindow.addComponents(i);
        }
        
        for(int i = 0; i < numPlayers; i++)
        {
            windows.get(i).pack();
            windows.get(i).setVisible(true);
        }
        
        //Sets the window for the first player to be the front window.
        windows.get(currentPlayer).toFront();
    }
    
    /**
     * Checks the ending condition for our game.
     * @return Returns true if the game is over, and false otherwise.
     */
    public static boolean End()
    {
        for(int i = 0; i < endCard.size(); i++)
        {
            //System.out.println(endCard.get(i) + ", " + supply.get(endCard.get(i)).Supply());
            if(supply.get(endCard.get(i)).Supply() == 0)
                return true;
        }
        
        if(pilesGone == pilesEnd)
            return true;
        else
            return false;
    }
    
    /**
     * Redraws the game window for a given player.
     * @param numPlayer The index of a player in our list.
     */
    public static void Redraw(int numPlayer)
    {
        //All data permits must be available before any of the game windows are updated.
        try
        {
            dataSemaphore.acquire(numPlayers);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        //We remove all panels from a player's window and then redraw all of them.
        //This may be slightly inefficient, because it may be that only one panel needs
        //to be redrawn. But checking for that would be more difficult to code, and the
        //benefit would be negligible, since drawing each window is virtually instantaneous.
        Main.windows.get(numPlayer).getContentPane().removeAll();
        GameWindow.addComponents(numPlayer);

        Main.windows.get(numPlayer).repaint();
        Main.windows.get(numPlayer).setVisible(true);
            
        dataSemaphore.release(numPlayers);
        windows.get(currentPlayer).toFront(); //Moves the window for the player whose turn it is to the front.
    }
}
