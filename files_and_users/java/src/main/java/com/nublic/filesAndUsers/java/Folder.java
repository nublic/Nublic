package com.nublic.filesAndUsers.java;

import java.io.IOException;

public abstract class Folder {
	public abstract String getPath() throws FileQueryException, IOException;
	public abstract String getName() throws FileQueryException, IOException;
	public abstract User getOwner() throws FileQueryException, IOException;
	
	public boolean canBeReadBy(User user) throws FileQueryException, IOException {
		return user.canRead(this);
	}
	
	public boolean canBeWrittenBy(User user) throws FileQueryException, IOException {
		return user.canWrite(this);
	}
}
