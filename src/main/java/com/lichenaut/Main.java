package com.lichenaut;

import com.lichenaut.cmd.Restriction;
import com.lichenaut.listen.MessageListener;
import com.lichenaut.util.MessageScanner;
import com.lichenaut.util.MessageSender;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class Main {
    public static void main(String[] args) throws IOException {
//        String token = Dotenv.load().get("BOT_TOKEN");
        String token = System.getenv("BOT_TOKEN");
        MessageSender messageSender = new MessageSender();
        JDA jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageListener(new MessageScanner(), messageSender))
                .build();
        jda.updateCommands()
                .addCommands(
                        Commands.slash("restrict", "Restrict a user.")
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
                                .addOption(OptionType.USER, "user", "The user to restrict", true)
                                .addOption(OptionType.STRING, "reason", "The restriction reason", false),
                        Commands.slash("unrestrict", "Restore a restricted user’s roles.")
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
                                .addOption(OptionType.USER, "user", "The user to unrestrict", true)
                                .addOption(OptionType.STRING, "reason", "The restriction reason", false),
                        Commands.slash("listrestrictions", "List all active restrictions.")
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
                )
                .queue();
        jda.addEventListener(new Restriction(jda, messageSender));

        if (!new File("data").exists()) {
            Files.createDirectory(Path.of("data"));
        }
    }
}