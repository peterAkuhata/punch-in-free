package com.aku.apps.punchin.free.db;

import java.util.ArrayList;

import com.aku.apps.punchin.free.domain.Client;

/**
 * Represents a {@link Client} datasource.
 * 
 * @author Peter Akuhata
 *
 */
public interface ClientFactory extends BaseFactory {
	/**
	 * Returns the full list of clients to the user.
	 * 
	 * @return
	 */
	public abstract ArrayList<Client> getList();

	/**
	 * Returns a client referenced by the specified id number.
	 * 
	 * @param id
	 * @return
	 */
	public abstract Client get(long id);

	/**
	 * Adds a new client to the system.
	 * 
	 * @param name
	 * @param address
	 * @param mobile
	 * @param normalWorkingHours
	 * @param hourlyRate
	 * @return
	 */
	public abstract Client add(String name, String address,
			String mobile, double normalWorkingHours, double hourlyRate,
			double overtimeRate, double mileageRate);

	/**
	 * Updates the properties of the specified client.
	 * 
	 * @param item
	 */
	public abstract void update(Client item);

	/**
	 * Returns a count of the number of clients stored in the system.
	 * 
	 * @return
	 */
	public abstract int getCount();

	/**
	 * Moves the specified client to the position, resorts the client list.
	 * 
	 * @param project
	 * @param position
	 */
	public abstract void resort(Client client, int newPosition);

	/**
	 * Returns a list of filtered clients.
	 * 
	 * @param activeOnly
	 * @param filter
	 * @return
	 */
	public abstract ArrayList<Client> getList(boolean activeOnly,
			String filter);

	/**
	 * Returns a list of projects, either the whole lot, or filtered to only
	 * active ones.
	 * 
	 * @param activeOnly
	 * @return
	 */
	public abstract ArrayList<Client> getList(boolean activeOnly);

	/**
	 * Creates a new client.
	 * @param client
	 */
	public abstract void add(Client client);

	/**
	 * Returns the client that contains the specified android id.
	 * @param lookupKey
	 * @return
	 */
	public abstract Client getByAndroidLookupKey(String lookupKey);
}
