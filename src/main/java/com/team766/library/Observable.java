package com.team766.library;

public interface Observable<ValueType> {
    public void addObserver(Observer<ValueType> obs);

    public void removeObserver(Observer<ValueType> obs);
}
