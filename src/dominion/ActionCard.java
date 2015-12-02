/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

import java.util.Scanner;
import java.util.ArrayList;

/**
 *
 * @author timothyjohnson
 */
public class ActionCard extends Card
{
    private ArrayList<Instruction> commands;
    
    public ActionCard(String name, String expansion, int type, int cost, int supply, ArrayList<Instruction> commands)
    {
        super(name, expansion, type, cost, supply);
        this.commands = commands;
    }
    
    public ArrayList<Instruction> Instructions()
    {
        return commands;
    }
}
