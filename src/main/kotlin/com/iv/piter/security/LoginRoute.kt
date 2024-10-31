package com.iv.piter.security

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.navigateTo
import com.github.mvysny.kaributools.setErrorMessage
import com.iv.piter.admin.CustomerRoute
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import eu.vaadinonkotlin.vaadin.Session
import org.slf4j.LoggerFactory
import javax.security.auth.login.LoginException

/**
 * The login view which simply shows the login form full-screen. Allows the user to log in. After the user has been logged in,
 * the page is refreshed which forces the MainLayout to reinitialize. However, now that the user is present in the session,
 * the reroute to login view no longer happens and the MainLayout is displayed on screen properly.
 */
@Route("login")
@PageTitle("Login")
@AnonymousAllowed
class LoginRoute : KComposite() {
    private lateinit var loginForm: LoginForm
    private val root = ui {
        verticalLayout {

            verticalLayout {
                setSizeFull()
                isPadding = false
                content { center() }

                val loginI18n: LoginI18n = loginI18n {
                    form.title = "Вход"
                    form.username ="имя пользователя"
                    form.password="пароль"
                    form.submit = "Вход"
                    form.forgotPassword = ""
                    additionalInformation = "Введите имя пользователя и пароль"
                }
                loginForm = loginForm(loginI18n)
                loginForm.isForgotPasswordButtonVisible = false

                loginForm.element.executeJs("this.$.vaadinLoginUsername.value = $0;", "admin")
                loginForm.element.executeJs("this.$.vaadinLoginPassword.value = $0;", "admin")

            }
        }
    }

    init {
        loginForm.addLoginListener { e ->
            try {
                Session.loginService.login(e.username, e.password)
                navigateTo<CustomerRoute>()
            } catch (e: LoginException) {
                log.warn("Login failed", e)
                loginForm.setErrorMessage("Ошибка входа", "Неправильный логин или пароль")
            } catch (e: Exception) {
                log.error("Internal error", e)
                loginForm.setErrorMessage("Ошибка", "Обратитесь к администратору")
            }
        }
    }

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(LoginRoute::class.java)
    }
}
