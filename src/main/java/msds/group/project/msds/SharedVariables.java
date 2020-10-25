/**
 * This class holds variables that need to be accessed by multiple App pages.
 * Uses a static instance to ensure that each Class that calls it receives the same object.
 *
 * @author  J.Woodhouse, D.B.Dawson, I.J.Atienza, M.J.T.Makunda
 * @version 1.03
 */
package msds.group.project.msds;

import android.app.Application;

public class SharedVariables extends Application {

    private String token;
    private String username;
    private Long sessionTimeStamp;

    /**
     * This line ensures that each class that creates an instance of SharedVariables is able to reference the same SharedVariables object.
     */
    private static SharedVariables instance = new SharedVariables();

    /**
     * Getter function to get the static instance of the class.
     * @return returns the static instance of the class.
     */
    public static SharedVariables getInstance()
    {
        return instance;
    }

    /**
     * Setter method for the token field.
     * @param token string to be set as the token field.
     */
    public void setToken(String token)
    {
        this.token = token;
    }

    /**
     * Getter method for the token field.
     * @return returns the token as a String.
     */
    public String getToken()
    {
        return token;
    }

    /**
     * Setter method for the username field.
     * @param username string to set as the username field.
     */
    public void setUsername (String username)
    {
        this.username = username;
    }

    /**
     * Getter method for the username field.
     * @return returns the username as a String.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Setter method for the SessionTimeStamp field.
     * @param sessionTimeStamp Long to be set as the sessionTimeStamp field.
     */
    public void setSessionTimeStamp(Long sessionTimeStamp)
    {
        this.sessionTimeStamp = sessionTimeStamp;
    }

    /**
     * Getter method for the SessionTimeStamp field.
     * @return returns the SessionTimeStamp as a Long.
     */
    public Long getSessionTimeStamp()
    {
        return sessionTimeStamp;
    }
}
