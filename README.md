# WalletMessenger

###UserController("/user"):

####Метод для регистрации пользователей
**Путь:** /user/reg/

**Параметр:** phone - телефон пользователя, используется как логин <br>
**Параметр:** name - имя пользователя <br>
**Параметр:** hashpsd - MD5-хеш пароля пользователя <br>
**Выходные данные:** DefaultClass

####Метод для авторизации пользователя
**Путь:** /user/log/

**Параметр:** phone - телефон пользователя, используется как логин <br>
**Параметр:** hashpsd - MD5-хеш пароля пользователя <br>
**Выходные данные:** User

####Метод для смены пароля
**Путь:** /user/chpsd/

**Параметр:** token - токен пользователя <br>
**Параметр:** lastpsd - MD5-хеш предыдущего значения пароля пользователя <br>
**Параметр:** newpsd - MD5-хеш значения пароля пользователя <br>
**Выходные данные:** DefaultClass

####Метод для смены номера телефона
**Путь:** /user/chphone/

**Параметр:** token - токен пользователя <br>
**Параметр:** lastPhone - предыдущее значения телефона пользователя <br>
**Параметр:** newPhone - новое значения телефона пользователя <br>
**Выходные данные:** DefaultClass

####Метод для смены имени пользователя
**Путь:** /user/chname/

**Параметр:** token - токен пользователя <br>
**Параметр:** name - новое имя пользователя <br>
**Выходные данные:** DefaultClass

####Метод для изменения фотографии пользователя
**Путь:** /user/chphoto/

**Параметр:** token - токен пользователя <br>
**Параметр:** photo - новое фото пользователя в формате строки base64 <br>
**Выходные данные:** DefaultClass


####Метод получения пользователя по номеру телефона
**Путь:** /user/getubyphn/

**Параметр:** token - токен пользователя <br>
**Параметр:** phone - телефон пользователя <br>
**Выходные данные:** User 

###ConversationController("/conv"):

####Метод для получения списка бесед
**Путь:** /conv/gets/

**Параметр:** token  - токен пользователя <br>
**Параметр:** conversationID - ID диалога <br>
**Выходные данные:** Dialog[] + Group[] + DefaultClass 

####Метод для загрузки истории транзакций
**Путь:** /conv/gettransactions/

**Параметр:** conversationID – ID беседы
**Параметр:** page  - номер страницы с набором транзакций <br>
**Выходные данные:** Transaction[20] + DefaultClass

####Метод для получения прошлых транзакций, когда пришло новое сообщение
**Путь:** /conv/getnewtransactions/

**Параметр:** conversationID – ID беседы <br>
**Параметр:** transactionID – все транзакции после этой <br>
**Выходные данные:** Transaction[] + DefaultClass

####Метод для подтверждения транзакции
**Путь:** /conv/accepttr/

**Параметр:** token - токен пользователя, который подтверждает транзакцию <br>
**Параметр:** transcationID – ID транзакции <br>

**Выходные данные:** DefaultClass

####Метод для отклонения транзакции
**Путь:** /conv/declinetr/

**Параметр:** token - токен пользователя, который отклоняет транзакцию <br>
**Параметр:** transcationID – ID транзакции <br>

**Выходные данные:** DefaultClass

###DialogController("/dialog"):

####Метод для получения информации по диалогу 
**Путь:** /dialog/get/

**Параметр:** token  - токен пользователя <br>
**Параметр:** conversationID - ID диалога <br>
**Выходные данные:** User + Transaction[20] + DefaultClass

####Метод для отправки транзакции
**Путь:** /dialog/sendtr/

**Параметр:** token - токен пользователя, который совершает транзакцию <br>
**Параметр:** dialogID - диалог, в котором он отправил <br>
**Параметр:** money - сумма денег, которую отправили <br>
**Параметр:** cash - наличные это или нет <br>
**Параметр:** text - комментарий к транзакции <br>
**Выходные данные:** DefaultClass

###GroupController("/group"):

####Метод для получения группового диалога
**Путь:** /group/get/

**Параметр:** token - токен пользователя <br>
**Параметр:** dialogID - ID группового диалога <br>
**Выходные данные:** User[] +AdminID + Name + Sum 

####Метод для отправки транзакции
**Путь:** /group/sendtransaction/ 

Параметры:

**Параметр:** token - токен пользователя, который совершает транзакцию <br>
**Параметр:** groupID - диалог, в котором он отправил <br>
**Параметр:** money - сумма денег, которую отправили <br>
**Параметр:** cash - наличные это или нет <br>
**Параметр:** text - комментарий к транзакции <br>

**Выходные данные:** DefaultClass





























