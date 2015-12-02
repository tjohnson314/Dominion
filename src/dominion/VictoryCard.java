/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

/**
 *
 * @author timothyjohnson
 */
public class VictoryCard extends Card
{
    //Our only unique stat is the point value.
    private int[] stats = new int[VictoryArgs];

    public VictoryCard()
    {
        super("", "", -1, -1, -1);
        stats = new int[]{-1};
    }
    
    public VictoryCard(String name, String expansion, int type, int cost, int supply, int[] args)
    {
        super(name, expansion, type, cost, supply);
        if(args.length != VictoryArgs)
            System.out.println("Error: incorrect number of arguments for victory card," + name);
        else
            stats = args;
    }

    public int Points()
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
