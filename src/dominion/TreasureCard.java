/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

/**
 *
 * @author timothyjohnson
 */
public class TreasureCard extends Card
{
    //Our only unique stat is the monetary value.
    private int[] stats = new int[TreasureArgs];
    
    public TreasureCard()
    {
        super("", "", -1, -1, -1);
        stats = new int[]{-1};
    }
    
    public TreasureCard(String name, String expansion, int type, int cost, int supply, int[] args)
    {
        super(name, expansion, type, cost, supply);
        if(args.length != TreasureArgs)
            System.out.println("Error: Incorrect number of arguments for treasure card, " + name);
        else
            stats = args;    
    }

    public int Value()
    {
        return stats[0];
    }
    
    /**
     * Returns whether the card is a default card.
     * @return 
     */
    public boolean Default()
    {
        return (stats[1] == 1);
    }
}
