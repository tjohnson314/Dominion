/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

import java.util.Comparator;

/**
 *
 * @author timothyjohnson
 */

public abstract class Card
{
    protected String name;
    protected String expansion;
    protected int type;
    protected int cost;
    protected int supply;
    
    public static final int TreasureType = 0;
    public static final int VictoryType = 1;
    public static final int ActionType = 2;
    public static final int TreasureArgs = 2;
    public static final int VictoryArgs = 2;
    public static final int ActionArgs = 5;
    
    public static final int AttackCurse = 0;
    public static final int AttackDiscard = 1;
    public static final int AttackVictoryOnDeck = 2;
    public static final int AttackHandSize = 3;
    public static final int DiscardNum = 2;
    
    protected Card(String name, String expansion, int type, int cost, int supply)
    {
        this.name = name;
        this.expansion = expansion;
        this.type = type;
        this.cost = cost;
        this.supply = supply;
    }
    
    public String Name()
    {
        return name;
    }
     
    public int Type()
    {
        return type;
    }
    
    public int Cost()
    {
        return cost;
    }
    
    public int Supply()
    {
        return supply;
    }
    
    public void DecreaseSupply()
    {
        supply--;
    }
}

class CustomComparator implements Comparator<Card>
{
    public int compare(Card card1, Card card2)
    {
        if(card1.Type() < card2.Type())
            return -1;
        else if(card1.Type() > card2.Type())
            return 1;
        else
        {
            return card1.Name().compareTo(card2.Name());
        }
    }
}
