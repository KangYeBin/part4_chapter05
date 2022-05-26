package com.yb.part4_chapter05.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yb.part4_chapter05.data.dao.RepositoryDao
import com.yb.part4_chapter05.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase: RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
}