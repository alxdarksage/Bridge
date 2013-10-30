package org.sagebionetworks.bridge;

import org.sagebionetworks.bridge.common.SynapseStatusMessage;
import org.sagebionetworks.repo.model.auth.NewUser;
import org.sagebionetworks.repo.model.auth.Session;

public interface BridgeClient {
	Session login(NewUser newUser);

	SynapseStatusMessage getSynapseStatus();
}
