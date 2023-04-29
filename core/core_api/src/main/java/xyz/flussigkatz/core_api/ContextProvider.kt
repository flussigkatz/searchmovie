package xyz.flussigkatz.core_api

import android.content.Context

interface ContextProvider {
    fun provideContext(): Context
}