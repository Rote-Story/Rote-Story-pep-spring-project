package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Message m WHERE m.messageId = :messageId")
    int deleteMessageById(Integer messageId);

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.messageText = :updatedText WHERE m.messageId = :messageId")
    int updateMessageById(Integer messageId, String updatedText);

    @Query("FROM Message m WHERE m.postedBy = :accountId")
    List<Message> findAllPostedBy(Integer accountId);
}
