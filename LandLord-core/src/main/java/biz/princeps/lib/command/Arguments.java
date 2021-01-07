package biz.princeps.lib.command;

import biz.princeps.lib.exception.ArgumentsOutOfBoundsException;
import biz.princeps.lib.exception.PlayerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 2/5/18
 * <p>
 * This class represents the command arguments which are typed after a command.
 * There are some useful methods to reduce writing of repeating code.
 * Basically said its just a wrapper class around the "raw" string array
 */
public class Arguments {

    // The "raw" data
    private final String[] strings;

    /**
     * Creates a new arguments object out of the raw string array
     *
     * @param strings the typed actual args from the commandexecutor
     */
    public Arguments(String[] strings) {
        this.strings = strings;
    }

    /**
     * Getter Method for the raw data
     *
     * @return the string array of the original args
     */
    public String[] get() {
        return strings;
    }


    /**
     * Tries to get the string on the n-th position.
     * May return ArgumentsOutOfBoundsException, if the index is outofbounds. So you have
     * a possibility to just try-catch it and message back to the user
     *
     * @param index the position
     * @return the actual string on the position
     * @throws ArgumentsOutOfBoundsException in case the index is to large or out of bounds
     */
    public String get(int index) {
        checkForRange(index);
        return strings[index];
    }

    /**
     * Tries to get an int on a given index
     *
     * @param index the index you want get an int from
     * @return the parsed integer
     * @throws ArgumentsOutOfBoundsException in case you are trying to get a value out of bounds
     * @throws NumberFormatException         in case there is no int on the given index
     */
    public int getInt(int index) throws ArgumentsOutOfBoundsException, NumberFormatException {
        checkForRange(index);
        try {
            return Integer.parseInt(strings[index]);
        } catch (NumberFormatException ex) {
            throw new NumberFormatException();
        }
    }

    /**
     * Tries to get a double on a given index
     *
     * @param index the index you want get a double from
     * @return the parsed Double
     * @throws ArgumentsOutOfBoundsException in case you are trying to get a value out of bounds
     * @throws NumberFormatException         in case there is no double on the given index
     */
    public double getDouble(int index) throws ArgumentsOutOfBoundsException, NumberFormatException {
        checkForRange(index);
        try {
            return Double.parseDouble(strings[index]);
        } catch (NumberFormatException ex) {
            throw new NumberFormatException();
        }
    }

    /**
     * Tries to get the strings from the n-th position up to the m-th
     * May return ArgumentsOutOfBoundsException, if one of the indices is outofbounds. So you have
     * a possibility to just try-catch it and message back to the user
     * <p>
     * E.g.
     * String[] { "1", "5", "3" }
     * get(1,3) will return { "5" , "3"}
     *
     * @param from the position from (inclusive) => see the above example
     * @param to   the position to (exclusive) => see the above example
     * @return the actual string on the position
     * @throws ArgumentsOutOfBoundsException in case the index is to large or out of bounds
     */
    public String[] get(int from, int to) throws ArgumentsOutOfBoundsException {
        checkForRange(from);
        checkForRange(to);
        if (from > to) {
            throw new RuntimeException("From cant be larger then to! (" + from + " > " + to + ")");
        }

        return Arrays.copyOfRange(strings, from, to);
    }

    /**
     * Tries to form a player out of the n-th argument
     *
     * @param index the n-th argument, which should be tried to be transformed to a player object
     * @return a online player
     * @throws PlayerNotFoundException       is thrown in case the player could not be found / isn't online
     * @throws ArgumentsOutOfBoundsException in case the index is to large or out of bounds
     */
    public Player getPlayer(int index) throws PlayerNotFoundException, ArgumentsOutOfBoundsException {
        checkForRange(index);

        Player p = Bukkit.getPlayer(strings[index]);

        if (p == null) {
            throw new PlayerNotFoundException(strings[index]);
        }

        return p;
    }


    /**
     * Internal helper method to check the bounds of an index
     *
     * @param index
     * @throws ArgumentsOutOfBoundsException
     */
    private void checkForRange(int index) throws ArgumentsOutOfBoundsException {
        if (index < 0 || index >= strings.length) {
            throw new ArgumentsOutOfBoundsException("Invalid CommandArguments access! Accessed: " + index + " length: " + strings.length);
        }
    }

    /**
     * Getter method for length
     *
     * @return the length of the raw string array
     */
    public int size() {
        return strings.length;
    }

}
