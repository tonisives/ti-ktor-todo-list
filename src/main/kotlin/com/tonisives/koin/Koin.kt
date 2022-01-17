package com.tonisives.koin

import com.tonisives.repository.InMemoryTodoRepository
import com.tonisives.repository.InMemoryUserRepository
import com.tonisives.repository.TodoRepository
import com.tonisives.repository.UserRepository
import org.koin.dsl.module

val appModules = module {
    single { InMemoryTodoRepository() } as TodoRepository
    single { InMemoryUserRepository() } as UserRepository
}
