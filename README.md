# WalletMessenger

***Все запросы являются POST-запросами, значения параметров указываются в HTTP-body в формате application/x-www-form-urlencoded:
`a=bla&b=1234`***

### UserController("/user"):

#### Метод для регистрации пользователей
**Путь:** /user/reg/

**Параметр:** phone - телефон пользователя, используется как логин <br>
**Параметр:** name - имя пользователя <br>
**Параметр:** hashpsd - MD5-хеш пароля пользователя <br>
**Выходные данные:** userID + name + phone + image + DefaultClass 

**Пример JSON**: <br>
{
"name":"Alex2",
"userID":8,
"defaultClass":{
"token":"0df23be331dc7fc1cd9372cb7edfb660",
"operationOutput":true
},
"image":null,
"phone":"8-002",
"balance":0.0
}

#### Метод для авторизации пользователя
**Путь:** /user/log/

**Параметр:** phone - телефон пользователя, используется как логин <br>
**Параметр:** hashpsd - MD5-хеш пароля пользователя <br>
**Выходные данные:** userID + name + phone + image + DefaultClass

**Пример JSON**: <br>
`{
"name":null, `<br>` 
"userID":0, `<br>`
"defaultClass":{ `<br>`
"operationOutput":false,
"token":"Operation timed out (Read failed)" `<br>`
}, `<br>`
"phone":null, `<br>`
"image":null, `<br>`
"balance":0.0 `<br>`
}`

#### Метод для смены пароля
**Путь:** /user/chpsd/

**Параметр:** token - токен пользователя <br>
**Параметр:** lastpsd - MD5-хеш предыдущего значения пароля пользователя <br>
**Параметр:** newpsd - MD5-хеш значения пароля пользователя <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `

#### Метод для смены имени пользователя
**Путь:** /user/chname/

**Параметр:** token - токен пользователя <br>
**Параметр:** name - новое имя пользователя <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `

#### Метод для изменения фотографии пользователя
**Путь:** /user/chphoto/

**Параметр:** token - токен пользователя <br>
**Параметр:** photo - новое фото пользователя в формате строки base64 <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `

#### Метод получения пользователя по номеру телефона
**Путь:** /user/getubyphn/

**Параметр:** token - токен пользователя <br>
**Параметр:** phone - телефон пользователя <br>
**Выходные данные:** userID + name + phone + image + DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"name":"Anton", `<br>`
"image":null, `<br>`
"balance":0.0, `<br>`
"phone":"8-920", `<br>`
"userID":2, `<br>`
"defaultClass":{ `<br>`
"operationOutput":true, `<br>`
"token":null `<br>`
} `<br>`
} `

### ConversationController("/conv"):

#### Метод для получения первноначального списка бесед (при входе)
**Путь:** /conv/gets/

**Параметр:** token  - токен пользователя <br>
**Выходные данные:** Dialog()[] + Group()[] + DefaultClass 
//JSON устарел (нету в группах и диалогах поля date)
**Пример JSON**: <br>
`{ `<br>` 
"groups":[ `<br>` 
{ `<br>` 
"name":"DreamTeam", `<br>` 
"adminID":3, `<br>` 
"myBalance":-100.0, `<br>` 
"groupID":1, `<br>` 
"sum":150.0 `<br>` 
},  `<br>` 
{  `<br>` 
"name":"Old Owls Team",  `<br>` 
"adminID":6,  `<br>` 
"myBalance":300.0,  `<br>` 
"groupID":3,  `<br>` 
"sum":300.0  `<br>` 
}  `<br>` 
],  `<br>` 
"dialogs":[  `<br>` 
{  `<br>` 
"userProfile":{  `<br>` 
"name":"Anton",  `<br>` 
"image":null, `<br>` 
"balance":0.0, `<br>` 
"phone":"8-920", `<br>` 
"userID":2, `<br>` 
"defaultClass":{ `<br>` 
"operationOutput":null, `<br>` 
"token":null  `<br>` 
} `<br>` 
}, `<br>` 
"dialogID":1, `<br>` 
"balance":100.0 `<br>` 
}, `<br>` 
{ `<br>` 
"userProfile":{ `<br>` 
"name":"Leonid", `<br>` 
"image":null, `<br>` 
"balance":0.0, `<br>` 
"phone":"8-940", `<br>` 
"userID":4, `<br>` 
"defaultClass":{ `<br>` 
"operationOutput":null, `<br>` 
"token":null `<br>` 
} `<br>` 
}, `<br>` 
"dialogID":2,  `<br>` 
"balance":-200.0 `<br>` 
} `<br>` 
], `<br>` 
"defaultClass":{ `<br>` 
"operationOutput":true,  `<br>` 
"token":"tokenAlex"  `<br>` 
}  `<br>` 
}  `<br>` 

#### Метод для получения дополнительного списка бесед по дате
**Путь:** /conv/gets/

**Параметр:** token  - токен пользователя <br>
**Параметр:** date - дата последнего диалога или группы <br>
**Выходные данные:** Dialog()[] + Group()[] + DefaultClass  //в сумме 20 например, распределения по диалогам и группам неизвестно

#### Метод для подтверждения транзакции
**Путь:** /conv/accepttr/

**Параметр:** token - токен пользователя, который подтверждает транзакцию <br>
**Параметр:** transactionID – ID транзакции <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `


#### Метод для отклонения транзакции
**Путь:** /conv/declinetr/

**Параметр:** token - токен пользователя, который отклоняет транзакцию <br>
**Параметр:** transactionID – ID транзакции <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `

### DialogController("/dialog"):

#### Метод для получения информации по диалогу 
**Путь:** /dialog/get/

**Параметр:** token  - токен пользователя <br>
**Параметр:** dialogID - ID диалога <br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"defaultClass":{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
}, `<br>`
"transactions":[ `<br>`
{ `<br>`
"userID":4, `<br>`
"text":"text6", `<br>`
"date":"2017-06-25",`<br>`
"groupID":0,`<br>`
"cash":0,`<br>`
"dialogID":5,`<br>`
"transactionID":6,`<br>`
"proof":0,`<br>`
"money":50.0`<br>`
}]} `

#### Метод для отправки транзакции
**Путь:** /dialog/sendtr/

**Параметр:** token - токен пользователя, который совершает транзакцию <br>
**Параметр:** dialogID - диалог, в котором он отправил <br>
**Параметр:** money - сумма денег, которую отправили <br>
**Параметр:** cash - наличные это или нет <br>
**Параметр:** text - комментарий к транзакции <br>
**Выходные данные:** DefaultClass + TransactionID + Date //дата совершения транзакции

//JSON устарел, будет поле date ещё
**Пример JSON**: <br>
`{ `<br>`
"id":30, `<br>`
"defaultClass":{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
}
} `

#### Метод для загрузки истории транзакций диалога
**Путь:** /dialog/gettransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** dialog – ID диалога <br>
**Параметр:** transactionID - ID последней транзакции<br>
**Выходные данные:** Transaction[20] + DefaultClass
**Пример JSON**: <br>
`{ `<br>`
"defaultClass":{`<br>`
"operationOutput":true,`<br>`
"token":"tokenAlex"`<br>`
}, `<br>`
"transactions":[`<br>`
{`<br>`
"userID":2,`<br>`
"text":"text4",`<br>`
"date":"2017-06-25",`<br>`
"groupID":0,`<br>`
"cash":0,`<br>`
"dialogID":4,`<br>`
"transactionID":4,`<br>`
"proof":0,`<br>`
"money":1000.0 `<br>`
}]} `


#### Метод для создания диалога с пользователем
**Путь:** /dialog/create/

**Параметр:** token  - токен пользователя <br>
**Параметр:** phone - телефон того, с кем хотим создать диалог <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `

#### Метод для получения новых транзакций
**Путь:** /dialog/getnewtransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** dialogID – ID диалога <br>
**Параметр:** transactionID – самая ранняя транзакция <br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"defaultClass":{`<br>`
"operationOutput":true,`<br>`
"token":"tokenAlex"`<br>`
}, `<br>`
"transactions":[`<br>`
{`<br>`
"userID":2,`<br>`
"text":"text4",`<br>`
"date":"2017-06-25",`<br>`
"groupID":0,`<br>`
"cash":0,`<br>`
"dialogID":4,`<br>`
"transactionID":4,`<br>`
"proof":0,`<br>`
"money":1000.0 `<br>`
}]} `

### GroupController("/group"):

#### Метод для получения группового диалога
**Путь:** /group/get/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID - ID группы <br>
**Выходные данные:** UserProfile[] + Transaction[20] + DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"defaultClass":{`<br>`
"operationOutput":true,`<br>`
"token":"tokenAlex"`<br>`
}, `<br>`
"transactions":[`<br>`
{`<br>`
"userID":2,`<br>`
"text":"text4",`<br>`
"date":"2017-06-25",`<br>`
"groupID":0,`<br>`
"cash":0,`<br>`
"dialogID":4,`<br>`
"transactionID":4,`<br>`
"proof":0,`<br>`
"money":1000.0 `<br>`
}],  `<br>`
"userProfiles": [ `<br>`
                { `<br>`
                "name":"Alex", `<br>`
                "userID":1, `<br>`
                "defaultClass":{ `<br>`
                "operationOutput":null, `<br>`
                "token":null `<br>`
                }, `<br>`
                "image":null, `<br>`
                "phone":"8-910", `<br>`
                "balance":-50.274 `<br>`
                }, `<br>`
                { `<br>`
                "name":"Anton",`<br>`
                "userID":2,`<br>`
                "defaultClass":{`<br>`
                "operationOutput":null, `<br>`
                "token":null `<br>`
                }, `<br>`
                "image":null, `<br>`
                "phone":"8-920", `<br>`
                "balance":-100.326 `<br>`
                }]} `


#### Метод для отправки транзакции
**Путь:** /group/sendtransaction/ 

//Если receiverID > 0, то идет перевод от одного к другому внутри группы и все
//Если receiverID == 0, то мы это зачисляем в групповой баланс и разделяем сумму на всех и пересчитываем баланс всех участников
**Параметр:** token - токен пользователя, который совершает транзакцию <br>
**Параметр:** receiverID - ID пользователя, которому мы отправляем перевод внутри группы
**Параметр:** groupID - диалог, в котором он отправил <br>
**Параметр:** money - сумма денег, которую отправили <br>
**Параметр:** cash - наличные это или нет <br>
**Параметр:** text - комментарий к транзакции <br>
**Выходные данные:** DefaultClass + TransactionID + Date

//JSON устарел (+ поле date возвращается с датой транзакции)
**Пример JSON**: <br>
`{ `<br>`
"id":30, `<br>`
"defaultClass":{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
}
} `

#### Метод для загрузки истории транзакций групповой беседы
**Путь:** /group/gettransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID – ID группы <br>
**Параметр:** transactionID <br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"defaultClass":{`<br>`
"operationOutput":true,`<br>`
"token":"tokenAlex"`<br>`
}, `<br>`
"transactions":[`<br>`
{`<br>`
"userID":2,`<br>`
"text":"text4",`<br>`
"date":"2017-06-25",`<br>`
"groupID":0,`<br>`
"cash":0,`<br>`
"dialogID":4,`<br>`
"transactionID":4,`<br>`
"proof":0,`<br>`
"money":1000.0 `<br>`
}]} `

#### Метод для получения новых транзакций, когда пришло новое сообщение в группе
**Путь:** /group/getnewtransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID – ID группы <br>
**Параметр:** transactionID – самая ранняя транзакция <br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"defaultClass":{`<br>`
"operationOutput":true,`<br>`
"token":"tokenAlex"`<br>`
}, `<br>`
"transactions":[`<br>`
{`<br>`
"userID":2,`<br>`
"text":"text4",`<br>`
"date":"2017-06-25",`<br>`
"groupID":0,`<br>`
"cash":0,`<br>`
"dialogID":4,`<br>`
"transactionID":4,`<br>`
"proof":0,`<br>`
"money":1000.0 `<br>`
}]} `

#### Метод для создания групповой беседы
**Путь:** /group/create/

**Параметр:** token  - токен пользователя <br>
**Параметр:** name - название групповой беседы <br>
**Выходные данные:** GroupID + DefaultClass

**Пример JSON**: <br>
{"id":31,"defaultClass":{"operationOutput":true,"token":"tokenAlex"}}

#### Метод для добавления участника в групповую беседу
**Путь:** /group/add/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы в которую хотим добавить <br>
**Параметр:** phone - телефон того, кого хотим добавить <br>
**Выходные данные:** DefaultClass
**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `
#### Метод для удаления участника из групповой беседы
**Путь:** /group/deluser/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы, из которой хотим удалить <br>
**Параметр:** phone - телефон того, кого хотим удалить <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `

#### Метод для выхода из групповой беседы для юзеров
**Путь:** /group/quit/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы из которой хотим удалиться <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `

//беседу удалить может только Админ
#### Метод для удаления групповой беседы
**Путь:** /group/leave/

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы которую хотим удалить <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
`{ `<br>`
"operationOutput":true, `<br>`
"token":"tokenAlex" `<br>`
} `


