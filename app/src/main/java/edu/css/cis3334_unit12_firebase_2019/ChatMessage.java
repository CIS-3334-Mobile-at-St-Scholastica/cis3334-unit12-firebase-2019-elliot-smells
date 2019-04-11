package edu.css.cis3334_unit12_firebase_2019;

/**
 * Chat message with email and message
 */
public class ChatMessage {

    private String email;
    private String message;

    /**
     * Default constructor
     */
    public  ChatMessage(){
        this.email = "";
        this.message = "";
    }

    /**
     * Constructor with email and message
     * @param e email string
     * @param m message string
     */
    public ChatMessage(String e, String m){
        this.email = e;
        this.message = m;
    }

    /**
     * Set email string
     * @param e email string
     */
    public void setEmail(String e){
        this.email = e;
    }

    /**
     * Get email string
     * @return email string
     */
    public String getEmail(){
        return this.email;
    }

    /**
     * Set message string
     * @param m message string
     */
    public void setMessage(String m){
        this.message = m;
    }

    /**
     * Get message string
     * @return message string
     */
    public String getMessage(){
        return this.message;
    }
}
