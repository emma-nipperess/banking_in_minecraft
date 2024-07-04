package com.wespac.wespacmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import okhttp3.*;

import java.io.IOException;

public class ReplyCommand {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "http://10.89.247.191:9000/";
    private static final String API_KEY = "your-api-key";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("reply")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            String message = StringArgumentType.getString(context, "message");
                            CommandSourceStack source = context.getSource();
                            if (source.getEntity() instanceof ServerPlayer) {
                                ServerPlayer player = (ServerPlayer) source.getEntity();
                                sendChatMessage(message, player);
                            }
                            return 1;
                        })
                )
        );
    }

    private static void sendChatMessage(String message, ServerPlayer player) {
        RequestBody body = new FormBody.Builder()
                .add("prompt", message)
                .add("max_tokens", "150")
                .build();

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                player.sendSystemMessage(Component.literal("Failed to connect to the chatbot."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    player.sendSystemMessage(Component.literal(responseBody));
                } else {
                    player.sendSystemMessage(Component.literal("The chatbot is unavailable at the moment."));
                }
            }
        });
    }
}
