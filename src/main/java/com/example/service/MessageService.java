package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {

    MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * Persists message object to database.
     * 
     * @param message - the message being persisted in the database
     * @return message object saved to database
     */
    public Message persistMessage(Message message) {
        return messageRepository.save(message);
    }

    /**
     * Fetches an message matching the messageId parameter.
     * 
     * @param messageId - the id of the message to search for
     * @return a message matching the messageId
     */
    public Message getMessageById(Integer messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            return message;
        } else {
            return null;
        }
    }

    /**
     * Fetches an message matching the accountId parameter. The postedBy column
     * references the Account's accountId, which is used to get all messages posted
     * by users with a matching accountId.
     * 
     * @param accountId - the accountId of the user who posted the message
     * @return an account object matching the id parameter
     */
    public List<Message> getAllMessagesFromUser(Integer accountId) {
        return messageRepository.findAllPostedBy(accountId);
    }

    /**
     * Deletes message by messageId and returns the number of rows affected; if no
     * matching id is found, then no records are deleted and null is returned.
     * 
     * @param messageId - the id of the message targeted for deletion
     * @return number of rows deleted, or null if no messages with a matching
     *         messageId were found
     */
    public Integer deleteMessageById(Integer messageId) {
        Integer rowsAffected = messageRepository.deleteMessageById(messageId);
        if (rowsAffected > 0) {
            return rowsAffected;
        } else {
            return null;
        }
    }

    /**
     * Updates a message whose messageId matches the messageId argument passed in by
     * the user.
     * 
     * @param messageId   - the id of the message to be updated
     * @param messageText - the updated message text
     * @return the number of rows affected by the update
     */
    public Integer updateMessageById(Integer messageId, String messageText) {
        Integer rowsAffected = messageRepository.updateMessageById(messageId, messageText);
        if (rowsAffected > 0) {
            return rowsAffected;
        } else {
            return null;
        }
    }
}
