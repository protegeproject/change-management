package edu.stanford.smi.protegex.server_changes;

import edu.stanford.smi.protege.server.RemoteSession;

public class StandaloneSession implements RemoteSession {
	private static StandaloneSession instance;
	
	public static StandaloneSession getInstance() {
		if (instance == null) {
			instance = new StandaloneSession();
		}
		return instance;
	}
	
	private StandaloneSession() {}

	public String getUserName() {
		return null;
	}

	public String getUserIpAddress() {
		return null;
	}

        public int getSessionGroup() {
            return 0;
        }
}
