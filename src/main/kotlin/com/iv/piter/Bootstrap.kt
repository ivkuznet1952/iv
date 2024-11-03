package com.iv.piter

import com.gitlab.mvysny.jdbiorm.JdbiOrm
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.BodySize
import com.vaadin.flow.component.page.Viewport
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import com.iv.piter.security.User
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.vaadinonkotlin.VaadinOnKotlin
import eu.vaadinonkotlin.vaadin.vokdb.dataSource
import jakarta.servlet.ServletContextEvent
import jakarta.servlet.ServletContextListener
import jakarta.servlet.annotation.WebListener
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep


/**
 * Called by the Servlet Container to bootstrap your app. We need to bootstrap the Vaadin-on-Kotlin framework,
 * in order to have support for the database; then we'll run Flyway migration scripts, to make sure that the database is up-to-date.
 * After that's done, your app is ready to be serving client browsers.
 */
@WebListener
class Bootstrap: ServletContextListener {
    override fun contextInitialized(sce: ServletContextEvent?) = try {
        log.info("Starting up")

        log.info("Starting up")
        var jdbc_url = System.getenv("JDBC_URL")
        var jdbc_username = System.getenv("JDBC_USERNAME")
        var jdbc_password = System.getenv("JDBC_PASSWORD")

        log.info("Initializing the database connection");

        if (jdbc_url == null) {
            jdbc_url = "jdbc:postgresql://localhost:5432/iv"
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


        val flyway: Flyway = Flyway.configure()
            .dataSource(VaadinOnKotlin.dataSource)
            .load()
        flyway.migrate()
//        flyway.repair()
        // setup security
        // security interceptor is configured in AppServiceInitListener
//        User(username = "admin", roles = "").apply { setPassword("admin"); save() }
        if (User.findByUsername("admin") == null) User(username = "admin", role = "Администратор", active = true).apply { setPassword("admin"); save() }
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
@CssImport("./themes/my-theme/styles.css")
@Theme(variant = Lumo.DARK)

@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
class AppShell: AppShellConfigurator
