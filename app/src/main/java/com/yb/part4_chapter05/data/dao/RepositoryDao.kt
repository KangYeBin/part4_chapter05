package com.yb.part4_chapter05.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yb.part4_chapter05.data.entity.GithubRepoEntity

@Dao
interface RepositoryDao {
    @Insert
    suspend fun insert(repo: GithubRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repoList: List<GithubRepoEntity>)

    @Query("SELECT * FROM GithubRepository")
    suspend fun getAllRepositories(): List<GithubRepoEntity>

    @Query("DELETE FROM GithubRepository WHERE fullName = :repoName")
    suspend fun remove(repoName: String)

    @Query("DELETE FROM GithubRepository")
    suspend fun clearAll()
}