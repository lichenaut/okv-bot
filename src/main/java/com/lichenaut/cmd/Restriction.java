package com.lichenaut.cmd;

import com.lichenaut.util.MessageSender;
import com.lichenaut.util.RestrictionData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Restriction extends ListenerAdapter {

    private final JDA jda;
    private final MessageSender messageSender;

    /**
     * Retrieves the reason from the slash command event.
     * @param event The slash command interaction event.
     * @return The reason as a String, or a default message if no reason was given.
     */
    private String getReasonFromEvent(SlashCommandInteractionEvent event) {
        OptionMapping reason = event.getOption("reason");
        return (reason == null) ? "no reason given." : reason.getAsString();
    }

    /**
     * Handles slash commands for restricting, unrestricting, and listing users.
     * @param event The slash command interaction event.
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("restrict")) {
            Member member = Objects.requireNonNull(event.getOption("user")).getAsMember();
            String reasonString = getReasonFromEvent(event);
            if (member != null) {
                restrictUser(event, member, reasonString);
            } else {
                event.reply("User not found!").queue();
            }
        } else if (event.getName().equals("unrestrict")) {
            Member member = Objects.requireNonNull(event.getOption("user")).getAsMember();
            String reasonString = getReasonFromEvent(event);
            if (member != null) {
                unrestrictUser(event, member, reasonString);
            } else {
                event.reply("User not found!").queue();
            }
        } else if (event.getName().equals("listrestrictions")) {
            listRestrictions(event);
        }
    }

    /**
     * Restricts a user by saving their roles and restriction reason, removing roles, and adding a restricted role.
     * @param event The slash command interaction event.
     * @param member The member to restrict.
     * @param reason The reason for the restriction.
     */
    private void restrictUser(SlashCommandInteractionEvent event, Member member, @Nullable String reason) {
        String filePath = "data/" + member.getId() + "-roles.ser";
        if (new File(filePath).exists()) {
            event.reply("User restriction file already exists.").setEphemeral(true).queue();
            return;
        }

        List<String> roleIds = member.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        try {
            RestrictionData restrictionData = new RestrictionData(roleIds, reason);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(restrictionData);
            }
            member.getRoles().forEach(role -> Objects.requireNonNull(event.getGuild()).removeRoleFromMember(member, role).queue());
            Role restrictedRole = Objects.requireNonNull(event.getGuild()).getRolesByName("restricted-temp", true).get(0);
            event.getGuild().addRoleToMember(member, restrictedRole).queue();
            event.reply("User restricted and roles saved.").setEphemeral(true).queue();
            messageSender.logRestriction(jda, member, true, reason);
            messageSender.sendRestrictionReason(member, true, reason);
        } catch (Exception e) {
            event.reply("Error saving roles!").queue();
            e.printStackTrace();
        }
    }

    /**
     * Unrestricts a user by restoring their roles and deleting the restriction file.
     * @param event The slash command interaction event.
     * @param member The member to unrestrict.
     * @param reason The reason for the unrestriction.
     */
    private void unrestrictUser(SlashCommandInteractionEvent event, Member member, @Nullable String reason) {
        String filePath = "data/" + member.getId() + "-roles.ser";
        if (!new File(filePath).exists()) {
            event.reply("User restriction file not found.").setEphemeral(true).queue();
            return;
        }

        try {
            RestrictionData restrictionData;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
                restrictionData = (RestrictionData) ois.readObject();
            }
            for (String roleId : restrictionData.roleIds()) {
                Role role = Objects.requireNonNull(event.getGuild()).getRoleById(roleId);
                if (role != null) {
                    event.getGuild().addRoleToMember(member, role).queue();
                }
            }
            Role restrictedRole = Objects.requireNonNull(event.getGuild()).getRolesByName("restricted-temp", true).get(0);
            event.getGuild().removeRoleFromMember(member, restrictedRole).queue();
            Files.deleteIfExists(Paths.get(filePath));
            event.reply("User unrestricted and roles restored.").setEphemeral(true).queue();
            messageSender.logRestriction(jda, member, false, reason);
            messageSender.sendRestrictionReason(member, false, reason);
        } catch (Exception e) {
            event.reply("Error restoring roles!").queue();
            e.printStackTrace();
        }
    }

    /**
     * Lists all currently restricted users and their restriction reasons.
     * @param event The slash command interaction event.
     */
    public void listRestrictions(SlashCommandInteractionEvent event) {
        File directory = new File("data");
        if (!directory.exists() || !directory.isDirectory()) {
            event.reply("No restrictions found.").queue();
            return;
        }

        StringBuilder messageBuilder = new StringBuilder("List of restricted users:\n\n");
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            event.reply("No restrictions found.").queue();
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith("-roles.ser")) {
                String userId = file.getName().replace("-roles.ser", "");
                jda.retrieveUserById(userId).queue(
                        userFound -> {
                            if (userFound != null) {
                                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                                    RestrictionData data = (RestrictionData) ois.readObject();
                                    Member member = event.getGuild().getMember(userFound);
                                    String mention = (member != null) ? member.getAsMention() : userFound.getName();
                                    messageBuilder.append("- ").append(mention)
                                            .append(" (ID: ").append(userId).append("): ")
                                            .append(data.reason()).append("\n");
                                } catch (Exception e) {
                                    System.err.println("Error reading restriction data for user ID: " + userId);
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("User not found for ID: " + userId);
                            }

                            if (file == files[files.length - 1]) {
                                event.reply(messageBuilder.toString()).queue();
                            }
                        },
                        throwable -> {
                            System.err.println("Failed to retrieve user: " + throwable.getMessage());
                            if (file == files[files.length - 1]) {
                                event.reply(messageBuilder.toString()).queue();
                            }
                        }
                );
            }
        }
    }
}
