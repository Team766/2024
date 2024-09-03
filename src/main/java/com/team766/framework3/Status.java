package com.team766.framework3;

/**
 * Marker interface for Statuses.  Mechanisms and complex Procedures can publish Statuses to let
 * interested parties know about the current state of the Mechanism or progress of the Procedure.
 * This allows for decoupling between the code that manipulates state (eg within a Mechanism) and
 * code that takes action based on that state (eg, displaying a light pattern on an LED strip so
 * the driver or human player knows about the current state of a mechanism).
 *
 * @see StatusBus
 */
public interface Status {}
