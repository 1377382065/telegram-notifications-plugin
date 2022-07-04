package jenkinsci.plugins.telegrambot;

import hudson.model.Run;
import jenkinsci.plugins.telegrambot.telegram.TelegramBotRunner;

public class Common {

    public static String infoToMessage(Run<?, ?> run, String message) {
        if (message.isEmpty()) {
            return "Project "
                    + run.getFullDisplayName()
                    + " has finished build. Build status is "
                    + run.getResult();
        } else {
            return message;
        }
    }

    public static void send(Run<?, ?> run, String message) {
        TelegramBotRunner instance = TelegramBotRunner.getInstance();
        instance.getBot().sendMessage(instance.getChatId(), infoToMessage(run, message));
    }

}
