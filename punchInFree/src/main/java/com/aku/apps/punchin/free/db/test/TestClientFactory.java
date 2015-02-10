package com.aku.apps.punchin.free.db.test;

import java.util.ArrayList;
import java.util.Date;

import com.aku.apps.punchin.free.db.ClientFactory;
import com.aku.apps.punchin.free.domain.Client;

public class TestClientFactory implements ClientFactory {

	private static ArrayList<Client> clientList = null;
	
	static {
		clientList = new ArrayList<Client>();
		clientList.add(new Client(IDGenerator.generate(), "Acme Ltd", "peter.akuhata@gmail.com", "+64 01202562037", 0, true, 8, 10, 2, 0));
		clientList.add(new Client(IDGenerator.generate(), "ABC", "peter.akuhata@gmail.com", "", 0, true, 8, 10, 2, 0));
	}

	@Override
	public ArrayList<Client> getList() {
		return clientList;
	}

	@Override
	public Client get(long id) {
		Client item = null;
		
		for (Client c : clientList) {
			if (c.getId() == id) {
				item = c;
				break;
			}
		}
		
		return item;
	}

	@Override
	public Client add(String name, String email, String mobile, double normalWorkingHours, double hourlyRate, double overtimeRate, double mileageRate) {
		Client c = new Client(IDGenerator.generate(), name, email, mobile, clientList.size(), true, normalWorkingHours, hourlyRate, overtimeRate, mileageRate);
		clientList.add(c);
		
		return c;
	}

	@Override
	public void add(Client client) {
		client.setId(IDGenerator.generate());
		client.setSort(clientList.size());
		clientList.add(client);
	}

	@Override
	public void update(Client item) {
		// no need to do anything, object already updated
		item.setModified(new Date());
	}

	@Override
	public int getCount() {
		return clientList.size();
	}

	@Override
	public void resort(Client client, int newPosition) {
		// just shift it in the array list
		clientList.remove(client);
		clientList.add(newPosition, client);

		int index = (client.getSort() > newPosition ? newPosition : client.getSort());
		
		for (int i = index; i < clientList.size(); i++) {
			clientList.get(i).setSort(i);
		}
	}

	@Override
	public ArrayList<Client> getList(boolean activeOnly, String filter) {
		ArrayList<Client> projects = null;

		if (filter == null || filter.length() == 0) {
			projects = getList(activeOnly);
			
		} else {
			projects = new ArrayList<Client>();
			
			for (Client item : clientList) {
				if (item.getName().toLowerCase().startsWith(filter.toLowerCase())) {
					if (item.getActive() || !activeOnly)
						projects.add(item);
				}
			}
		}
		
		return projects;
	}

	@Override
	public ArrayList<Client> getList(boolean activeOnly) {
		ArrayList<Client> clients = null;
		
		if (activeOnly) {
			clients = new ArrayList<Client>();
			
			for (Client item : clients) {
				if (item.getActive())
					clients.add(item);
			}
		} else {
			clients = clientList;
		}
		
		return clients;
	}

	@Override
	public void clearCache() {
		// don't clear this in test mode
	}

	@Override
	public Client getByAndroidLookupKey(String lookupKey) {
		Client client = null;
		
		if (lookupKey != null && lookupKey.length() > 0) {
			for (Client item : clientList) {
				String id = item.getAndroidLookupKey();
				
				if (id != null && id.equals(lookupKey)) {
					client = item;
					break;
				}
			}
		}
		
		return client;
	}
}
