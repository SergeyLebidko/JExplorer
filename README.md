# JExplorer
Простой файловый менеджер на Java

Этот проект - моя вторая попытка сделать файловый менеджер на Java. Первая попытка [JManager](https://github.com/SergeyLebidko/JManager)
представляла собой двухпанельный менеджер по образу Total Commander'а. Теперь я захотел реализовать другой внешний вид, больше
похожий на проводник Windows или аналогичные приложения под Linux. Плюс хотелось сделать код приложения более структурированным и 
более понятным, в свете накопленного мной на сегодняшний момент опыта.

Внешний вид приложения (относительно прежней моей работы, см. ссылку выше) стал более аккуратным. Появилась полноценная строка меню.
Реализована полноценная панель инструментов. Удалось реализовать адресную строку, каталоги в которой отображаются в виде кнопок; таким 
образом можно быстро перейти вверх по дереву каталогов на любое количество уровней. Был реализован помимо табличного вида также 
вид "плитка" с большими и маленькими значками и возможностью быстрого переключения между ними.

Программный код разбит на несколько пакетов, иерархически вложенных друг в друга:

1. Пакет jexplorer
   
   Класс MainClass. С него начинается выполнение программы. Также в MainClass содержатся в виде статических полей ссылки на все
   основные, используемые программой объекты. Остальные классы по ходу своего выполнения обращаются к MainClass за ссылками на 
   необходимые для работы объекты.
   
   Класс GUI. Пожалуй, самый объемный класс программы. Как понятно уже из названия - создает интерфейс и все объекты интерфейса.
   Также привязывает к элементам интерфейса обработчики событий.
   
2. Пакет guiclasses. 
   
   Содержит ряд пакетов и классов, реализующих основные элементы интерфейса: адресную строку (класс AdressPane); панель, отображающую
   перечень локальных дисков и ссылку на каталог пользователя (класс RootPointExplorerPane); панель, реализующую табличный вид (классы 
   TableExplorerPane, TableExplorerModel, TableExplorerCellRenderer, TableExplorerHeaderRenderer); панель плиточного вида (классы
   TileExplorerPane и Selector), интерфейс ExplorerPane, которому должны удовлетворять панели. Использование общего для панелей 
   интерфейса позволило значительно облегчить реализацию переключения между плиточным и табличным видами в приложении.
   
   Отельно хочу рассказать о классах TileExplorerPane и Selector. Java Swing не содержаит компонентов, которые позволили бы реализовать
   плиточный вид также легко как табличный и управлять возможностью выделения объектов в таком виде также легко как в таблицах JTable.
   Поэтому я сам написал соответствующий плиточный вид (но не как наследника класса Component, а как отдельный класс, возвращающий 
   соответсвующим образом настроенный и скомбинированный из других компонентов компонент). Для плиточного вида был также написан (в виде
   внутреннего класса) специальный менеджер расположения (AdaptiveGridLayout), который перераспределяет плитки в окне зависимости от
   размеров окна и выбранного режима (большие плитки или маленькие плитки). Класс Selector необходим для реализации выбора элементов 
   с помощью мыши (при этом можно задействовать кнопки Shift и Ctrl также как и в Windows).
   
3. Пакет fileexplorerclasses.

   В этом пакете содержатся классы, необходимые для перемещения по файловой системе, получения свойств отдельных каталогов и файлов,
   а также для сортировки по различным признакам (по имени, по размеру и т.д.) выводимого на экран содержимого каталогов. Классы GUI,
   таким образом, получают уже готовые, предварительно отсортированные (классом FileSorter) по выбранному признаку списки файлов 
   и каталогов и занимаются лишь только выводом их на экран. Типы сортировки (по имени, по размеру, по расширению и т.д.) и группировки 
   (по-возрастанию, по-убыванию) описаны в перечислениях SortTypes и SortOrders. Перечисление FileTypes позволяет связывать типы файлов
   (по крайней мере, самые распространенные) с иконками файлов. Дело в том, что мне не удалось найти способ получения из Java иконок
   для файлов таких, какими они отображаются в Windows. Существующие способы (по караней мере, известные мне) позволяют получать иконки
   размером только 16х16 пикселей, что делает их абсолютно непригоднымии для нормальной реализации плиточного вида. Поэтому мне пришлось
   применить свой набор иконок для самых распространенных типов файлов. И перечисление FileTypes позволяет быстро сопоставлять тип файла
   и соответсвующую ему иконку.
   
4. Пакет fileutilities.

   Содержит классы, необходимые для реализации действий с файлами и каталогами: создание каталогов (класс DirectoryCreator), 
   переименование файлов и каталогов (класс Renamer), их удаление (класс Remover), просмотр их свойств (класс PropertyReceiver),
   копирование и перемещение (класс Copier). Также в данном пакете находятся классы Clipboard и ClipboardContent, реализующие в моем 
   приложении буфер обмена, необходимый для операций копирования и вставки. Класс ResultSet - пожалуй самый спорный в этом пакете.
   Мне необходим был класс, в котором можно было бы собирать и возвращать в вызывающий код всю информацию об операциях с файлами и
   папками и некоторых ошибках, возникающих при выполнении данных операций. Возвращать данные об ошибках путем генерирования Exception
   в данном случае бывает далеко не всегда удобно. Например, программа получила задание на копирование 10 каталогов, но к двум из них
   она не может получить доступ, а к остальным - может. Простое генерирование Exception в этом случае прервало бы процесс копирования, 
   а использование ResultSet позволяет затем вывести пользователю отчет о проделанной работе и неудачном доступе к файлам. К тому же
   через ResultSet удобно передавать, к примеру, отчет о свойствах групп объектов, организованный по принципу "имя свойства"*"значение".
   Да, возможно, подобный "многофункциональный" класс и нарушает некоторые принципы чистого кода (испольльзуется и для передачи логов
   ошибок и для передачи наборов свойств), но мне пришлось пойти на этот компромисс, чтобы сделать код... более единообразным что-ли..
   
P.S. Небольшое замечание по поводу ведения мной github-аккаунта. Раньше я считал, что код на гите должен просто быть. Что история 
коммитов не так уж важна и можно вообще ограничиваться одним коммитом и простейшим описанием своего приложения. Но теперь я понял,
что ошибался и по возможности буду впредь стараться выкладывать на гитхаб всю историю коммитов и снабжать свои репозитории 
вменяемым readme и скриншотами. Также у меня пока не очень получается делать свои коммиты в достаточной мере "атомарными". 
Так что, еще есть к чему стремиться))

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_1.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_2.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_3.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_4.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_5.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_6.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_7.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_8.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_9.jpg)

![ScreenShot](https://github.com/SergeyLebidko/JExplorer/blob/master/screenshots/screen_10.jpg)
   