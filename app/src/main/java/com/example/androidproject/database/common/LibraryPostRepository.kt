package com.example.androidproject.database.common

import androidx.lifecycle.map
import com.example.androidproject.database.local.LibraryPostDao
import com.example.androidproject.database.remote.LibraryPostRemoteAccess
import com.example.androidproject.helpers.DocumentListener
import com.example.androidproject.helpers.extensions.nullIfEmpty
import com.example.androidproject.models.ItemPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LibraryPostRepository(
    private val localDb: LibraryPostDao,
    private val remoteDb: LibraryPostRemoteAccess,
    private val localDbOpsScope: CoroutineScope,
) : PostRepo by remoteDb {
    companion object {
        const val LIBRARY_POST_LAST_UPDATE_KEY = "library_posts_last_update"
    }

    fun listenAllPosts(): DocumentListener<List<ItemPost>?> {
        return DocumentListener(
            localDb.listenAllPosts()
                .map { it.filter { post -> !post.deleted } }
                .nullIfEmpty(),
            remoteDb.listenAllPosts(lastUpdateKey = LIBRARY_POST_LAST_UPDATE_KEY) {
                localDbOpsScope.launch {
                    localDb.insert(it)
                }
            }
        )
    }

}