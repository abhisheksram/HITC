package com.example.hotc.presentation.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hotc.util.getImagesFromFolder
import com.example.hotc.util.getVideosFromFolder
import com.example.hotc.util.handleError
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.io.File

class MyViewModel(val context: Context) : ViewModel() {

    var photosLiveData = MutableLiveData<List<File>>()
    var videosLiveData = MutableLiveData<List<File>>()
    private val compositeDisposable = CompositeDisposable()


    fun getPhotos(folder: File) {
        compositeDisposable.add(
            Single.just(getImagesFromFolder(context, folder.absolutePath))
                .subscribe(
                    { files -> photosLiveData.postValue(files) },
                    { throwable: Throwable? -> handleError(throwable) }
                )
        )
    }

    fun  getVideos(folder: File) {
        compositeDisposable.add(
            Single.just(getVideosFromFolder(context, folder.absolutePath))
                .subscribe(
                    { files -> videosLiveData.postValue(files) },
                    { throwable: Throwable? -> handleError(throwable) }
                )
        )
    }


    class ViewModelFactory(val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
                MyViewModel(context) as T
            } else {
                throw IllegalArgumentException("ViewModel not found")
            }
        }
    }
}