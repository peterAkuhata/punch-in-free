package com.aku.apps.punchin.free.domain;

/**
 * Represents a combination of an overtime multiplier and a nice description of it.
 * For example, 1.5 = 'Time and a half', 2 = 'Double time'.
 * @author Peter Akuhata
 *
 */
public class OvertimeRate {
	/**
	 * The overtime multiplier.
	 */
	private double multiplier;
	
	/**
	 * A description of the multiplier.
	 */
	private int description;
	
	/**
	 * Creates an {@link OvertimeRate} object.
	 * @param multiplier
	 * @param description
	 */
	public OvertimeRate(double multiplier, int description) {
		super();
		this.multiplier = multiplier;
		this.description = description;
	}
	
	/**
	 * Returns the overtime multiplier.
	 * @return
	 */
	public double getMultiplier() {
		return multiplier;
	}
	
	/**
	 * Returns the description of the overtime multiplier.
	 * @return
	 */
	public int getDescription() {
		return description;
	}
}
