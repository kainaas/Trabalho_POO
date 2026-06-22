package Model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Base class for models that need to notify observers about state changes.
 *
 * <p>It wraps a {@link PropertyChangeSupport}, which implements the subject side
 * of the Observer pattern used by the view layer.</p>
 */
public abstract class AbstractModel {

    /** Delegate that keeps the registered listeners and dispatches events. */
    protected PropertyChangeSupport propertyChangeSupport;

    /** Creates the model with an empty listener list. */
    public AbstractModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Registers an observer.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a previously registered observer.
     *
     * @param listener the listener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Notifies the observers that a property changed.
     *
     * @param propertyName the property name
     * @param oldValue     the previous value
     * @param newValue     the new value
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
