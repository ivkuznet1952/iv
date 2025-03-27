package com.iv.piter

import com.gitlab.mvysny.jdbiorm.JdbiOrm
import com.iv.piter.security.User
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.vaadinonkotlin.VaadinOnKotlin
import eu.vaadinonkotlin.vaadin.vokdb.dataSource
import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener
import jakarta.servlet.annotation.WebListener
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.io.File


//import org.telegram.telegrambots.bots.TelegramLongPollingBot
//import org.telegram.telegrambots.meta.api.objects.Update


//import dev.inmo.tgbotapi.webapps.webApp

/**
 * Called by the Servlet Container to bootstrap your app. We need to bootstrap the Vaadin-on-Kotlin framework,
 * in order to have support for the database; then we'll run Flyway migration scripts, to make sure that the database is up-to-date.
 * After that's done, your app is ready to be serving client browsers.
 */
@WebListener
class Bootstrap : ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent?) = try {
        log.info("Starting up")

        log.info("Starting up")
        var jdbc_url = System.getenv("JDBC_URL")
        var jdbc_username = System.getenv("JDBC_USERNAME")
        var jdbc_password = System.getenv("JDBC_PASSWORD")

        log.info("Initializing the database connection");

        if (jdbc_url == null) {
            jdbc_url = "jdbc:postgresql://localhost:5432/excurs"
            jdbc_username = "iv";
            jdbc_password = "iv";
        }
        log.info("jdbc_Url: " + jdbc_url)
        // println("###################### jdbc_Username: " + jdbc_url)
        //println("###################### jdbc_Username: " + jdbc_Username + " pwd: " + jdbc_Password)
        //  Initialize the database.
        val cfg = HikariConfig().apply {
            jdbcUrl = jdbc_url
            username = jdbc_username
            password = jdbc_password
            minimumIdle = 0
        }
        cfg.initializationFailTimeout = 60000 // need devops

        // Done! The database layer is now ready to be used.
        VaadinOnKotlin.dataSource = HikariDataSource(cfg)
        log.info("Initializing VaadinOnKotlin")
        VaadinOnKotlin.init()

        // Initializes the VoK framework
        // Makes sure the database is up-to-date. See src/main/resources/db/migration for db init scripts.
        log.info("Running DB migrations")
//         val flyway = Flyway.configure().cleanDisabled(false)
//            .dataSource(JdbiOrm.getDataSource())
//            .load()
//        flyway.clean()
//        sleep(5000)


        val flyway: Flyway = Flyway.configure().dataSource(VaadinOnKotlin.dataSource).load()
        flyway.migrate()
//         flyway.repair()
//         setup security
        // security interceptor is configured in AppServiceInitListener

        if (User.findByUsername("admin") == null) User(
            username = "admin",
            role = "Администратор",
            active = true
        ).apply { setPassword("admin"); save() }
/*
        val bot = ExcursTelegramBot()
        try {
            val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
            telegramBotsApi.registerBot(bot)
            println("Initializer demo bot....")
            BotConfig.bot = bot
        } catch (e: TelegramApiException) {
            log.error(e.message)
        } */
        log.info("Initialization complete")

    } catch (t: Throwable) {
        log.error("Bootstrap failed!", t)
        throw t
    }

    override fun contextDestroyed(sce: ServletContextEvent?) {
        log.info("Shutting down")
        log.info("Destroying VaadinOnKotlin")
        VaadinOnKotlin.destroy()
        log.info("Closing database connections")
        JdbiOrm.destroy()
        log.info("Shutdown complete")
    }

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(Bootstrap::class.java)
    }
}


@BodySize(width = "100vw", height = "100vh")
//@Theme("my-theme")
//@Meta(name="ivan", content="iv")
@CssImport("./themes/my-theme/styles.css")
@Theme(variant = Lumo.DARK)
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class AppShell : AppShellConfigurator

object BotConfig {
    val botName: String = "sasterra"
    val token: String = "6038173806:AAGMKgMM1A4SrzUhy-earibyuWQiDMgOiNU"
    val chatId: Long = 436071699
    const val webapp = "https://pqvgup-176-117-129-130.ru.tuna.am"
    var bot: ExcursTelegramBot? = null
}

class ExcursTelegramBot : TelegramLongPollingBot(BotConfig.token), BotCommands {

     override fun getBotUsername(): String = BotConfig.botName

     override fun onUpdateReceived(update: Update) {
        //TODO("Not yet implemented")
        var chatId: Long = 0
        var userId: Long = 0
        var userName: String? = null
        val receivedMessage: String

        //System.out.println("****: " + update.hasMessage());
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId()
            userId = update.getMessage().getFrom().getId()
            userName = update.getMessage().getFrom().getFirstName()

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText()
                println("0000 ****receivedMessage: $receivedMessage")
                botAnswerUtils(receivedMessage, chatId, userName)
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId()
            userId = update.getCallbackQuery().getFrom().getId()
            userName = update.getCallbackQuery().getFrom().getFirstName()
            receivedMessage = update.getCallbackQuery().getData()

            botAnswerUtils(receivedMessage, chatId, userName)
        }
        println("0000 ****chatId: $chatId")
        println("0000 ****userId: $userId")
        println("0000 ****userName: $userName")
        println("0000 ****Long.valueOf(config.getChatId()): " + java.lang.Long.valueOf("436071699".toLong()))

        //
        if (chatId == BotConfig.chatId) {
            //updateDB(userId, userName);
            println("***** UPDATE USER DATA ****userName: $userName")
        }
    }

    override fun getBotToken(): String = BotConfig.token


    private fun botAnswerUtils(receivedMessage: String, chatId: Long, userName: String) {
        when (receivedMessage) {
            "/start" -> startBot(chatId, userName)
            "/order" -> sendOrder(chatId, "Сейчас что-нибудь закажем!")
            "/help" -> sendHelpText(chatId, BotCommands.HELP_TEXT)
            else -> {}
        }
    }

    private fun startBot(chatId: Long, userName: String) {

        //Session.set("bot", this)

        val message = SendMessage()
        message.setChatId(chatId)
        message.text = "Привет, $userName! I'm a Telegram bot.'"
        message.replyMarkup = Buttons(chatId).inlineMarkup()

        val sendPhoto = SendPhoto.builder()
            .chatId(chatId)
            .photo(InputFile(File("/Users/iv/Downloads/foto/kot.png")))
            .caption("caption")
            .parseMode(ParseMode.HTML)
            .build()

        try {
            execute(sendPhoto)
            execute(message)
            log.info("Reply sent")
        } catch (e: TelegramApiException) {
             log.error(e.message)
        }
    }

    private fun sendHelpText(chatId: Long, textToSend: String) {
        val message = SendMessage()
        message.setChatId(chatId)
        message.text = textToSend

        try {
            execute(message)
             log.info("Reply sent")
        } catch (e: TelegramApiException) {
             log.error(e.message)
        }
    }

    fun sendOrder(chatId: Long, textToSend: String) {
        val message = SendMessage()
        message.setChatId(chatId)
        message.text = textToSend

        try {
            execute(message)
             log.info("Reply sent")
        } catch (e: TelegramApiException) {
            log.error(e.message)
        }
    }

     companion object {
         @JvmStatic
         private val log = LoggerFactory.getLogger(ExcursTelegramBot::class.java)
     }
}

interface BotCommands {
    companion object {
        val LIST_OF_COMMANDS: List<BotCommand> = java.util.List.of(
            BotCommand("/start", "start bot"),
            BotCommand("/help", "bot info")
        )

        const val HELP_TEXT: String = "This bot will help to count the number of messages in the chat. " +
                "The following commands are available to you:\n\n" +
                "/start - start the bot\n" +
                "/help - help menu"
    }
}

class Buttons(val chat_id: Long) {

    //
    private val ORDER_BUTTON = InlineKeyboardButton("Заказать")
    private val HELP_BUTTON = InlineKeyboardButton("Помощь")

    fun inlineMarkup(): InlineKeyboardMarkup {

        ORDER_BUTTON.webApp = WebAppInfo(BotConfig.webapp + "/$chat_id")
        HELP_BUTTON.callbackData = "/help"
        //List<InlineKeyboardButton> rowInline = List.of(ORDER_BUTTON, HELP_BUTTON);
        val rowInline = listOf(ORDER_BUTTON, HELP_BUTTON)
        val rowsInLine = listOf(rowInline)

        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = rowsInLine

        return markupInline
    }
}