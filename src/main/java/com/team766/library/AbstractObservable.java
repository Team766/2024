package com.team766.library;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A generic base class to aid in implementing the Observable interface.
 *
 * Classes that derive from this should call notifyObservers when their value changes.
 */
public class AbstractObservable<ValueType> implements Observable<ValueType> {
    private List<Observer<ValueType>> _observers = new LinkedList<Observer<ValueType>>();

    @Override
    public void addObserver(Observer<ValueType> obs) {
        Objects.requireNonNull(obs);
        if (_observers.contains(obs)) {
            return;
        }
        _observers.add(obs);
    }

    @Override
    public void removeObserver(Observer<ValueType> obs) {
        _observers.remove(obs);
    }

    /**
     * Notify all observers that have been registered with this Observable that its value has
     * changed.
     *
     * @param value The new value of this Observable.
     */
    protected void notifyObservers(ValueType value) {
        for (Observer<ValueType> obs : _observers) {
            obs.onValueUpdated(value);
        }
    }
}
