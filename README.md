# 간단한 계산기 어플
간단한 수행할 수 있는 계산기 어플리케이션

### 1. 개발환경
* IDE: Android Studio Arctic Fox | 2020.3.1 Canary 1
* Language : Kotlin
---
### 2. 사용 라이브러리
* [Room](https://developer.android.com/topic/libraries/architecture/room?hl=ko) : 모바일 DB관련 라이브러리.
---
### 3. 지원기능
1. 간단한 사칙연산 및 나머지 연산 기능
2. 한 연산에 한가지 연산자만 사용 가능(다중 연산 지원x)
3. 사용자가 입력한 연산 기록 기능(연산기록 삭제 기능) 
4. 사용자가 연산자를 입력할 경우 = 버튼을 눌리지 않아도 실시간으로 계산결과를 제공
![image](https://user-images.githubusercontent.com/57440834/139468490-2bd9d1ca-e417-4351-8696-20582a8b6e74.png)
---
### 4. 추가설명

기초적인 어플이기 때문에 ViewBinding 과 같은 라이브러리는 사용하지 않았으며 모두 각각 findViewById로 xml 뷰에 접근하였습니다.

```
private val historyLayout: ConstraintLayout by lazy {
        findViewById(R.id.history_layout)
    }
```

isOperator,hasOperator 라는 두개의 boolean형 변수를 만들어 연산자가 이미 존재하는지 혹은 연산자를 사용하였는지를 체크하였습니다.

```
private var isOperator: Boolean = false
private var hasOperator: Boolean = false
```

텍스트를 공백(" ")을 기준으로 나누어 List<String>으로 반환해주는 split 내장 메서드를 사용하여 입력숫자1 +(연산자) 입력숫자2 와 같이 구분하였습니다.
  ```
  val expressionText = expressionTextView.text.split(" ")
  ```
  
  또한 입력할 수 있는 숫자를 15자리 제한하거나 0을 제일먼저 입력할 수 없도록 제한하였습니다.
  ```
  if (expressionText.last().length >= 15) {
            Toast.makeText(this@MainActivity, "15자리 까지만 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
            return

        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this@MainActivity, "0은 제일 앞에 올 수 없습니다.", Toast.LENGTH_LONG).show()
            return
        }
  ```
  입력한 숫자를 String형으로 받아 Int형은 숫자 범위에 제한이 있으므로 제한이 없는 BigInteger형으로 변환하였습니다.
  ```
  return when (expressionText[1]) {
                "/" -> {
                    (expressionText[0].toBigInteger() / expressionText[2].toBigInteger()).toString()
                }
  ```
  
  Room DB에 데이터를 넣고 빼는 코드들은 모드 Thread를 따로 열어 비동기로 작업하였으며 UI를 그려야 하는 부분이 있으면 runOnUiThread 메서드를 사용하였습니다.
  또한 초기에 스크롤뷰를 선언하고 내부에 리니어레이아웃을 집어넣어 그 내부에 연산기록들을 반복문으로 addView 하여 집어넣는 형식으로 구현하였지만 성능 부분에서 최적화
  하기 위해 리사이클러뷰를 사용하였습니다.
  ```
  //todo DB에 넣어주는 부분(비동기처리)
        Thread {
            db.historyDao().insertHistory(History(null,tempExpressionText,resultText))
        }.start()
  
  //todo 리사이클러뷰 사용
        Thread{
            //DB에 존재하는 데이터 가져와서
            val dbHistory : List<History> = db.historyDao().getAll()
            //아답터에 데이터를 넣어준다.
            historyAdapter = HistoryAdapter(dbHistory.reversed())
            runOnUiThread {
                //리사이클러뷰에 아답터 등록
                recyclerView.adapter = historyAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }.start()
    }
  ```
  
  
  
  
  

