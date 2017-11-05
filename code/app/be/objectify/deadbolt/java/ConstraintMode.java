package be.objectify.deadbolt.java;

public enum ConstraintMode {

	// Only the first constraint in the action composition chain will ever be checked (the remaining ones will always be skipped and NEVER be executed).
	// The default (because of backward compatibility).
    PROCESS_FIRST_CONSTRAINT_ONLY,

    // All constraints in the action composition chain have to be successful.
    AND

}
