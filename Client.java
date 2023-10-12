/**
 * Violet Monserate
 * 10/11/2023
 * CSE 123 
 * Section 0 Creative Project: Abstract Strategy Game
 * TA: Hawa
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Manages the Tapatan game played between 2 players, displaying and inputting data from terminal.
 */
public class Client 
{
    
    /**
     * The main function that runs our game of Tapatan.
     *
     * @param args arguments that are put in during runtime, not used
     */
    public static void main(String... args)
    {
        AbstractStrategyGame tapatan = new Tapatan();
        Scanner scanner = new Scanner(System.in);
        printMessage(tapatan.instructions());

        while (!tapatan.isGameOver())
        {
            System.out.println(tapatan);
            String color = tapatan.getNextPlayer() == 0 ? "BLACK" : "WHITE";
            printMessage("It is now " + color + "'s turn");
            System.out.println();
            tapatan.makeMove(scanner);
        }
        System.out.println(tapatan);

        if (tapatan.getWinner() == -1)
        {
            printMessage("There is no winner due to 3 move repetition! Oh well");
        }
        else
        {
            String color = tapatan.getWinner() == 0 ? "BLACK" : "WHITE";
            printMessage(color + " has won... Congrats, I guess... fr tho, thanks for playing! <3");
        }
        scanner.close();
    }

    /**
     * Prints out the message in a fancy border, with wraparound if neccessary. (I need it!
     * The default just looks so bad on the screen with the board state printed out)
     *
     * @param message the message of any length that we want to print to terminal.
     */
    public static void printMessage(String message)
    {
        int lineLength = 37; // Includes the box lines
        int margin = 2; // Minimum white space amount + side of box, per side
        System.out.println("┌───────────────────────────────────┐");
        
        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        String currentLine = "";
        for (String word : words) 
        {
            String sectionOfLine = word + " ";
            if (currentLine.length() + sectionOfLine.length() <= lineLength - (2 * margin))
            {
                currentLine += sectionOfLine;
            }
            else
            {
                lines.add(currentLine.substring(0, currentLine.length() - 1)); // Removes end space
                currentLine = sectionOfLine;
            }
        }
        if (currentLine != "")
        {
            lines.add(currentLine);
        }
        for (String line : lines) 
        {    
            String entireLine = "│";
            int leftWhitespace = (lineLength - line.length()) / 2;

            for (int i = 1; i < leftWhitespace; i++)
            {
                entireLine += " ";
            }
            entireLine += line;
            int rightWhitespace = lineLength - leftWhitespace - line.length();
            for (int i = 1; i < rightWhitespace; i++)
            {
                entireLine += " ";
            }
            entireLine += "│";
            System.out.println(entireLine);
        }
        System.out.println("└───────────────────────────────────┘");
    }
}
