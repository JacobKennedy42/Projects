package com.cooksys.ftd.assignments.collections.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.cooksys.ftd.assignments.collections.util.MissingImplementationException;

/**
 * TODO: Implement this class
 *  <br><br>
 *  A manager is a type of employee that can be a superior to another employee.
 *  <br>
 *  A manager should have a name, and, optionally, a manager superior to them.
 */
public class Manager implements Employee {

    // TODO: Does this class need private fields? If so, add them here
    private Manager _manager = null;
    private String _name;

    /**
     * TODO: Implement this constructor.
     *
     * @param name the name of the manager to be created
     */
    public Manager(String name) {
        this(name, null);
    }

    /**
     *  TODO: Implement this constructor.
     *
     * @param name the name of the manager to be created
     * @param manager the direct manager of the manager to be created
     */
    public Manager(String name, Manager manager) {
        _name = name;
        _manager = manager;
    }

    /**
     * TODO: Implement this getter.
     *
     * @return the name of the manager
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * TODO: Implement this getter.
     *
     * @return {@code true} if this manager has a manager, or {@code false} otherwise
     */
    @Override
    public boolean hasManager() {
        return _manager != null;
    }

    /**
     * TODO: Implement this getter.
     *
     * @return the manager of this manager, or {@code null} if it has none
     */
    @Override
    public Manager getManager() {
        return _manager;
    }

    /**
     * TODO: Implement this method.
     *  <br><br>
     *  Retrieves the manager's chain of command as a {@code List<Manager>}, starting with their direct {@code Manager},
     *  followed by that {@code Manager}'s {@code Manager}, and so on, until the top of the hierarchy is reached.
     *  <br><br>
     *  The returned list should never be or contain {@code null}.
     *  <br><br>
     *  If the manager does not have a {@code Manager}, an empty
     *  {@code List<Manager>} should be returned.
     *
     * @return a {@code List<Manager>} that represents the manager's chain of command,
     */
    @Override
    public List<Manager> getChainOfCommand() {
        if (_manager == null)
            return new LinkedList<Manager>();

        List<Manager> chainSoFar = _manager.getChainOfCommand();
        chainSoFar.add(0, _manager);
        return chainSoFar;
    }

    // TODO: Does this class need custom .equals() and .hashcode() methods? If so, implement them here.
    @Override
    public boolean equals (Object other) {
        if (!(other instanceof Manager))
            return false;
        
        Manager otherWorker = (Manager) other;
        return otherWorker != null
                && Objects.equals(getName(), otherWorker.getName())
                && Objects.equals(getManager(), otherWorker.getManager());
    }
    @Override
    public int hashCode() {
        String managerName = getManager() != null ? getManager().getName() : null; 
        return Objects.hash(getName(), managerName);
    }

    // TODO [OPTIONAL]: Consider adding a custom .toString() method here if you want to debug your code with System.out.println() statements

}
