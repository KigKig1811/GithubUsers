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
//        nodeId = dto.nodeId,
//        type = dto.type,
//        siteAdmin = dto.siteAdmin,
//        url = dto.url,
//        gravatarId = dto.gravatarId,
//        eventsUrl = dto.eventsUrl,
//        followersUrl = dto.followersUrl,
//        followingUrl = dto.followingUrl,
//        gistsUrl = dto.gistsUrl,
//        organizationsUrl = dto.organizationsUrl,
//        receivedEventsUrl = dto.receivedEventsUrl,
//        reposUrl = dto.reposUrl,
//        starredUrl = dto.starredUrl,
//        subscriptionsUrl = dto.subscriptionsUrl,
//        userViewType = dto.userViewType
    )

    fun toEntity(user: UserModel) = UserEntity(
        id = user.id,
        login = user.login,
        avatarUrl = user.avatarUrl,
        htmlUrl = user.htmlUrl,
        followers = user.followers,
        following = user.following,
        location = user.location,
//        nodeId = user.nodeId,
//        type = user.type,
//        siteAdmin = user.siteAdmin,
//        url = user.url,
//        gravatarId = user.gravatarId,
//        eventsUrl = user.eventsUrl,
//        followersUrl = user.followersUrl,
//        followingUrl = user.followingUrl,
//        gistsUrl = user.gistsUrl,
//        organizationsUrl = user.organizationsUrl,
//        receivedEventsUrl = user.receivedEventsUrl,
//        reposUrl = user.reposUrl,
//        starredUrl = user.starredUrl,
//        subscriptionsUrl = user.subscriptionsUrl,
//        userViewType = user.userViewType
    )

    fun entityToModel(entity: UserEntity) = UserModel(
        id = entity.id,
        login = entity.login,
        avatarUrl = entity.avatarUrl,
        htmlUrl = entity.htmlUrl,
        followers = entity.followers,
        following = entity.following,
        location = entity.location,
//        nodeId = entity.nodeId,
//        type = entity.type,
//        siteAdmin = entity.siteAdmin,
//        url = entity.url,
//        gravatarId = entity.gravatarId,
//        eventsUrl = entity.eventsUrl,
//        followersUrl = entity.followersUrl,
//        followingUrl = entity.followingUrl,
//        gistsUrl = entity.gistsUrl,
//        organizationsUrl = entity.organizationsUrl,
//        receivedEventsUrl = entity.receivedEventsUrl,
//        reposUrl = entity.reposUrl,
//        starredUrl = entity.starredUrl,
//        subscriptionsUrl = entity.subscriptionsUrl,
//        userViewType = entity.userViewType
    )
}

