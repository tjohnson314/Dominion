/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominion;

/**
 *
 * @author timothyjohnson
 */
public class Instruction
{
    private String command;
    private int num;

    public Instruction(String command, int num)
    {
        this.command = command;
        this.num = num;
    }
    
    public String Command()
    {
        return command;
    }
    
    public int Num()
    {
        return num;
    }
}
