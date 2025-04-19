package com.alcidev.onequestionjournal.model

import kotlin.random.Random

class JournalQuestions() {

    private val questions = mutableListOf<String>("What are three things you’re grateful for today?",
        "Who made your day better today, and why?",
        "What’s a small thing you usually overlook that you appreciated today?",
        "What’s the best thing that happened to you today?",
        "What’s one thing you learned about yourself today?",
        "How did you handle a challenge today?",
        "What’s one decision you’re proud of making today?",
        "If today were a chapter in your life story, what would the title be?",
        "What’s something that made you smile today?",
        "How did you practice self-care today?",
        "What’s one thing you did today that made you feel calm?",
        "What’s one thing you could have done better today?",
        "What’s your favorite song right now?",
        "What’s a funny or random thought you had today?",
        "What’s the most surprising thing that happened to you today?",
        "Who did you interact with the most today, and how did it make you feel?",
        "What was the most productive part of your day?",
        "What’s one thing you wish you had more time for today?",
        "What’s the last thing you thought about before going to bed last night, and did it affect your day?",
        "What was the most peaceful moment of your day?",
        "What’s one thing that didn’t go as planned today?",
        "What’s one thing you did today that made someone else happy?",
        "Did you face an unexpected challenge today? How did you deal with it?",
        "What’s one thing you tried for the first time today?",
        "What’s the most beautiful thing you saw today?",
        "How did the weather influence your mood or activities today?",
        "What did you spend most of your time doing today, and was it worth it?",
        "What’s the most interesting conversation you had today?",
        "What’s a sound, smell, or taste from today that you want to remember?",
        "How do you feel as you look back on your day?",
        "If you could relive one moment from today, what would it be?",
        "Did you accomplish what you set out to do today?",
        "What’s one thing you’ll take away from today to make tomorrow better?",
        "What’s something you’ll look forward to tomorrow after reflecting on today?"
        )

    fun getQuestions():List<String>{
        return questions
    }

    fun getRandomQuestion():String{
        return questions[Random.nextInt(0,questions.size)]
    }
}