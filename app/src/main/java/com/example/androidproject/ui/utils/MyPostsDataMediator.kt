package com.example.androidproject.ui.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.androidproject.models.ItemPost
import com.example.androidproject.models.OtherUser
import com.example.androidproject.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class MyPostsDataMediator(
    private val allPosts: LiveData<List<ItemPost>?>,
    private val allUsers: LiveData<List<OtherUser>?>,
    private val currentUser: LiveData<User?>,
) : ReadOnlyProperty<Any?, MediatorLiveData<LibraryData>> {


    private val _libraryData = LibraryData()
    private val libraryData by lazy {
        MediatorLiveData<LibraryData>().apply {
            value = LibraryData()
            addSource(allPosts) { posts ->
                val currentData = value ?: _libraryData
                posts?.let {
                    value = currentData.copy(allPosts = posts
                        .filter { it.owner == FirebaseAuth.getInstance().uid })
                }
            }

            addSource(allUsers) { users ->
                val currentData = value ?: _libraryData
                users?.let {
                    value = currentData.copy(allUsers = users)
                }
            }

            addSource(currentUser) { user ->
                val currentData = value ?: _libraryData
                val posts = allPosts.value ?: currentData.allPosts ?: listOf()
                user ?: return@addSource
                user.let {
                    value = currentData.copy(
                        currentUser = user,
                        allPosts = posts.filter { post ->
                            post.owner == user.id
                        })
                }
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): MediatorLiveData<LibraryData> {
        return libraryData
    }

}