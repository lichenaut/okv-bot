package com.lichenaut.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MessageSender {

    /**
     * Sends the deleted message to the user in a private channel.
     *
     * @param user the user to whom the message will be sent
     * @param message the deleted message that is being sent for reference
     */
    public void sendOriginalMessageBadWord(User user, Message message) {
        user.openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage("The deleted message, for your convenience:\n```" + message.getContentDisplay() + "```").queue();
        }, throwable -> {
            System.out.println("Failed to open private channel: " + throwable.getMessage());
        });
    }

    /**
     * Sends an explanation to the user regarding the deletion of their message due to bad words.
     *
     * @param user the user to whom the explanation will be sent
     * @param exactWords a list of exact blacklisted words found in the message, or null if none
     * @param subWords a list of sub-string blacklisted words found in the message, or null if none
     */
    public void sendAutoModExplanationBadWord(User user, @Nullable List<String> exactWords, @Nullable List<String> subWords) {
        user.openPrivateChannel().queue(privateChannel -> {
            StringBuilder messageBuilder = new StringBuilder("Your message was deleted because it contained ableist vocabulary, as listed below.\n\n");

            if (exactWords != null && !exactWords.isEmpty()) {
                messageBuilder.append("**Exact-spelling blacklisted words:** ");
                for (String word : exactWords) {
                    messageBuilder.append("||").append(word).append("||").append(", ");
                }
                messageBuilder.setLength(messageBuilder.length() - 2);
            }

            if (subWords != null && !subWords.isEmpty()) {
                if (exactWords != null) {
                    messageBuilder.append("\n");
                }
                messageBuilder.append("**Punctuation-ignoring blacklisted words:** ");
                for (String word : subWords) {
                    messageBuilder.append("||").append(word).append("||").append(", ");
                }
                messageBuilder.setLength(messageBuilder.length() - 2);
            }

            messageBuilder.append("\n\nSee https://discord.com/channels/1287983430315016262/1289465102860292157/1291592370034315304 for more information. Thank you for your time.");
            privateChannel.sendMessage(messageBuilder.toString()).queue();
        }, throwable -> System.out.println("Failed to open private channel: " + throwable.getMessage()));
    }
}
