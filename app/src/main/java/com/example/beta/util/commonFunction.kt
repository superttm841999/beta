package com.example.beta.util

import android.content.Context
import android.util.Patterns
import com.example.beta.R
import com.example.beta.login.User
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.math.BigInteger
import java.security.MessageDigest

fun md5(input:String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

// validation thing
fun checkValidation(validationItem: MutableMap<String, Boolean>): Boolean{
    var size = validationItem.size
    var valid = 0
    for(d in validationItem){
        if(d.value){
            valid++
        }else{
            valid--
        }
    }
    return valid==size
}

fun validateEmail(email:String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun isEmptyString(value: String) = value.trim() == ""

fun validateMYPhone(phoneNumber: String): Boolean {
    val phoneUtil = PhoneNumberUtil.getInstance()

    return try {
        val numberProto = phoneUtil.parse(phoneNumber, "MY")
        return phoneUtil.isValidNumber(numberProto)
    } catch (e: NumberParseException) {
        System.err.println("NumberParseException was thrown: $e")
        false
    }
}

fun formatMYPhone(phoneNumber: String): String{
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val numberProto = phoneUtil.parse(phoneNumber, "MY")
        phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    } catch (e: NumberParseException) {
        System.err.println("NumberParseException was thrown: $e")
        phoneNumber
    }
}


//Login
fun checkUserStatus(status: Int?):Boolean{
    return when(status){
        0 -> false
        1 -> true
        2 -> false
        else -> false
    }
}


fun userStatusMessage(context:Context, status: Int?):String{
    return when(status){
        0 -> context.getString(R.string.pending_user)
        1 -> context.getString(R.string.active_user)
        2 -> context.getString(R.string.blocked_user)
        else -> context.getString(R.string.pending_user)
    }
}

fun compareUser(oldUser: User, newUser: User):Boolean{
    return oldUser == newUser
}

fun randomAlphaNumericString(desiredStrLength: Int=15): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    return (1..desiredStrLength)
        .map{ kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}
