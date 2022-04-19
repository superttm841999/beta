package com.example.beta.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.*
import javax.mail.internet.MimeMessage

class SimpleEmail(
    private var to     : String  = "",
    private var subject: String  = "",
    private var content: String  = "",
    private var isHtml : Boolean = false,
) {
    // --- UPDATE THE FOLLOWINGS -------------------------------------------------------------------

    private val username = "tongem1010@gmail.com"
    private val password = "abc123456test"
    private val personal = "Forgotten Password"

    private val host = "smtp.gmail.com"
    private val port = "587"

    // ---------------------------------------------------------------------------------------------

    private val from = "$personal<$username>"
    private var message: MimeMessage? = null

    fun to(to: String): SimpleEmail {
        this.to = to
        return this
    }

    fun subject(subject: String): SimpleEmail {
        this.subject = subject
        return this
    }

    fun content(content: String): SimpleEmail {
        this.content = content
        return this
    }

    fun isHtml(isHtml: Boolean = true): SimpleEmail {
        this.isHtml = isHtml
        return this
    }

    private fun getMessage(): MimeMessage {
        if (message == null) {
            val prop = Properties()
            prop["mail.smtp.host"] = host
            prop["mail.smtp.port"] = port
            prop["mail.smtp.starttls.enable"] = "true"
            prop["mail.smtp.auth"] = "true"

            val auth = object : Authenticator() {
                override fun getPasswordAuthentication() = PasswordAuthentication(username, password)
            }

            val sess = Session.getDefaultInstance(prop, auth)

            message = MimeMessage(sess)
        }

        return message!!
    }

    fun send(callback: (valid: Boolean) -> Unit = {}) {
        val type = if (isHtml) "text/html;charset=utf-8" else "text/plain;charset=utf-8"

        val msg = getMessage()
        msg.setFrom(from)
        msg.setRecipients(Message.RecipientType.TO, to)
        msg.subject = subject
        msg.setContent(content, type)

        CoroutineScope(Dispatchers.IO).launch {
            // NOTE: Use try-catch-finally block to silent runtime error
            try {
                Transport.send(msg)
                withContext(Dispatchers.Main) {
                    callback(true)
                }
            }catch (ex: MessagingException){
                withContext(Dispatchers.Main) {
                    callback(false)
                }
                ex.message
            }
        }
    }
}