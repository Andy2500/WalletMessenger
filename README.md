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
{  <br>
"name":"Alex2",  <br>
"userID":8,  <br> 
"defaultClass":{  <br>
"token":"0df23be331dc7fc1cd9372cb7edfb660",  <br>
"operationOutput":true  <br>
},  <br>
"image":null,  <br>
"phone":"8-002",  <br>
"balance":0.0  <br>
}  <br>

#### Метод для авторизации пользователя
**Путь:** /user/log/

**Параметр:** phone - телефон пользователя, используется как логин <br>
**Параметр:** hashpsd - MD5-хеш пароля пользователя <br>
**Выходные данные:** userID + name + phone + image + DefaultClass

**Пример JSON**: <br>
{  <br>
"name":"Alex2",  <br>
"userID":8,  <br> 
"defaultClass":{  <br>
"token":"0df23be331dc7fc1cd9372cb7edfb660",  <br>
"operationOutput":true  <br>
},  <br>
"image":null,  <br>
"phone":"8-002",  <br>
"balance":0.0  <br>
}  <br>

#### Метод для смены пароля
**Путь:** /user/chpsd/

**Параметр:** token - токен пользователя <br>
**Параметр:** lastpsd - MD5-хеш предыдущего значения пароля пользователя <br>
**Параметр:** newpsd - MD5-хеш значения пароля пользователя <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для смены имени пользователя
**Путь:** /user/chname/

**Параметр:** token - токен пользователя <br>
**Параметр:** name - новое имя пользователя <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для изменения фотографии пользователя
**Путь:** /user/chphoto/

**Параметр:** token - токен пользователя <br>
**Параметр:** photo - новое фото пользователя в формате строки base64 <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод получения пользователя по номеру телефона
**Путь:** /user/getubyphn/

**Параметр:** token - токен пользователя <br>
**Параметр:** phone - телефон пользователя <br>
**Выходные данные:** userID + name + phone + image + DefaultClass

**Пример JSON**: <br>
{ <br>
"name":"Anton", <br>
"userID":2, <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
}, <br>
"image":null, <br>
"phone":"8920", <br>
"balance":0.0 <br>
} <br>

### ConversationController("/conv"):

#### Метод для получения первноначального списка бесед (20 самых новых по дате)
**Путь:** /conv/getconv/

**Параметр:** token  - токен пользователя <br>
**Выходные данные:** Dialog()[] + Group()[] + DefaultClass  // 20 штук, может иметь менее 20 или вовсе быть пустым (!)

#### Метод для получения списка бесед по дате (20 самых новых по дате)
**Путь:** /conv/getconvhist/

**Параметр:** token  - токен пользователя <br>
**Параметр:** date  - дата самого раннего сообщения (формат long) <br>
**Выходные данные:** Dialog()[] + Group()[] + DefaultClass  // 20 штук, может иметь менее 20 или вовсе быть пустым (!)


#### Метод для получения первоначального списка диалогов (при входе)
**Путь:** /conv/getdialogs/

**Параметр:** token  - токен пользователя <br>
**Выходные данные:** Dialog()[] + DefaultClass  // 20 штук, может иметь менее 20 или вовсе быть пустым (!)

**Пример JSON**: <br>
{ <br>
"dialogs":[ <br>
{ <br>
"date":1498745540791, <br>
"userID":3, <br>
"dialogID":9, <br>
"userProfile":{ <br>
"name":"Andrey", <br>
"userID":3, <br>
"defaultClass":{ <br>
"token":null, <br>
"operationOutput":null <br>
}, <br>
"image":null, <br>
"phone":"8930", <br>
"balance":0.0 <br>
}, <br>
"balance":0.0 <br>
}, <br>
{ <br>
"date":1498744832344, <br>
"userID":5, <br>
"dialogID":3, <br>
"userProfile":{ <br>
"name":"Vika", <br>
"userID":5, <br>
"defaultClass":{ <br>
"token":null, <br>
"operationOutput":null <br>
}, <br>
"image":null, <br>
"phone":"7950", <br>
"balance":0.0 <br>
}, <br>
"balance":300.0 <br>
} <br>
], <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для получения первоначального списка групп (при входе)
**Путь:** /conv/getgroups/

**Параметр:** token  - токен пользователя <br>
**Выходные данные:** Group()[] + DefaultClass  // 20 штук, может иметь менее 20 или вовсе быть пустым (!)

**Пример JSON**: <br>
{ <br>
"groups":[ <br>
{ <br>
"name":"Большая Confa ", <br>
"date":1498757926606, <br>
"groupID":15, <br>
"sum":0.0, <br>
"myBalance":0.0, <br>
"adminID":1 <br>
}, <br>
{ <br>
"name":"Большая Confa ", <br>
"date":1498757505076, <br>
"groupID":14, <br>
"sum":0.0, <br>
"myBalance":0.0, <br>
"adminID":1 <br>
} <br>
], <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для получения дополнительного диалогов по дате
**Путь:** /conv/gethistdialogs/

**Параметр:** token  - токен пользователя <br>
**Параметр:** date - дата последнего диалога, формат long  <br>
**Выходные данные:** Dialog()[] + DefaultClass  // 20 штук, может иметь менее 20 или вовсе быть пустым (!)

**Пример JSON**: <br>
{ <br>
"dialogs":[ <br>
{ <br>
"date":1498547903614, <br>
"userID":4, <br>
"dialogID":2, <br>
"userProfile":{ <br>
"name":"Leonid", <br>
"userID":4, <br>
"defaultClass":{ <br>
"token":null, <br>
"operationOutput":null <br>
}, <br>
"image":null, <br>
"phone":"7940", <br>
"balance":0.0 <br>
}, <br>
"balance":-200.0 <br>
}, <br>
{ <br>
"date":1498547843614, <br>
"userID":2, <br>
"dialogID":1, <br>
"userProfile":{ <br>
"name":"Anton", <br>
"userID":2, <br>
"defaultClass":{ <br>
"token":null, <br>
"operationOutput":null <br>
}, <br>
"image":null, <br>
"phone":"8920", <br>
"balance":0.0 <br>
}, <br>
"balance":100.0 <br>
} <br>
], <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для получения дополнительного списка бесед по дате
**Путь:** /conv/gethistgroups/

**Параметр:** token  - токен пользователя <br>
**Параметр:** date - дата последней группы формат long  <br>
**Выходные данные:** Group()[] + DefaultClass  // 20 штук, может иметь менее 20 или вовсе быть пустым (!)

**Пример JSON**: <br>
{ <br>
"groups":[ <br>
{ <br>
"name":"Old Owls Team", <br>
"date":1498555043614, <br>
"groupID":3, <br>
"sum":300.0, <br>
"myBalance":300.0, <br>
"adminID":6 <br>
}, <br>
{ <br>
"name":"Fintech Cup", <br>
"date":1498551443614, <br>
"groupID":2, <br>
"sum":-50.0, <br>
"myBalance":-50.0, <br>
"adminID":3 <br>
} <br>
], <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для подтверждения транзакции
**Путь:** /conv/accepttr/

**Параметр:** token - токен пользователя, который подтверждает транзакцию <br>
**Параметр:** transactionID – ID транзакции <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>


#### Метод для отклонения транзакции
**Путь:** /conv/declinetr/

**Параметр:** token - токен пользователя, который отклоняет транзакцию <br>
**Параметр:** transactionID – ID транзакции <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>`

### DialogController("/dialog"):

#### Метод для получения информации по диалогу 
**Путь:** /dialog/get/

**Параметр:** token  - токен пользователя <br>
**Параметр:** dialogID - ID диалога <br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
}, <br>
"transactions":[ <br>
{ <br>
"text":"text3", <br>
"date":1498382243614, <br>
"groupID":0, <br>
"cash":0, <br>
"proof":0, <br>
"money":300.0, <br>
"userID":5, <br>
"dialogID":3, <br>
"receiverID":0, <br>
"transactionID":3 <br>
}, <br>
{ <br>
"text":"trans29", <br>
"date":1498565063614, <br>
"groupID":0, <br>
"cash":1, <br>
"proof":0, <br>
"money":500.0, <br>
"userID":1, <br>
"dialogID":3, <br>
"receiverID":0, <br>
"transactionID":29 <br>
} <br>
] <br>
} <br>

#### Метод для отправки транзакции
**Путь:** /dialog/sendtr/

**Параметр:** token - токен пользователя, который совершает транзакцию <br>
**Параметр:** dialogID - диалог, в котором он отправил <br>
**Параметр:** money - сумма денег, которую отправили <br>
**Параметр:** cash - наличные это или нет <br>
**Параметр:** text - комментарий к транзакции <br>
**Выходные данные:** DefaultClass + TransactionID + Date //дата совершения транзакции

**Пример JSON**: <br>
{ <br>
"id":39, <br>
"date":1498744832344, <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для загрузки истории транзакций диалога
**Путь:** /dialog/gettransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** dialog – ID диалога <br>
**Параметр:** transactionID - ID последней транзакции<br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br> 
"operationOutput":true <br>
}, <br>
"transactions":[ <br>
{ <br>
"text":"text4", <br>
"date":1498385843614, <br>
"groupID":0, <br>
"proof":0, <br>
"cash":0, <br>
"money":1000.0, <br>
"userID":2, <br>
"transactionID":4, <br>
"receiverID":0, <br>
"dialogID":4 <br>
} <br>
] <br>
} <br>


#### Метод для создания диалога с пользователем
**Путь:** /dialog/create/

**Параметр:** token  - токен пользователя <br>
**Параметр:** phone - телефон того, с кем хотим создать диалог <br>
**Выходные данные:** DefaultClass + Date //дата создания это

**Пример JSON**: <br>
{<br>
"id":9,<br>
"date":1498745540791,<br>
"defaultClass":{<br>
"token":"tokenAlex",<br>
"operationOutput":true<br>
}<br>
}<br>

#### Метод для получения новых транзакций
**Путь:** /dialog/getnewtransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** dialogID – ID диалога <br>
**Параметр:** transactionID – самая ранняя транзакция <br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
}, <br>
"transactions":[ <br>
{ <br>
"text":"text4", <br>
"date":1498385843614, <br>
"groupID":0, <br>
"proof":0, <br>
"cash":0, <br>
"money":1000.0, <br>
"userID":2, <br>
"transactionID":4, <br>
"receiverID":0, <br>
"dialogID":4 <br>
} <br>
] <br>
} <br>

### GroupController("/group"):

#### Метод для получения группового диалога
**Путь:** /group/get/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID - ID группы <br>
**Выходные данные:** UserProfile[] + Transaction[20] + DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
}, <br>
"userProfiles":[ <br>
{ <br>
"name":"NewName space", <br>
"defaultClass":{ <br>
"token":null, <br>
"operationOutput":null <br>
}, <br>
"userID":1, <br>
"balance":584.82, <br>
"image":null, <br>
"phone":"8910" <br>
}, <br>
{ <br>
"name":"Anton", <br>
"defaultClass":{ <br>
"token":null, <br>
"operationOutput":null <br>
}, <br>
"userID":2, <br>
"balance":-509.97, <br>
"image":null, <br>
"phone":"8920" <br>
}, <br>
{ <br>
"name":"Andrey", <br>
"defaultClass":{ <br>
"token":null, <br>
"operationOutput":null <br>
}, <br>
"userID":3, <br>
"balance":-24.91, <br>
"image":null, <br>
"phone":"8930" <br>
}, <br>
{ <br>
"name":"Leonid", <br>
"defaultClass":{ <br>
"token":null, <br>
"operationOutput":null <br>
}, <br>
"userID":4, <br>
"balance":-49.940002, <br>
"image":null, <br>
"phone":"7940" <br>
} <br>
], <br>
"transactions":[ <br>
{ <br>
"text":"groupText11", <br>
"date":1498478903614, <br>
"groupID":1, <br>
"money":10.0, <br>
"proof":1, <br>
"cash":1, <br>
"userID":1, <br>
"receiverID":0, <br>
"transactionID":10, <br>
"dialogID":0 <br>
}, <br>
{ <br>
"text":"groupText12", <br>
"date":1498484063614, <br>
"groupID":1, <br>
"money":25.0, <br>
"proof":0, <br>
"cash":0, <br>
"userID":2, <br>
"receiverID":0, <br>
"transactionID":11, <br>
"dialogID":0 <br>
}, <br>
{ <br>
"text":"groupText13", <br>
"date":1498487363614, <br>
"groupID":1, <br>
"money":100.0, <br>
"proof":0, <br>
"cash":0, <br>
"userID":3, <br>
"receiverID":0, <br>
"transactionID":12, <br>
"dialogID":0 <br>
} <br>
] <br>
} <br>


#### Метод для отправки транзакции
**Путь:** /group/sendtr/ 

//Если receiverID > 0, то идет перевод от одного к другому внутри группы и все <br>
//Если receiverID == 0, то мы это зачисляем в групповой баланс и разделяем сумму на всех и пересчитываем баланс всех участников <br>
**Параметр:** token - токен пользователя, который совершает транзакцию <br>
**Параметр:** receiverID - ID пользователя, которому мы отправляем перевод внутри группы <br>
**Параметр:** groupID - диалог, в котором он отправил <br>
**Параметр:** money - сумма денег, которую отправили <br>
**Параметр:** cash - наличные это или нет <br>
**Параметр:** text - комментарий к транзакции <br>
**Выходные данные:** DefaultClass + TransactionID + Date <br>

**Пример JSON**: <br>
{ <br>
"id":40, <br>
"date":1498746058540, <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} `  <br>

#### Метод для загрузки истории транзакций групповой беседы
**Путь:** /group/gettransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID – ID группы <br>
**Параметр:** transactionID <br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
{<br>
"defaultClass":{<br>
"token":"tokenAlex",<br>
"operationOutput":true<br>
},<br>
"transactions":[<br>
{<br>
"text":"groupText11",<br>
"date":1498478903614,<br>
"groupID":1,<br>
"money":10.0,<br>
"proof":1,<br>
"cash":1,<br>
"userID":1,<br>
"receiverID":0,<br>
"transactionID":10,<br>
"dialogID":0<br>
}<br>
]<br>
}<br>

#### Метод для получения новых транзакций, когда пришло новое сообщение в группе
**Путь:** /group/getnewtransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID – ID группы <br>
**Параметр:** transactionID – самая ранняя транзакция <br>
**Выходные данные:** Transaction[20] + DefaultClass

**Пример JSON**: <br>
{<br>
"defaultClass":{<br>
"token":"tokenAlex",<br>
"operationOutput":true<br>
},<br>
"transactions":[<br>
{<br>
"text":"groupText11",<br>
"date":1498478903614,<br>
"groupID":1,<br>
"money":10.0,<br>
"proof":1,<br>
"cash":1,<br>
"userID":1,<br>
"receiverID":0,<br>
"transactionID":10,<br>
"dialogID":0<br>
}<br>
]<br>
}<br>

#### Метод для создания групповой беседы с одним участником(админ)
**Путь:** /group/create/

**Параметр:** token  - токен пользователя <br>
**Параметр:** name - название групповой беседы <br>
**Выходные данные:** GroupID + DefaultClass + Date //дата создания группы

**Пример JSON**: <br>
{<br>
"id":6,<br>
"date":1498746328667,<br>
"defaultClass":{<br>
"token":"tokenAlex",<br>
"operationOutput":true<br>
}<br>
}<br>

#### Метод для создания групповой беседы с группой участников
**Путь:** /group/createwithusers/

**Параметр:** token  - токен пользователя <br>
**Параметр:** name - название групповой беседы <br>
//Очень строгий формат строки! Никаких пробелов вообще! телефоны строго через запятую, в начале и в конце запятых нет: <br>
//Пример:   "8913,8150,4444,56774,12312124,0001" -  без кавычек соответственно. <br>
**Параметр:** phones -  строка с мобильными телефонами других пользователей в соответствующем формате <br>

**Выходные данные:** GroupID + DefaultClass + Date //дата создания группы <br>

**Пример JSON**: <br>
{<br>
"id":6,<br>
"date":1498746328667,<br>
"defaultClass":{<br>
"token":"tokenAlex",<br>
"operationOutput":true<br>
}<br>
}<br>

#### Метод для добавления участника в групповую беседу
**Путь:** /group/add/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы в которую хотим добавить <br>
**Параметр:** phone - телефон того, кого хотим добавить <br>
**Выходные данные:** DefaultClass
**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для удаления участника из групповой беседы
**Путь:** /group/deluser/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы, из которой хотим удалить <br>
**Параметр:** phone - телефон того, кого хотим удалить <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

#### Метод для выхода из групповой беседы для юзеров
**Путь:** /group/quit/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы из которой хотим удалиться <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>

//беседу удалить может только Админ
#### Метод для удаления групповой беседы
**Путь:** /group/leave/

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы которую хотим удалить <br>
**Выходные данные:** DefaultClass

**Пример JSON**: <br>
{ <br>
"defaultClass":{ <br>
"token":"tokenAlex", <br>
"operationOutput":true <br>
} <br>
} <br>


