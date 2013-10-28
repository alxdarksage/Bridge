package org.sagebionetworks.bridge.services.auth.services;

import org.openid4java.message.ParameterList;
import org.sagebionetworks.repo.model.TermsOfUseException;
import org.sagebionetworks.repo.model.UnauthorizedException;
import org.sagebionetworks.repo.model.UserInfo;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.auth.RegistrationInfo;
import org.sagebionetworks.repo.model.auth.Session;
import org.sagebionetworks.repo.web.NotFoundException;

public class AuthenticationManagerImpl implements AuthenticationManager {

	@Override
	public Session authenticate(String email, String password)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getPrincipalId(String sessionToken)
			throws UnauthorizedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long checkSessionToken(String sessionToken)
			throws UnauthorizedException, TermsOfUseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidateSessionToken(String sessionToken) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePassword(String id, String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSecretKey(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void changeSecretKey(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Session getSessionToken(String username) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasUserAcceptedTermsOfUse(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTermsOfUseAcceptance(String id, Boolean acceptance) {
		// TODO Auto-generated method stub
		
	}
}
