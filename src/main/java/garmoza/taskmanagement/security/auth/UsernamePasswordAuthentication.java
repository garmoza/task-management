package garmoza.taskmanagement.security.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class UsernamePasswordAuthentication extends UsernamePasswordAuthenticationToken {

    // has a side effect similar to calling setAuthenticated(false)
    public UsernamePasswordAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
