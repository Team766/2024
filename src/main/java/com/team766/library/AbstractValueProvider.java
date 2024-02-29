package com.team766.library;

import java.util.Optional;

abstract class AbstractValueProvider<E> extends AbstractObservable<Optional<E>>
        implements ValueProvider<E> {}
