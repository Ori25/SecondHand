package com.example.androidproject.database.common

import com.example.androidproject.database.local.OtherUserDao
import com.example.androidproject.database.remote.UserRemoteAccess
import com.example.androidproject.helpers.DocumentListener
import com.example.androidproject.helpers.extensions.nullIfEmpty
import com.example.androidproject.models.OtherUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class UserRepository(
    private val localDb: OtherUserDao,
    private val remoteDb: UserRemoteAccess,
    private val localDbOpsScope: CoroutineScope
) {
    companion object {
        const val USERS_LAST_UPDATE_KEY = "users_last_update"
    }

    fun listenAllUsers(): DocumentListener<List<OtherUser>?> {
        return DocumentListener(
            localDb.listenAllUsers()
                .nullIfEmpty(),
            remoteDb.listenAllUsers(lastUpdateKey = USERS_LAST_UPDATE_KEY) {
                localDbOpsScope.launch {
                    localDb.insert(it)
                }
            }
        )
    }

}