package org.sagebionetworks.bridge.services.auth.services;

import java.util.Collection;
import java.util.List;

import org.openid4java.message.ParameterList;
import org.sagebionetworks.repo.model.AuthorizationConstants.DEFAULT_GROUPS;
import org.sagebionetworks.repo.model.DatastoreException;
import org.sagebionetworks.repo.model.TermsOfUseException;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.model.UserGroup;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.auth.RegistrationInfo;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.repo.web.NotFoundException;

public class UserManagerImpl implements UserManager {

	@Override
	public UserInfo getUserInfo(String userName) throws DatastoreException,
			NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserGroup getDefaultUserGroup(DEFAULT_GROUPS group)
			throws DatastoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserGroup findGroup(String name, boolean b)
			throws DatastoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createUser(NewUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean doesPrincipalExist(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deletePrincipal(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDisplayName(Long principalId) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGroupName(String principalId) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateEmail(UserInfo userInfo, String newEmail)
			throws DatastoreException, NotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<UserGroup> getGroups() throws DatastoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserGroup> getGroupsInRange(UserInfo userInfo, long startIncl,
			long endExcl, String sort, boolean ascending)
			throws DatastoreException, UnauthorizedException {
		// TODO Auto-generated method stub
		return null;
	}
}
