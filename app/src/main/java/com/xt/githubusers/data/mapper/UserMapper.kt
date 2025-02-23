package com.xt.githubusers.data.mapper

import com.xt.githubusers.data.dto.UserDto
import com.xt.githubusers.domain.model.UserModel

object UserMapper {
    fun toModel(dto: UserDto) = UserModel(
        id = dto.id,
        login = dto.login,
        nodeId = dto.nodeId,
        type = dto.type,
        siteAdmin = dto.siteAdmin,
        avatarUrl = dto.avatarUrl,
        htmlUrl = dto.htmlUrl,
        url = dto.url,
        gravatarId = dto.gravatarId,
        eventsUrl = dto.eventsUrl,
        followersUrl = dto.followersUrl,
        followingUrl = dto.followingUrl,
        gistsUrl = dto.gistsUrl,
        organizationsUrl = dto.organizationsUrl,
        receivedEventsUrl = dto.receivedEventsUrl,
        reposUrl = dto.reposUrl,
        starredUrl = dto.starredUrl,
        subscriptionsUrl = dto.subscriptionsUrl,
        userViewType = dto.userViewType
    )
}

