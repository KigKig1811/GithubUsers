package com.xt.githubusers.data.mapper

import com.xt.githubusers.data.dto.UserDto
import com.xt.githubusers.data.room.entity.UserEntity
import com.xt.githubusers.domain.model.UserModel

object UserMapper {
    fun toModel(dto: UserDto) = UserModel(
        id = dto.id,
        login = dto.login,
        avatarUrl = dto.avatarUrl,
        htmlUrl = dto.htmlUrl,
        followers = dto.followers,
        following = dto.following,
        location = dto.location,
    )

    fun toEntity(user: UserModel) = UserEntity(
        id = user.id,
        login = user.login,
        avatarUrl = user.avatarUrl,
        htmlUrl = user.htmlUrl,
        followers = user.followers,
        following = user.following,
        location = user.location,
    )

    fun entityToModel(entity: UserEntity) = UserModel(
        id = entity.id,
        login = entity.login,
        avatarUrl = entity.avatarUrl,
        htmlUrl = entity.htmlUrl,
        followers = entity.followers,
        following = entity.following,
        location = entity.location,
    )
}

