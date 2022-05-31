package com.yb.part4_chapter05.data.response

import com.yb.part4_chapter05.data.entity.GithubRepoEntity

class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepoEntity>
)