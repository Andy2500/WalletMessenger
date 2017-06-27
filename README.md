# WalletMessenger

### UserController("/user"):

#### Метод для регистрации пользователей
**Путь:** /user/reg/

**Параметр:** phone - телефон пользователя, используется как логин <br>
**Параметр:** name - имя пользователя <br>
**Параметр:** hashpsd - MD5-хеш пароля пользователя <br>
**Выходные данные:** userID + name + phone + image + DefaultClass 
//убран баланс(он нужен только для участников группы, когда мы входим в неё) (ниже также убрано)

#### Метод для авторизации пользователя
**Путь:** /user/log/

**Параметр:** phone - телефон пользователя, используется как логин <br>
**Параметр:** hashpsd - MD5-хеш пароля пользователя <br>
**Выходные данные:** userID + name + phone + image + DefaultClass

#### Метод для смены пароля
**Путь:** /user/chpsd/

**Параметр:** token - токен пользователя <br>
**Параметр:** lastpsd - MD5-хеш предыдущего значения пароля пользователя <br>
**Параметр:** newpsd - MD5-хеш значения пароля пользователя <br>
**Выходные данные:** DefaultClass

#### Метод для смены имени пользователя
**Путь:** /user/chname/

**Параметр:** token - токен пользователя <br>
**Параметр:** name - новое имя пользователя <br>
**Выходные данные:** DefaultClass

#### Метод для изменения фотографии пользователя
**Путь:** /user/chphoto/

**Параметр:** token - токен пользователя <br>
**Параметр:** photo - новое фото пользователя в формате строки base64 <br>
**Выходные данные:** DefaultClass


#### Метод получения пользователя по номеру телефона
**Путь:** /user/getubyphn/

**Параметр:** token - токен пользователя <br>
**Параметр:** phone - телефон пользователя <br>
**Выходные данные:** userID + name + phone + image + DefaultClass

### ConversationController("/conv"):

#### Метод для получения списка бесед
**Путь:** /conv/gets/

**Параметр:** token  - токен пользователя <br>
**Выходные данные:** Dialog()[] + Group()[] + DefaultClass 

#### Метод для подтверждения транзакции
**Путь:** /conv/accepttr/

**Параметр:** token - токен пользователя, который подтверждает транзакцию <br>
**Параметр:** transcationID – ID транзакции <br>

**Выходные данные:** DefaultClass

#### Метод для отклонения транзакции
**Путь:** /conv/declinetr/

**Параметр:** token - токен пользователя, который отклоняет транзакцию <br>
**Параметр:** transcationID – ID транзакции <br>

**Выходные данные:** DefaultClass

### DialogController("/dialog"):

#### Метод для получения информации по диалогу 
**Путь:** /dialog/get/

**Параметр:** token  - токен пользователя <br>
**Параметр:** dialogID - ID диалога <br>
**Выходные данные:** Transaction[20] + DefaultClass

#### Метод для отправки транзакции
**Путь:** /dialog/sendtr/

**Параметр:** token - токен пользователя, который совершает транзакцию <br>
**Параметр:** dialogID - диалог, в котором он отправил <br>
**Параметр:** money - сумма денег, которую отправили <br>
**Параметр:** cash - наличные это или нет <br>
**Параметр:** text - комментарий к транзакции <br>
**Выходные данные:** DefaultClass + TransactionID

#### Метод для загрузки истории транзакций диалога
**Путь:** /dialog/gettransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** dialog – ID диалога <br>
**Параметр:** transactionID - ID последней транзакции<br>
**Выходные данные:** Transaction[20] + DefaultClass

#### Метод для создания диалога с пользователем
**Путь:** /dialog/create/

**Параметр:** token  - токен пользователя <br>
**Параметр:** phone - телефон того, с кем хотим создать диалог <br>
**Выходные данные:** DefaultClass

#### Метод для получения новых транзакций
**Путь:** /dialog/getnewtransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** dialogID – ID диалога <br>
**Параметр:** transactionID – самая ранняя транзакция <br>
**Выходные данные:** Transaction[20] + DefaultClass

### GroupController("/group"):

#### Метод для получения группового диалога
**Путь:** /group/get/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID - ID группы <br>
**Выходные данные:** UserProfile[] + Transaction[20] + DefaultClass

#### Метод для отправки транзакции
**Путь:** /group/sendtransaction/ 

**Параметр:** token - токен пользователя, который совершает транзакцию <br>
**Параметр:** groupID - диалог, в котором он отправил <br>
**Параметр:** money - сумма денег, которую отправили <br>
**Параметр:** cash - наличные это или нет <br>
**Параметр:** text - комментарий к транзакции <br>

**Выходные данные:** DefaultClass + TransactionID

#### Метод для загрузки истории транзакций групповой беседы
**Путь:** /conv/gettransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID – ID группы <br>
**Параметр:** transactionID <br>
**Выходные данные:** Transaction[20] + DefaultClass

#### Метод для получения новых транзакций, когда пришло новое сообщение в группе
**Путь:** /group/getnewtransactions/

**Параметр:** token - токен пользователя <br>
**Параметр:** groupID – ID группы <br>
**Параметр:** transactionID – самая ранняя транзакция <br>
**Выходные данные:** Transaction[20] + DefaultClass

//но Transaction[] будет пустой, и в юзерах только создатель будет
//Создатель является админом в беседе!
#### Метод для создания групповой беседы
**Путь:** /group/create/

**Параметр:** token  - токен пользователя <br>
**Параметр:** name - название групповой беседы <br>
**Выходные данные:** GroupID + DefaultClass

//Добавлять может все могут? или только админ
#### Метод для добавления участника в групповую беседу
**Путь:** /group/add/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы в которую хотим добавить <br>
**Параметр:** phone - телефон того, кого хотим добавить <br>
**Выходные данные:** DefaultClass

//Удалять точно только админ может
#### Метод для удаления участника из групповой беседы
**Путь:** /group/deluser/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы, из которой хотим удалить <br>
**Параметр:** phone - телефон того, кого хотим удалить <br>
**Выходные данные:** DefaultClass

//выйти каждый сам может
#### Метод для выхода из групповой беседы для юзеров
**Путь:** /group/quit/ 

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы из которой хотим удалиться <br>
**Выходные данные:** DefaultClass


//беседу удалить может только Админ
#### Метод для удаления групповой беседы
**Путь:** /group/leave/

**Параметр:** token  - токен пользователя <br>
**Параметр:** groupID - id группы которую хотим удалить <br>
**Выходные данные:** DefaultClass

//Так кратко: все методы указанные выше работают.
НО это не все, так как будут добавлены ещё дополнительные методы - например запрос неподтвержденных транзакций
//Надо будет продумать как будет удобнее это сделать -> я думаю через отдельный экран какой-нить 
типо "оповещения или неподтвержденные транзакции".























