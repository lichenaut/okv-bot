package com.lichenaut.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MessageSender {

    /**
     * Sends the deleted message to the member in a private channel.
     *
     * @param member  the member to whom the message will be sent
     * @param message the deleted message that is being sent for reference
     */
    public void sendOriginalMessageBadWord(Member member, Message message) {
        sendMessageToMember(member, "The deleted message, for your convenience:\n```" + message.getContentDisplay() + "```");
    }

    /**
     * Sends an explanation to the member regarding the deletion of their message due to bad words.
     *
     * @param member     the member to whom the explanation will be sent
     * @param exactWords a list of exact blacklisted words found in the message, or null if none
     * @param subWords   a list of sub-string blacklisted words found in the message, or null if none
     */
    public void sendAutoModExplanationBadWord(Member member, @Nullable List<String> exactWords, @Nullable List<String> subWords) {
        StringBuilder messageBuilder = new StringBuilder("Your message was deleted because it contained ableist vocabulary, as listed below.\n\n");
        appendBlacklistedWords(messageBuilder, "Exact-spelling blacklisted words:", exactWords);
        appendBlacklistedWords(messageBuilder, "Punctuation-ignoring blacklisted words:", subWords);
        messageBuilder.append("\n\nSee https://discord.com/channels/1287983430315016262/1289465102860292157/1291592370034315304 for more information. Thank you for your time.");
        sendMessageToMember(member, messageBuilder.toString());
    }

    /**
     * Sends the restriction reason to the member in a private channel.
     *
     * @param member the member to whom the message will be sent
     * @param reason the restriction reason that is being sent for reference
     */
    public void sendRestrictionReason(Member member, boolean restricted, String reason) {
        String action = restricted ? "restricted" : "unrestricted";
        sendMessageToMember(member, "You have been " + action + " for:\n```" + reason + "```");
    }

    /**
     * Logs the restriction action in the specified text channel.
     *
     * @param jda       the JDA instance to get the channel
     * @param member    the member being restricted or unrestricted
     * @param reason    the reason for the restriction
     * @param restricted true if the member is being restricted, false if unrestricted
     */
    public void logRestriction(JDA jda, Member member, boolean restricted, String reason) {
        Channel channel = jda.getTextChannelById("1288967533201719470");
        if (channel instanceof TextChannel textChannel) {
            String action = restricted ? "restricted" : "unrestricted";
            String message = String.format("%s has been %s. Reason: ```%s```", member.getAsMention(), action, reason);
            textChannel.sendMessage(message).queue(
                    success -> {},
                    error -> System.err.println("Failed to send restriction log: " + error.getMessage())
            );
        } else {
            System.err.println("Provided channel is not a TextChannel.");
        }
    }

    /**
     * Sends a direct message to a specified member in a private channel.
     *
     * @param member  the member to whom the message will be sent
     * @param message the message to be sent to the member
     */
    private void sendMessageToMember(Member member, String message) {
        member.getUser().openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessage(message).queue(),
                throwable -> System.out.println("Failed to open private channel: " + throwable.getMessage())
        );
    }

    /**
     * Appends a list of blacklisted words to a message builder, formatted with headers.
     *
     * @param messageBuilder the StringBuilder used to construct the message
     * @param header         the header for the section of blacklisted words
     * @param words          a list of blacklisted words to be appended, or null if none
     */
    private void appendBlacklistedWords(StringBuilder messageBuilder, String header, @Nullable List<String> words) {
        if (words != null && !words.isEmpty()) {
            messageBuilder.append("**").append(header).append("** ");
            for (String word : words) {
                messageBuilder.append("||").append(word).append("||").append(", ");
            }
            messageBuilder.setLength(messageBuilder.length() - 2);
            messageBuilder.append("\n");
        }
    }
}
