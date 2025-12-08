package com.dev.taskaroo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform