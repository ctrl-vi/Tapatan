/**
 * Violet Monserate
 * 10/11/2023
 * CSE 123 
 * Section 0 Creative Project: Abstract Strategy Game
 * TA: Hawa
 */

import java.util.Scanner;

/**
 * Manages the Tapatan game played between 2 players, displaying and inputting data from terminal.
 */
public class TapatanGameClient 
{
    
    /**
     * The main function that runs our game of Tapatan.
     *
     * @param args arguements that are put in during runtime, not used
     */
    public static void main(String... args)
    {
        Tapatan tapatan = new Tapatan();
        Scanner terminalScanner = new Scanner(System.in); 

        tapatan.printMessage("Go to the website https://tinyurl.com/tapatan1 for rules");
        tapatan.printCurrentBoardState();
        
        for (int i = 0; i < 6 && !tapatan.hasWinner(); i++)
        {
            tapatan.printMessage("It is now " + tapatan.getCurrentPlayer() + "'s turn");
            
            System.out.print("Input the x coordinate: ");
            int x = terminalScanner.nextInt();
            System.out.print("Input the y coordinate: ");
            int y = terminalScanner.nextInt();

            tapatan.addCounter(new Coordinate(x, y));
            tapatan.printCurrentBoardState();

            if (tapatan.hasWinner())
            {
                tapatan.printMessage(tapatan.winnerColor() + " has won the game! Congrats I guess");
            }
        }

        while (!tapatan.hasTie() && !tapatan.hasWinner())
        {
            tapatan.printMessage("It is now " + tapatan.getCurrentPlayer() + "'s turn");

            System.out.print("Input the origin x coordinate: ");
            int x1 = terminalScanner.nextInt();
            System.out.print("Input the origin y coordinate: ");
            int y1 = terminalScanner.nextInt();
            Coordinate origin = new Coordinate(x1, y1);

            System.out.print("Input the destination x coordinate: ");
            int x2 = terminalScanner.nextInt();
            System.out.print("Input the destination y coordinate: ");
            int y2 = terminalScanner.nextInt();
            Coordinate destination = new Coordinate(x2, y2);

            tapatan.move(origin, destination);
            tapatan.printCurrentBoardState();
        }
        terminalScanner.close();
    }
}
