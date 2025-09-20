# VFS Emulator - Этап 4: Основные команды

Эмулятор виртуальной файловой системы с поддержкой основных UNIX-подобных команд.

## 📋 Требования этапа 4

Реализована поддержка основных команд:

- ✅ Логика для `ls` и `cd`
- ✅ Новая команда `date` - вывод текущей даты и времени
- ✅ Новая команда `clear` - очистка экрана
- ✅ Новая команда `tail` - вывод последних строк файла
- ✅ Тестовые скрипты для всех команд
- ✅ Обработка ошибок для новых команд

```bash
# Компиляция всех файлов
javac -d out src/Main.java src/ConsoleEmulator.java src/CommandManager.java src/ConsoleUI.java src/EmulatorException.java src/filesystem/VFS.java src/filesystem/PathResolver.java src/filesystem/VFSDirectory.java src/filesystem/VFSFile.java src/filesystem/VFSNode.java src/filesystem/exceptions/VFSException.java src/filesystem/exceptions/VFSOperationException.java src/filesystem/exceptions/VFSPathException.java

# Запуск
java -cp out Main
```