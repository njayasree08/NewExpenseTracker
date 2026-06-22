package com.example.smartexpensetracker.di

import android.content.Context
import androidx.room.Room
import com.example.smartexpensetracker.data.local.AppDatabase
import com.example.smartexpensetracker.data.local.CategoryDao
import com.example.smartexpensetracker.data.local.TransactionDao
import com.example.smartexpensetracker.data.local.UserDao
import com.example.smartexpensetracker.data.remote.AuthRepository
import com.example.smartexpensetracker.data.remote.AuthRepositoryImpl
import com.example.smartexpensetracker.data.remote.TransactionRepository
import com.example.smartexpensetracker.data.remote.TransactionRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth, userDao: UserDao): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, userDao)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(firestore: FirebaseFirestore, transactionDao: TransactionDao): TransactionRepository {
        return TransactionRepositoryImpl(firestore, transactionDao)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart_expense_tracker_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.transactionDao()
    }
}
