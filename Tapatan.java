/**
 * Violet Monserate
 * 10/11/2023
 * CSE 123 
 * Section 0 Creative Project: Abstract Strategy Game
 * TA: Hawa
 */

import java.util.*;

/**
 * Represents the board state of Tapatan, and checks for winners, ties, and validity of moves.
 */
public class Tapatan 
{
    /**
     * Represents whose turn it is, as well as the color of each square.
     */
    public enum Color
    {
        BLACK, WHITE; // Default will be null (empty)
    }

    public static final int SIZE = 3; // How wide and tall the grid should be
    public static final int LINE_LENGTH = 37; // Includes the outsides of boxes
    public static final int MARGIN = 2; // Minimum white space amount + side of box, per side
        
    /** 
     * The current board state will be stored as an enum array, standard x, y for 2d array. "No
     * counter" is represented as a null value. For previous board state checking, it's stored as
     * a 9-character long String, with "W" as white, "B" as black, and "E" as empty, left to right,
     * top to bottom. These act as the keys inside of the 
     * Example:
     * [BLACK], [WHITE], [ NULL]
     * [ NULL], [BLACK], [BLACK] -----> "BWEEBBWWE"
     * [WHITE], [WHITE], [ NULL]        
     */ 
    private Color[][] currentBoardState;

    private Map<String, Integer> previousBoardStates;

    // Should increment at start of white turn, also count how many counters each player put down
    private int numberOfRounds; 
    private Color currentPlayer;

    /**
     * Contructs a new object that represents the game of tapatan. Assumes starting player is white,
     * the board is clear, and no rounds have been played yet.
     */
    public Tapatan()
    {
        previousBoardStates = new HashMap<>();
        currentBoardState = new Color[3][3];
        numberOfRounds = 0;
        currentPlayer = Color.WHITE;
    }

    /**
     * Will add a counter to the board, as long as we are allowed to do so (Must not have >3 tokens
     * of your color on the board already, square must be empty). Changes board state to reflect
     * this. Also will increment number of rounds if necessary, and switch players. Will also 
     * archive the new board state for stalemate checking.
     *
     * @param destination where we want to put the piece
     * 
     * @throws IndexOutOfBoundsException coordinate is not within our gird
     * @throws IllegalStateException we are trying to add too many counters, that's not a legal
     *          state
     * @throws IllegalArgumentException the chosen space is already filled with another piece
     */
    public void addCounter(Coordinate destination)
    {
        if (!destination.isValid(SIZE))
        {
            String message = "At least one component of coordinates is Out of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        else if (numberOfRounds >= 3)
        {
            String message = "Cannot add more counters";
            throw new IllegalStateException(message);
        }
        else if (!isSquareOpen(destination))
        {
            String message = "The destination is already filled";
            throw new IllegalArgumentException(message);
        }
        setPoint(currentPlayer, destination);
        archiveCurrentBoardState();
        switchPlayer();
    }

    /**
     * Moves a given counter at an origin to a desired destination. Alters both origin and 
     * destination points inside of 2d array to reflect the current board state. Also will 
     * increment number of rounds if necessary, and switch players. Will also archive the new 
     * board state for stalemate checking.
     *
     * @param origin The place where we are moving our point from, must be within grid and point
     *      has the current player's piece.
     * @param destination The place where we are moving our point to, must be within grid and
     *      point is empty.
     * @throws IndexOutOfBoundsException coordinate(s) is/are not within our grid
     * @throws IllegalArgumentException chosen space is filled, you're moving another player's
     *      piece, or you're trying to move the piece too far
     */
    public void move(Coordinate origin, Coordinate destination)
    {
        if (!origin.isValid(SIZE) || !destination.isValid(SIZE))
        {
            String message = "At least one component of coordinates is Out of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        if (!isSquareOpen(destination))
        {
            String message = "The destination space is already filled";
            throw new IllegalArgumentException(message);
        }
        if (colorAt(origin) == oppositeColor(currentPlayer))
        {
            String message = "You're moving someone else's piece! That's rude...";
            throw new IllegalArgumentException(message);
        }
        double distance = Math.pow(
                Math.pow(origin.getX() - destination.getX(), 2)
                + Math.pow(origin.getY() - destination.getY(), 2), 
                0.5
        );
        if (distance > 1.5) // Max distance is 1 left/right, 1 up/down, max distance equals sqrt(2)
        {
            String message = "You're moving your piece too far";
            throw new IllegalArgumentException(message);
        }
        setPoint(null, origin);
        setPoint(currentPlayer, destination);
        archiveCurrentBoardState();
        switchPlayer();
    }

    /**
     * Checks to see if the current boardstate contains a winner.
     *
     * @return color of winner, null if undecided OR tie
     * @throws invalidArguementException if the square is invalid or already occupied
     */
    public Color winnerColor()
    {
        if (
                (
                    colorAt(0, 0) != null 
                    && colorAt(0, 0) == colorAt(1, 1)
                    && colorAt(1, 1) == colorAt(2, 2)
                )
                ||
                (
                    colorAt(2, 0) != null
                    && colorAt(2, 0) == colorAt(1, 1)
                    && colorAt(1, 1) == colorAt(0, 2)
                )
        )
        {
            return colorAt(1, 1); // Diagonal Win
        }

        for (int i = 0; i < SIZE; i++)
        {
            if (
                    colorAt(0, i) != null 
                    && colorAt(0, i) == colorAt(1, i)
                    && colorAt(1, i) == colorAt(2, i)
            )
            {
                return colorAt(0, i); // Horizontal Win
            }
            else if (
                    colorAt(i, 0) != null 
                    && colorAt(i, 0) == colorAt(i, 1) 
                    && colorAt(i, 1) == colorAt(i, 2)
            )
            {
                return colorAt(i, 0); // Vertical Win
            }
        }
        
        return null;
    }

    /**
     * Checks the current board state and sees if we have a clear winner.
     *
     * @return true if we have a winner, false if we do not
     */
    public boolean hasWinner()
    {
        return winnerColor() != null;
    }

    /**
     * Checks if a board state appears in the arraylist more than 2 times, which should yield a 
     * tie according to the rules of Tapatan.
     *
     * @return true if we have a winner, false if we do not
     */
    public boolean hasTie()
    {
        boolean result = false;
        for (int count : previousBoardStates.values())
        {
            result = result ? result : count >= 3;
        }
        return result;

    }

    /**
     * Returns whether the square already contains a counter (if null, then it's open, otherwise
     * it's taken up by a counter of some sort (use colorAt() method to find that color).
     *
     * @param coordinate the square we want to check
     * @return whether the square is open or not as a true/false
     * @throws IndexOutOfBoundsException coordinate is not within our grid
     */
    public boolean isSquareOpen(Coordinate coordinate)
    {
        if (!coordinate.isValid(SIZE))
        {
            String message = "At least one of the components of coordinate is Out Of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        return colorAt(coordinate) == null;
    }

    /**
     * Returns the {@link Color} of the square at the given coordinate.
     *
     * @param x the x coordinate, from 0 to 2 (inclusive), otherwise throws exception 
     * @param y the y coordinate, from 0 to 2 (inclusive), otherwise throws exception
     * @return the color of the square you are looking for
     * 
     * @throws IndexOutOfBoundsException the square does not exist on the grid
     */
    public Color colorAt(int x, int y)
    {
        return colorAt(new Coordinate(x, y));
    }

    /**
     * Returns the {@link Color} of the square at the given coordinate.
     *
     * @param coordinate the square we are checking. x and y coordinates must be from 0 to 2 
     *      (inclusive), otherwise throws exception 
     * @return the color of the square you are looking for
     * 
     * @throws IndexOutOfBoundsException the square does not exist on the grid
     */
    public Color colorAt(Coordinate coordinate)
    {
        if (!coordinate.isValid(SIZE))
        {
            String message = "At least one of the components of coordinate is Out Of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        return currentBoardState[coordinate.getY()][coordinate.getX()];
    }

    /**
     * Inverts the given color, useful for finding opponent color.
     *
     * @param color given color of type "Color"
     * @return the opposite color (eg. BLACK -> WHITE)
     */
    public Color oppositeColor(Color color)
    {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }


    /**
     * Counts how many times the current board state has happened. otherwise will add
     */
    private int archiveCurrentBoardState()
    {
        String currentBoardAsString = currentBoardStateToString();
        int count;
        if (previousBoardStates.containsKey(currentBoardAsString))
        {
            count = previousBoardStates.get(currentBoardAsString) + 1;
        }
        else
        {
            count = 1;
        }
        previousBoardStates.put(currentBoardAsString, count);
        return count;
    }

    /**
     * Helper class that allows us to create a concise, easily comparable key for use with the
     * previousBoardStates map. 
     * Example: 
     * [BLACK], [WHITE], [ NULL]
     * [ NULL], [BLACK], [BLACK] -----> "BWEEBBWWE"
     * [WHITE], [WHITE], [ NULL]        
     *
     * @return The current board state as a {@link String}, Left-To-Right, Top-To-Bottom, with
     *      "W" representing White, "B" representing Black, and "E" representing empty/null
     */ 
    private String currentBoardStateToString()
    {
        String output = "";
        for (Color[] row : currentBoardState) 
        {
            for (Color square : row) 
            {
                if (square == Color.WHITE)
                {
                    output += "W";
                }
                else if (square == Color.BLACK)
                {
                    output += "B";
                } 
                else
                {
                    output += "E";
                }
            }
        }
        return output;
    }

    /**
     * Changes who the current player is, and also increments the number of rounds, if 
     * appropriate (the next player is white, which is the person who starts the game).
     *  
     */
    private void switchPlayer()
    {
        currentPlayer = oppositeColor(currentPlayer);
        if (currentPlayer == Color.WHITE)
        {
            numberOfRounds++;
        }
        
    }
    
    /**
     * Sets the {@link Color} at the given coordinate, which must be within the grid.
     *
     * @param color the desired {@link Color} that we want, or null if square will be empty.
     * @param coordinate the place we want to put/remove the counter. Must be within grid.
     * 
     * @throws IndexOutOfBoundsException at least 1 part of the coordinate is Out Of Bounds
     */
    public void setPoint(Color color, Coordinate coordinate)
    {
        if (!coordinate.isValid(SIZE))
        {
            String message = "At least one of the components of coordinate is Out Of Bounds";
            throw new IndexOutOfBoundsException(message);
        }
        currentBoardState[coordinate.getY()][coordinate.getX()] = color;
    }

    /**
     * Getter that returns whose turn it is.
     *
     * @return whose turn it is, as a {@link Color} 
     */
    public Color getCurrentPlayer() 
    {
        return currentPlayer;
    }

    /**
     * Prints out the current board state into the terminal that is visually appealing and 
     * readable.
     * I SPENT WAY TOO LONG ON THIS TEMPLATE but it looks very pretty!
     * Example:
     * ┌─────┐   ┌─────┐   ┌─────┐
     * │     │───│     │───│     │
     * └─────┘   └─────┘   └─────┘
     *    │  ╲      │      ╱  │
     *    │   ╲     │     ╱   │
     *    │    ╲    │    ╱    │
     * ┌─────┐  ╲┌─────┐╱  ┌─────┐
     * │BLACK│   │WHITE│   │     │
     * └─────┘  ╱└─────┘╲  └─────┘
     *    │    ╱    │    ╲    │
     *    │   ╱     │     ╲   │
     *    │  ╱      │      ╲  │
     * ┌─────┐   ┌─────┐   ┌─────┐
     * │     │───│     │───│WHITE│
     * └─────┘   └─────┘   └─────┘

     */
    public void printCurrentBoardState()
    {
        String[] values = new String[9]; // Stores all text values, left to right, top to bottom
        for (int i = 0; i < SIZE; i++)
        {
            for (int j = 0; j < SIZE; j++)
            {
                if (currentBoardState[i][j] != null)
                {    
                    values[i * SIZE + j] = currentBoardState[i][j].toString();
                }
                else
                {
                    values[i * SIZE + j] = "     ";
                }
            }
        }   
        System.out.println("       ┌─┐       ┌─┐       ┌─┐       ");
        System.out.println("       │0│       │1│       │2│       ");
        System.out.println("       └─┘       └─┘       └─┘       ");
        System.out.println(" ┌─┐ ┌─────┐   ┌─────┐   ┌─────┐     ");
        System.out.println(String.format(" │0│ │%1$s│───│%2$s│───│%3$s│     ", 
                            values[0], 
                            values[1], 
                            values[2]));
        System.out.println(" └─┘ └─────┘   └─────┘   └─────┘     ");
        System.out.println("        │  ╲      │      ╱  │        ");
        System.out.println("        │   ╲     │     ╱   │        ");
        System.out.println("        │    ╲    │    ╱    │        ");
        System.out.println(" ┌─┐ ┌─────┐  ╲┌─────┐╱  ┌─────┐     ");
        System.out.println(String.format(" │1│ │%1$s│───│%2$s│───│%3$s│     ", 
                            values[3], 
                            values[4], 
                            values[5]));
        System.out.println(" └─┘ └─────┘  ╱└─────┘╲  └─────┘     ");
        System.out.println("        │    ╱    │    ╲    │        ");
        System.out.println("        │   ╱     │     ╲   │        ");
        System.out.println("        │  ╱      │      ╲  │        ");
        System.out.println(" ┌─┐ ┌─────┐   ┌─────┐   ┌─────┐     ");
        System.out.println(String.format(" │2│ │%1$s│───│%2$s│───│%3$s│     ", 
                            values[6], 
                            values[7], 
                            values[8]));
        System.out.println(" └─┘ └─────┘   └─────┘   └─────┘     ");
    }

    /**
     * Prints out the message in a fancy border, with wraparound if neccessary.
     *
     * @param message the message of any length that we want to print to terminal.
     */
    public void printMessage(String message)
    {
        System.out.println("┌───────────────────────────────────┐");
        
        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        String currentLine = "";
        for (String word : words) 
        {
            String sectionOfLine = word + " ";
            if (currentLine.length() + sectionOfLine.length() <= LINE_LENGTH - (2 * MARGIN))
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
            int leftWhitespace = (LINE_LENGTH - line.length()) / 2;

            for (int i = 1; i < leftWhitespace; i++)
            {
                entireLine += " ";
            }
            entireLine += line;
            int rightWhitespace = LINE_LENGTH - leftWhitespace - line.length();
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