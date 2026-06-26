package com.task.tracker.core.common.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Prod

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Fake
