package almeida.rochapaulo.demo.api;

import com.google.inject.Inject;

import almeida.rochapaulo.demo.api.requests.CreateUser;
import almeida.rochapaulo.demo.service.UserManagement;

public class UserManagementRS {

	private final UserManagement service;
	
	@Inject
	public UserManagementRS(UserManagement service) {
		this.service = service;
	}
	
	public void createUser(CreateUser request) {
		service.createUser(request);
	}
	
}
