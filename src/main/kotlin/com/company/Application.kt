package com.company

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("com.company")
                .mainClass(Application.javaClass)
                .start()
    }
}