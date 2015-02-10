package com.aku.apps.punchin.free.domain;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents a client in the system.
 * 
 * @author Peter Akuhata
 *
 */
public class Client extends DomainObject implements Activatable {
	
	/**
	 * The client name.
	 */
	private String name;
	
	/**
	 * The client email address.
	 */
	private String email;
	
	/**
	 * The client mobile number.
	 */
	private String mobile;
	
	/**
	 * A value that allows the clients to be sorted.
	 */
	private int sort;
	
	/**
	 * Is the client active or not.
	 */
	private boolean active;
	
	/**
	 * The client's hourly rate.
	 */
	private double hourlyRate;
	
	/**
	 * The normal working hours of this client.  The normal working hours represents
	 * the max time per day until overtime kicks in.
	 */
	private double normalWorkingHours;
	
	/**
	 * A multiple for the hourly rate that works out how much overtime is worth.
	 * For example, 1.5 = Time and a half, 2.0 = Double time.
	 */
	private double overtimeMultiplier;
	
	/**
	 * The client's mileage rate.
	 */
	private double mileageRate;
	
	/**
	 * The android contact lookup key
	 */
	private String androidLookupKey;
	
	/**
	 * The client's address.
	 */
	private String address;
	
	/**
	 * The android email lookup key.
	 */
	private String androidEmailLookupKey;
	
	/**
	 * The android mobile lookup key.
	 */
	private String androidMobileLookupKey;
	
	/**
	 * Returns the android mobile lookup key.
	 * @return
	 */
	public String getAndroidMobileLookupKey() {
		return androidMobileLookupKey;
	}

	/**
	 * Sets the android mobile lookup key.
	 * @param androidMobileLookupKey
	 */
	public void setAndroidMobileLookupKey(String androidMobileLookupKey) {
		this.androidMobileLookupKey = androidMobileLookupKey;
	}

	/**
	 * Returns the android email lookup key.
	 * @return
	 */
	public String getAndroidEmailLookupKey() {
		return androidEmailLookupKey;
	}

	/**
	 * Sets the android lookup key.
	 * @param androidEmailLookupKey
	 */
	public void setAndroidEmailLookupKey(String androidEmailLookupKey) {
		this.androidEmailLookupKey = androidEmailLookupKey;
	}

	/**
	 * Returns the android contact lookup key.
	 * @return
	 */
	public String getAndroidLookupKey() {
		return androidLookupKey;
	}

	/**
	 * Sets the android contact lookup key.
	 * @param androidLookupKey
	 */
	public void setAndroidLookupKey(String androidLookupKey) {
		this.androidLookupKey = androidLookupKey;
	}

	/**
	 * Returns the client's address.
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the client's address.
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Returns the client's mileage rate.
	 * @return
	 */
	public double getMileageRate() {
		return mileageRate;
	}

	/**
	 * Sets the client's mileage rate.
	 * @param mileageRate
	 */
	public void setMileageRate(double mileageRate) {
		this.mileageRate = mileageRate;
	}

	/**
	 * Returns the overtime multiplier.
	 * @return
	 */
	public double getOvertimeMultiplier() {
		return overtimeMultiplier;
	}

	/**
	 * Sets the overtime muliplier.
	 * @param multiplier
	 */
	public void setOvertimeMultiplier(double multiplier) {
		this.overtimeMultiplier = multiplier;
	}

	/**
	 * Returns the client's hourly rate.
	 * @return
	 */
	public double getHourlyRate() {
		return hourlyRate;
	}

	/**
	 * Sets the client's hourly rate.
	 * @param hourlyRate
	 */
	public void setHourlyRate(double hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	/**
	 * Returns client's normal working hours.
	 * @return
	 */
	public double getNormalWorkingHours() {
		return normalWorkingHours;
	}

	/**
	 * Sets the client's normal working hours.
	 * @param normalWorkingHours
	 */
	public void setNormalWorkingHours(double normalWorkingHours) {
		this.normalWorkingHours = normalWorkingHours;
	}

	/**
	 * Creates a {@link Client}.
	 * @param id
	 * @param name
	 * @param email
	 * @param mobile
	 * @param sort
	 * @param active
	 * @param normalWorkingHours
	 * @param hourlyRate
	 * @param overtimeMultiplier
	 * @param mileageRate
	 */
	public Client(long id, String name, String email, String mobile, int sort,
			boolean active, double normalWorkingHours, double hourlyRate,
			double overtimeMultiplier, double mileageRate) {
		
		this(id, name, email, mobile, sort, active, normalWorkingHours, hourlyRate, overtimeMultiplier, mileageRate, 
				"", null, null, null, new Date(), new Date());
	}

	/**
	 * Creates a {@link Client}.
	 * @param id
	 * @param name
	 * @param email
	 * @param mobile
	 * @param sort
	 * @param active
	 * @param normalWorkingHours
	 * @param hourlyRate
	 * @param overtimeMultiplier
	 * @param mileageRate
	 * @param androidId
	 * @param address
	 * @param androidEmailId
	 * @param androidMobileId
	 * @param androidLookupKey
	 */
	public Client(long id, String name, String email, String mobile, int sort,
			boolean active, double normalWorkingHours, double hourlyRate,
			double overtimeMultiplier, double mileageRate, String address,
			String androidLookupKey,
			String androidEmailLookupKey,
			String androidMobileLookupKey) {
		
		this(id, name, email, mobile, sort, active, normalWorkingHours, hourlyRate, overtimeMultiplier, mileageRate, 
				address, androidLookupKey, androidEmailLookupKey, androidMobileLookupKey, new Date(), new Date());
	}

	/**
	 * Creates a {@link Client}.
	 * @param id
	 * @param name
	 * @param email
	 * @param mobile
	 * @param sort
	 * @param active
	 * @param normalWorkingHours
	 * @param hourlyRate
	 * @param overtimeMultiplier
	 * @param mileageRate
	 * @param androidId
	 * @param address
	 * @param androidEmailId
	 * @param androidMobileId
	 * @param androidLookupKey
	 */
	public Client(long id, String name, String email, String mobile, int sort,
			boolean active, double normalWorkingHours, double hourlyRate,
			double overtimeMultiplier, double mileageRate, String address,
			String androidLookupKey,
			String androidEmailLookupKey,
			String androidMobileLookupKey,
			Date created,
			Date modified) {
		
		super(id, created, modified);
		
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.sort = sort;
		this.active = active;
		this.normalWorkingHours = normalWorkingHours;
		this.hourlyRate = hourlyRate;
		this.overtimeMultiplier = overtimeMultiplier;
		this.mileageRate = mileageRate;
		this.address = address;
		this.androidLookupKey = androidLookupKey;
		this.androidEmailLookupKey = androidEmailLookupKey;
		this.androidMobileLookupKey = androidMobileLookupKey;
	}


	/**
	 * Creates a {@link Client}.
	 * @param id
	 * @param name
	 * @param email
	 * @param mobile
	 * @param sort
	 * @param active
	 * @param normalWorkingHours
	 * @param hourlyRate
	 * @param overtimeMultiplier
	 * @param mileageRate
	 * @param androidId
	 * @param address
	 * @param androidEmailId
	 * @param androidMobileId
	 * @param androidLookupKey
	 */
	public Client(long id, String name, String email, String mobile, int sort,
			int active, double normalWorkingHours, double hourlyRate,
			double overtimeMultiplier, double mileageRate, String address,
			String androidLookupKey,
			String androidEmailLookupKey,
			String androidMobileLookupKey,
			long created,
			long modified) {
		
		super(id);
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(created);
		this.setCreated(cal.getTime());
		cal.setTimeInMillis(modified);
		this.setModified(cal.getTime());
		
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.sort = sort;
		this.active = (active == 0 ? false : true);
		this.normalWorkingHours = normalWorkingHours;
		this.hourlyRate = hourlyRate;
		this.overtimeMultiplier = overtimeMultiplier;
		this.mileageRate = mileageRate;
		this.address = address;
		this.androidLookupKey = androidLookupKey;
		this.androidEmailLookupKey = androidEmailLookupKey;
		this.androidMobileLookupKey = androidMobileLookupKey;
	}

	/**
	 * Returns the client's name.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the client's name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the client's email address.
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the client's email address.
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the client's mobile number.
	 * @return
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * Sets the client's mobile number.
	 * @param mobile
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * Returns the sort value.
	 * @return
	 */
	public int getSort() {
		return sort;
	}

	/**
	 * Sets the sort value.
	 * @param sort
	 */
	public void setSort(int sort) {
		this.sort = sort;
	}

	/**
	 * Returns whether the client is active or not.
	 */
	public boolean getActive() {
		return active;
	}

	/**
	 * Sets whether the client is active or not.
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
}
