package com.iv.piter

import com.github.mvysny.vaadinboot.VaadinBoot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

/**
 * Run this function to launch your app in Embedded Jetty.
 */
fun main() {

  /*  val bot = ExcursTelegramBot()
    try {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        telegramBotsApi.registerBot(bot)
        println("****** Initializer demo bot....")
        BotConfig.bot = bot
        //
    } catch (e: TelegramApiException) {
        //log.error(e.message)
    } */

    VaadinBoot().run()
}

