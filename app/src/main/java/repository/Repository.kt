package repository

import android.content.Context
import androidx.lifecycle.LiveData
import database.AppDatabase
import database.WordDao
import model.Word

object Repository {
    lateinit var db: AppDatabase
    lateinit var wordDao: WordDao


    fun initDB(context: Context):AppDatabase {
        db = AppDatabase.getMyDataBase(context)
        wordDao = db.wordDao()
        return db
    }

    fun insert(word:Word){
        wordDao.insert(word)
    }

    fun getWordList():List<Word>{
        return wordDao.getWordList()
    }

    fun getCountWordLiveData(): LiveData<Int>{
        return  wordDao.getCountWordLiveData()
    }

    fun search(word:String):Int{
        return wordDao.search(word)
    }

    fun searchMeaning(Meaning:String):Int{
        return wordDao.searchMeaning(Meaning)
    }

    fun getWord(id:Int): Word{
       return wordDao.getWord(id)
    }

    fun update(word: Word){
        wordDao.update(word)
    }

    fun deleteById(id:Int){
        wordDao.deleteById(id)
    }



//    fun deleteWord(word:Word){
//        wordDao.delete(word)
//    }


}