package com.lichenaut;

import com.lichenaut.listen.MessageListener;
import com.lichenaut.util.MessageScanner;
import com.lichenaut.util.MessageSender;
import lombok.Getter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Getter
public class Main {
    public static void main(String[] args) {
//        JDABuilder.createLight(Dotenv.load().get("BOT_TOKEN"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
//                .addEventListeners(new MessageListener(new MessageScanner(), new MessageSender()))
//                .build();
        JDABuilder.createLight(System.getenv("BOT_TOKEN"), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageListener(new MessageScanner(), new MessageSender()))
                .build();
    }
}