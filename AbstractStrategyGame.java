import java.util.*;

/**
* A strategy game where all players have perfect information and no theme
* or narrative around gameplay.
*/
public interface AbstractStrategyGame {
    /**
     * Constructs and returns a String describing how to play the game. Should include
     * any relevant details on how to interpret the game state as returned by toString(),
     * how to make moves, the game end condition, and how to win.
     */
    public String instructions();

    /**
    * Constructs and returns a String representation of the current game state. 
    * This representation should contain all information that should be known to
    * players at any point in the game, including board state (if any) and scores (if any).
    */
    public String toString();

    /**
    * Returns true if the game has ended, and false otherwise.
    */
    public boolean isGameOver();

    /**
    * Returns the index of the player who has won the game,
    * or -1 if the game is not over.
    */
    public int getWinner();

    /**
    * Returns the index of the player who will take the next turn.
    * If the game is over, returns -1.
    */
    public int getNextPlayer();

    /**
    * Takes input from the parameter to specify the move the player
    * with the next turn wishes to make, then executes that move. 
    * If any part of the move is illegal, throws an IllegalArgumentException.
    */
    public void makeMove(Scanner input);
}