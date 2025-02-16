package com.example.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

@RestController
public class SocialMediaController {

    AccountService accountService;
    MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /**
     * Handles new account registration Post requests.
     * Username cannot be taken already: if username is taken, then status code 409
     * returned.
     * Username must not be blank: if username is blank, then 400 status code
     * returned in the response body.
     * Password must be at least 4 characters long: if password is too short, 400
     * status code returned in the response body.
     * 
     * @param newAccount - the username and password for the new account to be
     *                   registered
     * @return newly created account with assigned accountId
     */
    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account newAccount) {
        String newPassword = newAccount.getPassword();
        String newUsername = newAccount.getUsername();

        // Checking if username already exists in the database
        Account account = accountService.getAccountByUsername(newUsername);

        // If a null value was not returned, then username is taken
        if (account != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        // Valid username must not be blank to be valid
        // Valid password must be at least 4 characters long
        else if (newUsername.isBlank() || newPassword.length() < 4) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // Valid username and password, persisting account in database
        else {
            newAccount = accountService.persistAccount(newAccount);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(newAccount);
        }
    }

    /**
     * Handles POST login requests. Parses account object from JSON and checks for
     * an account with a matching username and password in the database - login
     * credentials.
     * 
     * @param account - an account object with the username and password for the
     *                login attempt
     * @return an account matching the given username and password if it is found,
     *         otherwise, the user is not authorized to access the account
     */
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {

        // Check for an account matching the username and password login credentials
        Account matchingAccount = accountService.getAccountByLoginCredentials(account);

        // If the matching account is null, then none matching the username or password
        // were found in the database
        if (matchingAccount == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // If the username and password match database records, then the login is
        // successful
        else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(matchingAccount);
        }
    }

    /**
     * Handles POST requests to post a new message. Persists message to database if
     * the message length is not blank or over 255 characters in length and is
     * posted by a user that exists in the database.
     * 
     * @param message - the msssage to be posted and persisted in the database
     * @return message if it meets validation requirements and a 200 status code,
     *         otherwise 401 status code
     */
    @PostMapping("/messages")
    public ResponseEntity<Message> postMessage(@RequestBody Message message) {
        Integer accountId = message.getPostedBy();
        String messageText = message.getMessageText();

        // If message text is blank or over 255 characters, then it is not valid
        if (messageText.isBlank() || messageText.length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // If the user posting the message does not exist in the database, then the
        // message is not valid
        else if (accountService.getAccountById(accountId) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            messageService.persistMessage(message);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(message);
        }
    }

    /**
     * Retrieves all available messages.
     * 
     * @return a JSON object representing a list of all available messages
     */
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(messageService.getAllMessages());
    }

    /**
     * Retrieves a message matching the given messageId if one exists in the
     * database.
     * 
     * @param messageId - the messageId of the message to be updated
     * @return a message with a matching messageId, or nothing if no matching
     *         message is found
     */
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Message message = messageService.getMessageById(messageId);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getAllMessagesFromUser(@PathVariable Integer accountId) {
        List<Message> messageList = messageService.getAllMessagesFromUser(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(messageList);
    }

    /**
     * Deletes a message that matches the messageId parameter if one is found.
     * 
     * @param messageId - the messageId of the message targeted for deletion
     * @return the number of rows affected by the deletion request (1) or nothing if
     *         no matching rows were found
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessageById(@PathVariable Integer messageId) {
        Integer rowsAffected = messageService.deleteMessageById(messageId);
        return ResponseEntity.status(HttpStatus.OK).body(rowsAffected);
    }

    /**
     * Updates an existing message's text. Identifies update message by messageId.
     * Updates text only if a matching message is found.
     * 
     * @param messageId  - path parameter used to identify update target.
     * @param newMessage - message containing updated text.
     * @return the number of rows affected by the update.
     */
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessageById(@PathVariable Integer messageId, @RequestBody Message newMessage) {
        Message oldMessage = messageService.getMessageById(messageId);
        String messageText = newMessage.getMessageText();

        // Check to ensure that a message with a matching messageId exists in the
        // database
        if (oldMessage == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // Check to verify that updated message text is not blank and less than 255
        // characters
        else if (messageText.isBlank() || messageText.length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // Updating message, returning the number of rows affected
        else {
            Integer rowsAffected = messageService.updateMessageById(messageId, messageText);
            return ResponseEntity.status(HttpStatus.OK).body(rowsAffected);
        }
    }

}
