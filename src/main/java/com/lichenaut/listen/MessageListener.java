package com.lichenaut.listen;

import com.lichenaut.util.MessageScanner;
import com.lichenaut.util.MessageSender;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

@RequiredArgsConstructor
public class MessageListener extends ListenerAdapter {

    private final MessageScanner messageScanner;
    private final MessageSender messageSender;

    /**
     * Called when a message is received.
     * This method checks the content of the message for exact and sub-string bad words.
     * If any bad words are found, the message is deleted and an explanation is sent to the user.
     *
     * @param event the event containing information about the received message
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContentDisplay();
        List<String> exactWords = messageScanner.exactScan(messageContent);
        List<String> subWords = messageScanner.subScan(messageContent);

        if (exactWords == null && subWords == null) {
            return;
        }

        message.delete().queue();
        User user = event.getAuthor();
        messageSender.sendAutoModExplanationBadWord(user, exactWords, subWords);
        messageSender.sendOriginalMessageBadWord(user, message);
    }
}