package com.cooksys.ftd.assignments.collections.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.cooksys.ftd.assignments.collections.util.MissingImplementationException;

/**
 * TODO: Implement this class
 *  <br><br>
 *  A worker is a type of employee that cannot be a superior to another employee.
 *  <br>
 *  A worker should have a name, and, optionally, a manager superior to them.
 */
public class Worker implements Employee {

    // TODO: Does this class need private fields? If so, add them here
    private Manager _manager = null;
    private String _name;

    /**
     * TODO: Implement this constructor.
     *
     * @param name the name of the worker to be created
     */
    public Worker(String name) {
        this(name, null);
    }

    /**
     *  TODO: Implement this constructor.
     *
     * @param name the name of the worker to be created
     * @param manager the direct manager of the worker to be created
     */
    public Worker(String name, Manager manager) {
        _name = name;
        _manager = manager;
    }

    /**
     * TODO: Implement this getter.
     *
     * @return the name of the worker
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * TODO: Implement this getter.
     *
     * @return {@code true} if this worker has a manager, or {@code false} otherwise
     */
    @Override
    public boolean hasManager() {
        return _manager != null;
    }

    /**
     * TODO: Implement this getter.
     *
     * @return the manager of this worker, or null if it has none
     */
    @Override
    public Manager getManager() {
        return _manager;
    }

    /**
     * TODO: Implement this method.
     *  <br><br>
     *  Retrieves the worker's chain of command as a {@code List<Manager>}, starting with their direct {@code Manager},
     *  followed by that {@code Manager}'s {@code Manager}, and so on, until the top of the hierarchy is reached.
     *  <br><br>
     *  The returned list should never be or contain {@code null}.
     *  <br><br>
     *  If the worker does not have a {@code Manager}, an empty
     *  {@code List<Manager>} should be returned.
     *
     * @return a {@code List<Manager>} that represents the worker's chain of command,
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
        if (!(other instanceof Worker))
            return false;
        
        Worker otherWorker = (Worker) other;
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
