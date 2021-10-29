package com.jimdac_todolist.simplecalculatorapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jimdac_todolist.simplecalculatorapp.Dao.HistoryDao
import com.jimdac_todolist.simplecalculatorapp.model.History
//version은 앱을 업데이트하다가 entity의 구조를 변경해야 하는 일이 생겼을 때 이전 구조와 현재 구조를 구분해주는 역할을 한다.
// 만약 구조가 바뀌었는데 버전이 같다면 에러가 뜨며 디버깅이 되지 않는다.
@Database(entities = [History::class],version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun historyDao() : HistoryDao
}