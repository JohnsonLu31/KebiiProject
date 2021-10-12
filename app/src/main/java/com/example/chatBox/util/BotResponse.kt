package com.example.chatBox.util

import com.example.chatBox.util.Constants.OPEN_GOOGLE
import com.example.chatBox.util.Constants.OPEN_SEARCH
import java.lang.Exception

object BotResponse {

    fun basicResponse(_message: String): String {


        val random = (0..2).random()
        val message = _message.toLowerCase()

        return when{

            //Hello
            message.contains("hello") -> {
                when (random) {
                    0 -> "Hello there!"
                    1 -> "Sup"
                    2 -> "Hey"
                    else -> "error"
                }
            }



            //How are you
            message.contains("how are you") -> {
                when (random) {
                    0 -> "I'm doing fin, thanks for asking!"
                    1 -> "I'm hungry"
                    2 -> "Pretty good! How about you?"
                    else -> "error"
                }
            }

            message.contains("flip") && message.contains("coin") -> {
                var r = (0..1).random()
                val result = if (r == 0) "head" else "tails"

                "I flip a coin and it landed on $result"
            }

            //Solve maths
            message.contains("solve") -> {
                val equation: String? = message.substringAfter("solve")

                return try{
                    val answer = SolveMath.solveMath(equation ?: "0")
                    answer.toString()

                }catch (e: Exception){
                    "Sorry, I can't solve that!"
                }
            }

            //Gets the current time
            message.contains("time") && message.contains("?") -> {
                Time.timeStamp()
            }

            //Opens Google
            message.contains("open") && message.contains("google") -> {
                OPEN_GOOGLE
            }

            //Opens Search
            message.contains("search") -> {
                OPEN_SEARCH
            }


            else -> {
                when (random) {
                    0 -> "I don't understand..."
                    1 -> "Idk"
                    2 -> "Try asking me something different!"
                    else -> "error"
                }
            }
        }
    }
}