package android.ktcodelab.mydailynote.di

import android.ktcodelab.mydailynote.connectivity.NetworkConnectivityObserver
import android.ktcodelab.mydailynote.data.database.ImagesDatabase
import android.ktcodelab.mydailynote.util.Constants.IMAGES_DATABASE
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ImagesDatabase {

        return Room.databaseBuilder(
            context = context,
            klass = ImagesDatabase::class.java,
            name = IMAGES_DATABASE
        ).build()
    }

    @Singleton
    @Provides
    fun provideFirstDao(database: ImagesDatabase) = database.imageToUploadDao()

    @Singleton
    @Provides
    fun provideSecondDao(database: ImagesDatabase) = database.imageToDeleteDao()


    /*-----------------------For Checking Network Connectivity-------------------------*/
    @Singleton
    @Provides
    fun provideNetworkConnectivityObserver(

        @ApplicationContext context: Context

    ) = NetworkConnectivityObserver(context = context)

}