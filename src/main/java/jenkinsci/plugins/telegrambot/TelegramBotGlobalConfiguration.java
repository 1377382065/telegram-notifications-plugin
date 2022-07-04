package jenkinsci.plugins.telegrambot;

import hudson.Extension;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import jenkinsci.plugins.telegrambot.telegram.TelegramBotRunner;
import jenkinsci.plugins.telegrambot.utils.StaplerRequestContainer;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class if user for the storing global plugin configuration.
 */
@Extension
public class TelegramBotGlobalConfiguration extends GlobalConfiguration {

    final static String PLUGIN_DISPLAY_NAME = "TelegramBot";

    private final Map<String, String> botStrings;

    private Boolean shouldLogToConsole = Boolean.TRUE;

    private String botToken;

    private Long chatId;

    private String botName;

    /**
     * Called when Jenkins is starting and it's config is loading
     */
    @DataBoundConstructor
    public TelegramBotGlobalConfiguration() {
        try {
            Properties properties = new Properties();
            properties.load(TelegramBotGlobalConfiguration.class.getClassLoader().getResourceAsStream("bot.properties"));
            botStrings = Collections.unmodifiableMap(properties.stringPropertyNames().stream()
                    .collect(Collectors.toMap(Function.identity(), properties::getProperty)));
        } catch (IOException e) {
            throw new RuntimeException("Bot properties file not found", e);
        }
        // Load global Jenkins config
        load();
        // Run the bot after Jenkins config has been loaded
        TelegramBotRunner.getInstance().runBot(botName, botToken, chatId);
    }

    /**
     * Called when Jenkins config is saving via web-interface
     */
    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {

        // Save for the future using
        StaplerRequestContainer.req = req;

        setBotToken(formData.getString("botToken"));
        setBotName(formData.getString("botName"));
        setChatId(formData.getLong("chatId"));

        TelegramBotRunner.getInstance().runBot(botName, botToken, chatId);

        save();
        return super.configure(req, formData);
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return PLUGIN_DISPLAY_NAME;
    }

    public Map<String, String> getBotStrings() {
        return botStrings;
    }

    public Boolean isShouldLogToConsole() {
        return shouldLogToConsole;
    }

    public void setShouldLogToConsole(Boolean shouldLogToConsole) {
        this.shouldLogToConsole = shouldLogToConsole;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = Secret.fromString(botToken).getPlainText();
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

}
